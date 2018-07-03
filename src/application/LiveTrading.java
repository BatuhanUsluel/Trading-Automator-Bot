package application;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.net.ntp.TimeStamp;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.knowm.xchange.Exchange;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.marketdata.Ticker;
import org.ta4j.core.BaseStrategy;
import org.ta4j.core.BaseTick;
import org.ta4j.core.BaseTimeSeries;
import org.ta4j.core.Indicator;
import org.ta4j.core.Rule;
import org.ta4j.core.Strategy;
import org.ta4j.core.Tick;
import org.ta4j.core.TimeSeries;
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
import javafx.collections.ObservableList;

public class LiveTrading implements Runnable {
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
		JSONObject prevlive = json;
		prevlive.put("request", "prevlive");
		
		sTime STime = new sTime();
		this.STime=STime;
		String[] servers = new String[] {"pool.ntp.org","0.pool.ntp.org","1.pool.ntp.org","2.pool.ntp.org","3.pool.ntp.org"};
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
					System.out.println("Adding 10 seconds. Time: " + STime.getServerTime());
            		STime.addtensecond();
            	}
            }});
		servertimethread.start();
		
		getMaxTimeFrame();
		System.out.println("Max: " + maxtimeframe);
		Long startime = STime.getServerTime() - (maxtimeframe * IndicatorMaps.timeframes.get(timeframe)*80000); //It is not 60000 to allow for some margin
		System.out.println((maxtimeframe * IndicatorMaps.timeframes.get(timeframe)*80000));
		System.out.println(startime);
		LocalDateTime starttimedate = LocalDateTime.ofInstant(Instant.ofEpochMilli(startime), ZoneOffset.UTC);
		prevlive.put("StartTime", starttimedate);
		System.out.println("Sending prevlive : " + prevlive.toString());
		SocketCommunication.out.print(prevlive.toString());
    	SocketCommunication.out.flush();
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
		System.out.println("recieved prev prices");
		JSONArray returned = jsonmessage.getJSONArray("Return");
		int lenght = returned.length();
		System.out.println("len: " + lenght);
		Tick[] ticksarray = new Tick[lenght];
		Date date = new Date(Long.valueOf(returned.getJSONArray(0).getInt(0)));
    	ZonedDateTime endTime = date.toInstant().atZone(ZoneOffset.UTC);
    	int multiplier = IndicatorMaps.timeframes.get(timeframe);
    	System.out.println(lenght);
    	for (int x=0;x<lenght;x++) {
    		JSONArray ohlcv = returned.getJSONArray(x);
    		ticksarray[x] = (new BaseTick(endTime.plusMinutes(x*multiplier), (double) ohlcv.get(1), (double) ohlcv.get(2), (double) ohlcv.get(3), (double) ohlcv.get(4), (double) ohlcv.get(5)));
    	}
    	List<Tick> ticks = Arrays.asList(ticksarray);
    	TimeSeries series = new BaseTimeSeries("series",ticks);
    	System.out.println("Sclass: " + series.getClass());
    	
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
    	System.out.println(totalentryrule.toString());
    	Strategy tradingstrategy =  new BaseStrategy(totalentryrule,totalexitrule);
    	this.totalentryrule = totalentryrule;
    	this.totalexitrule = totalexitrule;
    	this.tradingstrategy = tradingstrategy;
    	int retlen = returned.length();
    	//System.out.println("last: " + (String) returned.getJSONArray(retlen-1).(0));
    	this.lasttime = returned.getJSONArray(retlen-1).getLong(0);
    	System.out.println("Lasttime: " + lasttime);
    	try {
			loop();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	 }
	
	public void loop() throws InterruptedException {
		Long timemili = Long.valueOf(IndicatorMaps.timeframes.get(timeframe)*60000);
		while(cancled!=true) {
			System.out.println("Lasttime: " + lasttime + "Timemili: " + timemili + "ServerTime: " + STime.getServerTime());
			if (lasttime+timemili<STime.getServerTime()) {
				System.out.println("Time. Requesting price");
				TimeUnit.SECONDS.sleep(10);
				JSONObject jsonrequest = json;
				jsonrequest.put("Candles", 2);
				LocalDateTime starttimedate = LocalDateTime.ofInstant(Instant.ofEpochMilli(lasttime), ZoneOffset.UTC);
				jsonrequest.put("StartTime", starttimedate);
				jsonrequest.put("request", "LiveTrading");
				System.out.println("Live: " + jsonrequest.toString());
				SocketCommunication.out.print(jsonrequest.toString());
		    	SocketCommunication.out.flush();
				lasttime=lasttime+timemili;
			} else {
				System.out.println("Sleeping 5 seconds");
				TimeUnit.SECONDS.sleep(5);
			}
		}
	}
	
	public void recievedLiveTrading(JSONObject jsonmessage){
		System.out.println("recieved live trading");
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
			Class myClass = Class.forName(IndicatorMaps.indicatorclasspaths.get(indicatorname));
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
}
