package application;

import java.awt.BasicStroke;
import java.awt.Dimension;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.net.ntp.TimeStamp;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.fx.ChartViewer;
import org.jfree.chart.plot.CombinedDomainXYPlot;
import org.jfree.chart.plot.Marker;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.CandlestickRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.time.Minute;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.DefaultHighLowDataset;
import org.jfree.data.xy.OHLCDataset;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.knowm.xchange.Exchange;
import org.ta4j.core.Bar;
import org.ta4j.core.BaseBar;
import org.ta4j.core.BaseStrategy;
import org.ta4j.core.BaseTimeSeries;
import org.ta4j.core.BaseTradingRecord;
import org.ta4j.core.Decimal;
import org.ta4j.core.Indicator;
import org.ta4j.core.Rule;
import org.ta4j.core.Strategy;
import org.ta4j.core.TimeSeries;
import org.ta4j.core.Trade;
import org.ta4j.core.TradingRecord;
import org.ta4j.core.indicators.StochasticOscillatorKIndicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.indicators.helpers.MedianPriceIndicator;
import org.ta4j.core.trading.rules.CrossedDownIndicatorRule;
import org.ta4j.core.trading.rules.CrossedUpIndicatorRule;
import org.ta4j.core.trading.rules.IsEqualRule;
import org.ta4j.core.trading.rules.OverIndicatorRule;
import org.ta4j.core.trading.rules.UnderIndicatorRule;

import controllers.BacktestController;
import controllers.DashboardController;
import controllers.IndicatorMaps;
import controllers.Indicators;
import controllers.NTPTime;
import controllers.Person;
import controllers.TradingRules;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class LiveTrading implements Runnable {
	public static JFreeChart chart;
	private static ChartViewer viewer;
	private JSONObject json;
	private controllers.DashboardController.Person person;
	private String base;
	private String exchangestring;
	private String alt;
	private Exchange exchange;
	private LocalDate timestart;
	private boolean firstrun = true;
	private ObservableList<Person> dataentry;
	private ObservableList<Person> dataexit;
	private Rule totalentryrule;
	private Rule totalexitrule;
	private Strategy tradingstrategy;
	private String timeframe;
	private long lasttime;
	private boolean cancled=false;
	private int maxtimeframe=0;
	private sTime STime;
	private double volume;
	private List<Bar> Bars;
	private ArrayList<Bar> ticksarray;
	public static TimeSeries series;
	public static TradingRecord tradingRecord;
	public LiveTrading(JSONObject json, ObservableList<Person> dataentry, ObservableList<Person> dataexit) throws JSONException {
		this.json = json;
		this.dataentry = dataentry;
		this.dataexit = dataexit;
	}
	
	public void run() {
		try {
    		DashboardController dash = new DashboardController();
    		person = dash.newOrder(json);
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
    	this.base = json.getString("base");
		this.alt = json.getString("alt");
		this.exchangestring = json.getString("Exchanges");
		this.exchange = Exchanges.exchangemap.get(exchangestring);
		this.timeframe = json.getString("Timeframe");
		this.volume = Double.parseDouble(json.getString("volume"));
		JSONObject prevlive = json;
		prevlive.put("request", "prevlive");
		
		sTime STime = new sTime();
		this.STime=STime;
		String[] servers = new String[] {"pool.ntp.org","0.pool.ntp.org"};
		//String[] servers = new String[] {"pool.ntp.org","0.pool.ntp.org"};
		TimeStamp[] timestamps = NTPTime.runNTP(servers);
		Long totaltimestamp = 0L;
		int length = 0;
		for (TimeStamp tstamp : timestamps) {
			if (tstamp!=null) {
				totaltimestamp = totaltimestamp + tstamp.getTime();
				length++;
			}
		}
		Long time = totaltimestamp/(length);
		STime.setservertime(time);
		Thread servertimethread = new Thread(new java.lang.Runnable() {
            public void run() {
            	while(cancled!=true) {
					try {TimeUnit.SECONDS.sleep(10);} catch (InterruptedException e) {e.printStackTrace();}
            		STime.addtensecond();
            	}
            }});
		servertimethread.setDaemon(true);
		servertimethread.start();
		
		getMaxTimeFrame();
		System.out.println("Max: " + maxtimeframe);
		Long startime = STime.getServerTime() - (maxtimeframe * IndicatorMaps.timeframes.get(timeframe)*80000); //It is not 60000 to allow for some margin
		System.out.println((maxtimeframe * IndicatorMaps.timeframes.get(timeframe)*80000));
		System.out.println(startime);
		LocalDateTime utc = LocalDateTime.ofInstant(Instant.ofEpochMilli(time), ZoneOffset.UTC);
		LocalDateTime starttimedate = LocalDateTime.ofInstant(Instant.ofEpochMilli(startime), ZoneOffset.UTC);
		prevlive.put("StartTime", starttimedate);
		System.out.println("Sending prevlive : " + prevlive.toString());
		SocketCommunication.out.print(prevlive.toString());
    	SocketCommunication.out.flush();
		person.addOrderData("Starting Live Trading\n"
				+ String.format("%-10s:%10s\n","Base", base)
				+ String.format("%-10s:%10s\n","Alt", alt)
				+ String.format("%-10s:%10s\n","Max Period", maxtimeframe)
				+ String.format("%-10s:%10s\n","Exchange", exchangestring)
				+ String.format("%-10s:%10s\n","UTC Time", utc)
				+ String.format("%-10s:%10s\n","Volume", volume)
				+ String.format("%-10s:%10s\n","Time Frame", timeframe)
				+ "--------------------------------------\n") ;
	}
	
	public void getMaxTimeFrame() {
		for (Person person : dataentry) {
			String indicatorname1 = Indicators.getByCode(person.getIndicator1()).toString();
			String indicatorname2 = Indicators.getByCode(person.getIndicator2()).toString();
			String[] requiredparam1 = IndicatorMaps.indicatorparameters.get(indicatorname1);
			String[] requiredparam2 = IndicatorMaps.indicatorparameters.get(indicatorname2);
			int y = 0;
			for (String x : requiredparam1) {
				if (x=="timeFrame") {
					int time = (int) (person.getIndic1Param()[y]);
					if (time>maxtimeframe) {
						maxtimeframe = time;
					}
				}
				y++;
			}
			y=0;
			for (String x : requiredparam2) {
				if (x=="timeFrame") {
					int time = (int) (person.getIndic2Param()[y]);
					if (time>maxtimeframe) {
						maxtimeframe = time;
					}
				}
				y++;
			}
		}
		for (Person person : dataexit) {
			String indicatorname1 = Indicators.getByCode(person.getIndicator1()).toString();
			String indicatorname2 = Indicators.getByCode(person.getIndicator2()).toString();
			String[] requiredparam1 = IndicatorMaps.indicatorparameters.get(indicatorname1);
			String[] requiredparam2 = IndicatorMaps.indicatorparameters.get(indicatorname2);
			int y = 0;
			for (String x : requiredparam1) {
				if (x=="timeFrame") {
					int time = (int) (person.getIndic1Param()[y]);
					if (time>maxtimeframe) {
						maxtimeframe = time;
					}
				}
				y++;
			}
			y=0;
			for (String x : requiredparam2) {
				if (x=="timeFrame") {
					int time =  (int) (person.getIndic2Param()[y]);
					if (time>maxtimeframe) {
						maxtimeframe = time;
					}
				}
				y++;
			}
		}	
	}
	
	public void recievedPreviousPrices(JSONObject jsonmessage) {
		JSONArray returned = jsonmessage.getJSONArray("Return");
		int lenght = returned.length();
		ArrayList<Bar> ticksarray = new ArrayList<Bar>();
		//Bar[] ticksarray = new Bar[lenght-1];
		Date date = new Date(Long.valueOf(returned.getJSONArray(0).getInt(0)));
    	ZonedDateTime endTime = date.toInstant().atZone(ZoneOffset.UTC);
    	int multiplier = IndicatorMaps.timeframes.get(timeframe);
    	for (int x=0;x<lenght-1;x++) {
    		JSONArray ohlcv = returned.getJSONArray(x);
    		ticksarray.add(new BaseBar(endTime.plusMinutes(x*multiplier), (double) ohlcv.get(1), (double) ohlcv.get(2), (double) ohlcv.get(3), (double) ohlcv.get(4), (double) ohlcv.get(5)));
    	}
    	this.ticksarray = ticksarray;
    	TimeSeries series = new BaseTimeSeries("series",ticksarray);
    	this.series=series;
    	
    	ClosePriceIndicator closeprice = new ClosePriceIndicator(series);
        ArrayList<Rule> entryrules = new ArrayList<Rule>();
        boolean first = true;
        Rule andentryrule = null;
        for (Person entryrow : dataentry) { 		
    		//1
    		String indicatorname1 = Indicators.getByCode(entryrow.getIndicator1()).toString();
    		Object[] parameters1 = entryrow.getIndic1Param();
    		Indicator indicator = createindicator(entryrow, indicatorname1, parameters1, series, closeprice);
            entryrow.setfirstindicator(indicator);
    		//2
    		String indicatorname2 =Indicators.getByCode(entryrow.getIndicator2()).toString();
    		Object[] parameters2 = entryrow.getIndic2Param();
    		Indicator indicator2 = createindicator(entryrow, indicatorname2, parameters2, series, closeprice);
            entryrow.setsecondindicator(indicator2);
            //Rule
            String entryrulestring = TradingRules.getByCode(entryrow.getTradingRule()).toString();
            Rule entryrule = null;
            switch(entryrulestring) {
            	case "IsEqualRule":
            		entryrule = new IsEqualRule(indicator, indicator2);
            		break;
            	case "CrossedDownIndicatorRule":
            		entryrule = new CrossedDownIndicatorRule(indicator, indicator2);
            		break;
            	case "CrossedUpIndicatorRule":
            		entryrule = new CrossedUpIndicatorRule(indicator, indicator2);
            		break;
            	case "OverIndicatorRule":
            		entryrule = new OverIndicatorRule(indicator, indicator2);
            		break;
            	case "UnderIndicatorRule":
            		entryrule = new UnderIndicatorRule(indicator, indicator2);
            		break;
            	default:
            		System.out.println("Error");
            }
            
            if (entryrow.isor()==false) { //And
            	if (first) {
            		andentryrule = entryrule;
            	} else {
            		andentryrule = andentryrule.and(entryrule);
            	}
            } else { //Or
            	entryrules.add(andentryrule);
            	andentryrule = entryrule;
            }
            
    	}
    	entryrules.add(andentryrule);
    	
    	Rule totalentryrule = entryrules.get(0);
    	for (int i=1;i<entryrules.size();i++) {
    		totalentryrule = totalentryrule.or(entryrules.get(i));
    	}
    	ArrayList<Rule> exitrules = new ArrayList<Rule>();
        first = true;
        Rule andexitrule = null;
    	for (Person exitrow : dataexit) {
    		//1
    		String indicatorname1 = Indicators.getByCode(exitrow.getIndicator1()).toString();
    		Object[] parameters1 = exitrow.getIndic1Param();
    		Indicator indicator = createindicator(exitrow, indicatorname1, parameters1, series, closeprice);
    		exitrow.setfirstindicator(indicator);
    		//2
    		String indicatorname2 =Indicators.getByCode(exitrow.getIndicator2()).toString();
    		Object[] parameters2 = exitrow.getIndic2Param();
    		Indicator indicator2 = createindicator(exitrow, indicatorname2, parameters2, series, closeprice);
    		exitrow.setsecondindicator(indicator2);
    		//Rule
            String exitrulestring = TradingRules.getByCode(exitrow.getTradingRule()).toString();
            Rule exitrule = null;
            switch(exitrulestring) {
            	case "IsEqualRule":
            		exitrule = new IsEqualRule(indicator, indicator2);
            		break;
            	case "CrossedDownIndicatorRule":
            		exitrule = new CrossedDownIndicatorRule(indicator, indicator2);
            		break;
            	case "CrossedUpIndicatorRule":
            		exitrule = new CrossedUpIndicatorRule(indicator, indicator2);
            		break;
            	case "OverIndicatorRule":
            		exitrule = new OverIndicatorRule(indicator, indicator2);
            		break;
            	case "UnderIndicatorRule":
            		exitrule = new UnderIndicatorRule(indicator, indicator2);
            		break;
            	default:
            		System.out.println("Error");
            		break;
            }
            
            if (exitrow.isor()==false) { //And
            	if (first) {
            		andexitrule = exitrule;
            	} else {
            		andexitrule = andexitrule.and(exitrule);
            	}
            } else { //Or
            	exitrules.add(andexitrule);
            	andexitrule = exitrule;
            }
            
    	}
    	exitrules.add(andexitrule);
    	Rule totalexitrule = exitrules.get(0);
    	for (int i=1;i<exitrules.size();i++) {
    		totalexitrule = totalexitrule.or(exitrules.get(i));
    	}
    	Strategy tradingstrategy =  new BaseStrategy(totalentryrule,totalexitrule);
    	TradingRecord tradingRecord = new BaseTradingRecord();
    	this.totalentryrule = totalentryrule;
    	this.totalexitrule = totalexitrule;
    	this.tradingstrategy = tradingstrategy;
    	this.tradingRecord = tradingRecord;
    	int retlen = returned.length();
    	this.lasttime = returned.getJSONArray(retlen-1).getLong(0)-1000L;
    	System.out.println("Lasttime: " + lasttime);
        new Thread() {
            public void run() {
				try {
					loop();
				} catch (InterruptedException e) {e.printStackTrace();}
            }}.start();
	 }
	
	public void loop() throws InterruptedException {
		Long timemili = Long.valueOf(IndicatorMaps.timeframes.get(timeframe)*60000);
		Long timeformessage = timemili/5;
		person.addOrderData("\nSeconds Until Candle Close: " + ((lasttime+timemili)-STime.getServerTime())/1000);
		while(cancled!=true) {
			if (lasttime+timeformessage<STime.getServerTime()) {
				person.addOrderData("\nSeconds Until Candle Close: " + ((lasttime+timemili)-STime.getServerTime())/1000);
				timeformessage=timeformessage+(timemili/5);
			}
			//System.out.println("Lasttime: " + lasttime + "Seconds: " + ((lasttime+timemili)-STime.getServerTime())/1000 + "ServerTime: " + STime.getServerTime());
			if (lasttime+timemili<STime.getServerTime()) {
				person.addOrderData("\nRequesting Price");
				TimeUnit.SECONDS.sleep(10);
				JSONObject jsonrequest = json;
				LocalDateTime starttimedate = LocalDateTime.ofInstant(Instant.ofEpochMilli(lasttime), ZoneOffset.UTC);
				jsonrequest.put("StartTime", starttimedate);
				jsonrequest.put("request", "LiveTrading");
				SocketCommunication.out.print(jsonrequest.toString());
		    	SocketCommunication.out.flush();
				lasttime=lasttime+timemili;
			} else {
				TimeUnit.SECONDS.sleep(10);
			}
		}
	}
	
	public void recievedLiveTrading(JSONObject jsonmessage){
		JSONArray ohlcv = jsonmessage.getJSONArray("Return").getJSONArray(0);
		System.out.println("Long: " + ohlcv.getLong(0));
		Date date = new Date(ohlcv.getLong(0));
		System.out.println("Date: " + date.toString());
    	ZonedDateTime time = date.toInstant().atZone(ZoneOffset.UTC);
    	System.out.println("Zoned Date Time: " + time);
    	Object open = ohlcv.get(1);
    	Object high = ohlcv.get(2);
    	Object low = ohlcv.get(3);
    	Object close = ohlcv.get(4);
    	Object volume = ohlcv.get(5);
    	person.addOrderData("\nRecieved Price"
    			+ "\nCandle Start Time: " + time.toString()
    			+ "\nOpen: " + open.toString()
    			+ "\nHigh: " + high.toString()
    			+ "\nLow: " + low.toString()
    			+ "\nClose: " + close.toString()
    			+ "\nVolume " + volume.toString());
    	int multiplier = IndicatorMaps.timeframes.get(timeframe);
    	Bar bar = new BaseBar(time.plusMinutes(multiplier), (double) open, (double) high, (double) low, (double) close, (double) volume);
    	System.out.println("\nRecieved Price"
    			+ "\nCandle Start Time: " + time.toString()
    			+ "\nOpen: " + open.toString()
    			+ "\nHigh: " + high.toString()
    			+ "\nLow: " + low.toString()
    			+ "\nClose: " + close.toString()
    			+ "\nVolume " + volume.toString());
    	System.out.println(bar.toString());
    	ticksarray.add(bar);
    	series = new BaseTimeSeries("series",ticksarray);
		int endIndex = series.getEndIndex();
		if (tradingstrategy.shouldEnter(endIndex)) {
			System.out.println("Strategy should ENTER on " + endIndex);
		    tradingRecord.enter(endIndex, bar.getClosePrice(), Decimal.TEN);
		    boolean entered = tradingRecord.enter(endIndex, bar.getClosePrice(), Decimal.TEN);
            if (entered) {
                org.ta4j.core.Order entry = tradingRecord.getLastEntry();
                person.addOrderData("\nEntered on " + entry.getIndex()
                + " \n(price=" + entry.getPrice()
                + "\n, amount=" + entry.getAmount() + ")");
            }
		} else if (tradingstrategy.shouldExit(endIndex)) {
			System.out.println("Strategy should EXIT on " + endIndex);
		    tradingRecord.exit(endIndex, bar.getClosePrice(), Decimal.TEN);
		    boolean exited = tradingRecord.exit(endIndex, bar.getClosePrice(), Decimal.TEN);
            if (exited) {
            	org.ta4j.core.Order exit = tradingRecord.getLastExit();
                person.addOrderData("\nExited on " + exit.getIndex()
                + " \n(price=" + exit.getPrice()
                + "\n, amount=" + exit.getAmount() + ")");
            }
		} else {
			 person.addOrderData("No trade executed");
		}

		System.out.println(jsonmessage.toString());
	}
	
	public class sTime {
		public Long servertime;
		public void addtensecond() {
			servertime=servertime+10000;
		}
		public void setservertime(Long time) {
			servertime = time;
		}
		public Long getServerTime() {
			return servertime;
		}
	}
	private Indicator createindicator(Person row, String indicatorname, Object[] parameters, TimeSeries series, ClosePriceIndicator closeprice) {
			int i = 0;
			System.out.println(series);
			String[] requiredparam = IndicatorMaps.indicatorparameters.get(indicatorname);
			for (String x : requiredparam) {
				if (x=="closeprice") {
					parameters[i] = (Indicator) closeprice;
				} else if (x=="series") {
					parameters[i] = series;
				} else if (x=="MedianPriceIndicator") {
					parameters[i] = new MedianPriceIndicator(series);
				} else if (x=="StochasticOscillatorKIndicator"){
					int timeframe = (int) parameters[2];
					parameters = new Object[1];
					parameters[i] = new StochasticOscillatorKIndicator(series, timeframe);
				}
				i++;
			}
			try {
			Class<?>[] classes = new Class<?>[parameters.length];
			for (int ii = 0; ii < parameters.length; ++ii) {
				if (parameters[ii].getClass() == BaseTimeSeries.class) {
					 classes[ii] =  TimeSeries.class;
				} else if (parameters[ii].getClass() == Integer.class) {
					classes[ii] = int.class;
				} else if (parameters[ii].getClass() == ClosePriceIndicator.class) {
					classes[ii] = Indicator.class;
				} else if (parameters[ii].getClass() == MedianPriceIndicator.class) {
					classes[ii] = Indicator.class;
				} else {
					classes[ii] = parameters[ii].getClass();
				}
			}
			Class myClass = Class.forName("org.ta4j.core.indicators." + IndicatorMaps.indicatorclasspaths.get(indicatorname));
	        Constructor constructor = myClass.getDeclaredConstructor(classes);
	        Object indicator = constructor.newInstance(parameters);
	        System.out.println("Create  " + indicator.toString());
	        return (Indicator) indicator; 
			} catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			 return null;
	    }
	
	public void stopOrder() {
		System.out.println("cancel live order!!!");
		person.addOrderData("\nLive Trading has been manually canceled from dashboard.\n-------------------------------------------\n Stopping");
		this.cancled = true;
	}

	public void showMenu() {
		//CHART
		int barcount = series.getBarCount();
        Date[] dates = new Date[barcount];
        double[] opens = new double[barcount];
        double[] highs = new double[barcount];
        double[] lows = new double[barcount];
        double[] closes = new double[barcount];
        double[] volumes = new double[barcount];
        int x = 0;
        for (Bar bar : series.getBarData()) {
            dates[x] = new Date(bar.getEndTime().toEpochSecond() * 1000);
            opens[x] = bar.getOpenPrice().doubleValue();
            highs[x] = bar.getMaxPrice().doubleValue();
            lows[x] = bar.getMinPrice().doubleValue();
            closes[x] = bar.getClosePrice().doubleValue();
            volumes[x] = bar.getVolume().doubleValue();
        	x++;
        }
		TimeSeriesCollection indicatordataset = new TimeSeriesCollection();
		OHLCDataset ohlcdataset = new DefaultHighLowDataset( exchange  + " - "+ base + "/" + alt, dates, highs, lows, opens, closes, volumes);

		HashMap<String, Object[]> usedindicators = new HashMap<String, Object[]>();
        ArrayList<XYPlot> oscilators = new ArrayList<XYPlot>();

		Date startdate =Date.from(series.getFirstBar().getBeginTime().toInstant());
	    Date enddate = Date.from(series.getLastBar().getEndTime().toInstant());
	    DateAxis domainAxis = new DateAxis("Date");
	    domainAxis.setRange(startdate, enddate);
        
		for (Person exitrow : dataexit) {
			Indicator indic1 = (Indicator) exitrow.getfirstindicator();
			Indicator indic2 = (Indicator) exitrow.getsecondindicator();
			addindicatortochart(indic1, oscilators, closes, domainAxis, indicatordataset, series, exitrow.getIndicator1(), exitrow.getIndic1Param(), usedindicators);
			addindicatortochart(indic2, oscilators, closes, domainAxis, indicatordataset, series, exitrow.getIndicator2(), exitrow.getIndic2Param(), usedindicators);
		}
		
		for (Person entryrow : dataentry) {
			Indicator indic1 = (Indicator) entryrow.getfirstindicator();
			Indicator indic2 = (Indicator) entryrow.getsecondindicator();
			boolean added=false;
			addindicatortochart(indic1, oscilators, closes, domainAxis, indicatordataset, series, entryrow.getIndicator1(), entryrow.getIndic1Param(), usedindicators);
			addindicatortochart(indic2, oscilators, closes, domainAxis, indicatordataset, series,  entryrow.getIndicator2(), entryrow.getIndic2Param(), usedindicators);
		}

        // Candlestick rendering
        CandlestickRenderer renderer = new CandlestickRenderer();
        renderer.setAutoWidthMethod(CandlestickRenderer.WIDTHMETHOD_SMALLEST);
        NumberAxis priceAxis= new NumberAxis("Price");
        priceAxis.setAutoRangeIncludesZero(false);
        XYPlot XYPlotCandleStick = new XYPlot(ohlcdataset, domainAxis, priceAxis, renderer);
        
        //Indicator Dataset
        int index = 1; 
        XYPlotCandleStick.setDataset(index, indicatordataset); 
        XYPlotCandleStick.mapDatasetToRangeAxis(index, 0); 
        XYLineAndShapeRenderer renderer2 = new XYLineAndShapeRenderer(true, false); 
        renderer2.setSeriesPaint(index, java.awt.Color.BLUE); 
        XYPlotCandleStick.setRenderer(index, renderer2);

        //Build Combined Plot
        CombinedDomainXYPlot mainPlot = new CombinedDomainXYPlot(domainAxis);
        mainPlot.add(XYPlotCandleStick,90);

        for (XYPlot plot : oscilators) {
        	 System.out.println("adding oscilator");
        	 mainPlot.add(plot,20);
        }
       // double lowestLow = getLowestLow(lows);
        //double highestHigh = getHighestHigh(highs);
        JFreeChart finalchart = new JFreeChart("Backtesting Chart", null, mainPlot, true);
        XYPlot plot = XYPlotCandleStick;
        //finalchart.getXYPlot().getRangeAxis().setRange(lowestLow*0.95, highestHigh*1.05);
        //Add buy & sell trades to chart
        for (Trade trade : tradingRecord.getTrades()) {
        	System.out.println(trade.toString());
            // Buy signal
            double buySignalTickTime = new Minute(Date.from(series.getBar(trade.getEntry().getIndex()).getEndTime().toInstant())).getFirstMillisecond();
            Marker buyMarker = new ValueMarker(buySignalTickTime);
            buyMarker.setPaint(java.awt.Color.GREEN);
            buyMarker.setLabel("B");
            buyMarker.setLabelPaint(java.awt.Color.GREEN);
            buyMarker.setStroke(new BasicStroke(2));
            plot.addDomainMarker(buyMarker);
            // Sell signal
            double sellSignalTickTime = new Minute(Date.from(series.getBar(trade.getExit().getIndex()).getEndTime().toInstant())).getFirstMillisecond();
            Marker sellMarker = new ValueMarker(sellSignalTickTime);
            sellMarker.setPaint(java.awt.Color.RED);
            sellMarker.setLabel("S");
            sellMarker.setLabelPaint(java.awt.Color.RED);
            sellMarker.setStroke(new BasicStroke(2));
            plot.addDomainMarker(sellMarker);
        }
        
        // Chart panel
        ChartPanel panel = new ChartPanel(finalchart);
        panel.setFillZoomRectangle(true);
        panel.setMouseWheelEnabled(true);
        panel.setPreferredSize(new Dimension(1024, 400));
        ChartViewer viewer = new ChartViewer(finalchart);
        
        LiveTrading.chart=finalchart;
        LiveTrading.viewer=viewer;
        Platform.runLater(new Runnable() {
            public void run() {
		        FXMLLoader fxmlLoader = new FXMLLoader();
		        fxmlLoader.setLocation(BacktestController.class.getResource("/liveshowmenu.fxml"));
		        Scene scene;
				try {
					scene = new Scene(fxmlLoader.load(), 1000, 700);
			        Stage stage = new Stage();
			        stage.setTitle("LiveTrading Results");
			        stage.setScene(scene);
			        stage.show();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        }});
	}

	 private static void addindicatortochart(Indicator indic1, ArrayList<XYPlot> oscilators, double[] closes, DateAxis domainAxis, TimeSeriesCollection indicatordataset, TimeSeries series, String indicatorcode, Object[] parameters, HashMap<String, Object[]> usedindicators) {
	    	System.out.println("indiccode: " + indicatorcode);
	    	if (indicatorcode.equals("Dec")) {
	    		return;
	    	}
	    	boolean allequals=false;
	    	System.out.println("checking contains");
	    	if (usedindicators.containsKey(indicatorcode)) {
	    		System.out.println("contains indicaator");
	    		allequals=true;
	    		int x = 0;
	    		for (Object object : usedindicators.get(indicatorcode)) {
	    			System.out.println("looping");
	    			if (!parameters[x].equals(object)) {
	    				System.out.println("not equal");
	    				allequals=false;
	    			}
	    			x++;
	    		}
	    	}
	    	if (allequals==true) {
	    		System.out.println("all equals");
	    		return;
	    	}
	    	boolean added = false;
	    	for (int i=0;i<closes.length-1 && i<10;i++) {
				if (!added) {
				Decimal close = Decimal.valueOf(closes[i]);
				System.out.println(close.toString());
				Object indicvalue = indic1.getValue(i);
				System.out.println(indicvalue.getClass());
				if (indicvalue.getClass()==Decimal.class) {
					System.out.println("decimall");
					Decimal indicvalued = (Decimal) indicvalue;
					if (close.multipliedBy(2).isLessThan(indicvalued) || close.multipliedBy(0.5).isGreaterThan(indicvalued)) {
						added=true;
						System.out.println("indicator entry1 IS oscilator");
						XYLineAndShapeRenderer rendereroscilator = new XYLineAndShapeRenderer(true, false);
						TimeSeriesCollection oscilatordata = new TimeSeriesCollection(buildChartTimeSeries(series, indic1, indic1.toString()));
						NumberAxis numberaxis = new NumberAxis(indic1.toString());
						numberaxis.setAutoRangeIncludesZero(false);
						XYPlot oscilatorPlot = new XYPlot(oscilatordata, domainAxis, numberaxis, rendereroscilator);
						oscilators.add(oscilatorPlot);
						usedindicators.put(indicatorcode, parameters);
					} else {
						System.out.println("indicator entry1 NOT oscilator");
					}
				} else {
					System.out.println("not decimal");
				}
				} else {
					System.out.println("already added");
				}
			}
			if (!added) {
				indicatordataset.addSeries(buildChartTimeSeries(series, indic1, indic1.toString()));
				usedindicators.put(indicatorcode, parameters);
			}
		}
		private static org.jfree.data.time.TimeSeries buildChartTimeSeries(TimeSeries tickSeries, Indicator<Decimal> indicator, String name) {
	        org.jfree.data.time.TimeSeries chartTimeSeries = new org.jfree.data.time.TimeSeries(name);
	        for (int i = 0; i < tickSeries.getBarCount(); i++) {
	        	Bar tick = tickSeries.getBar(i);
	            chartTimeSeries.add(new Minute(Date.from(tick.getEndTime().toInstant())), indicator.getValue(i).toDouble());
	        }
	        return chartTimeSeries;
	}

		public void showInfo() {
			//Create the new 
			
		}
	
}
