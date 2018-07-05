package controllers;

import java.awt.Dimension;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.swing.JFrame;

import org.apache.commons.lang3.math.NumberUtils;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.fx.ChartViewer;
import org.jfree.chart.plot.Marker;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.time.Minute;
import org.jfree.data.time.TimeSeriesCollection;
import org.json.JSONArray;
import org.json.JSONObject;
import org.ta4j.core.Bar;
import org.ta4j.core.BaseBar;
import org.ta4j.core.BaseStrategy;
import org.ta4j.core.BaseTimeSeries;
import org.ta4j.core.Decimal;
import org.ta4j.core.Indicator;
import org.ta4j.core.Rule;
import org.ta4j.core.Strategy;
import org.ta4j.core.TimeSeries;
import org.ta4j.core.TimeSeriesManager;
import org.ta4j.core.Trade;
import org.ta4j.core.TradingRecord;
import org.ta4j.core.analysis.criteria.AverageProfitableTradesCriterion;
import org.ta4j.core.analysis.criteria.BuyAndHoldCriterion;
import org.ta4j.core.analysis.criteria.MaximumDrawdownCriterion;
import org.ta4j.core.analysis.criteria.NumberOfTradesCriterion;
import org.ta4j.core.analysis.criteria.RewardRiskRatioCriterion;
import org.ta4j.core.analysis.criteria.TotalProfitCriterion;
import org.ta4j.core.analysis.criteria.VersusBuyAndHoldCriterion;
import org.ta4j.core.indicators.StochasticOscillatorKIndicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.indicators.helpers.MedianPriceIndicator;
import org.ta4j.core.trading.rules.CrossedDownIndicatorRule;
import org.ta4j.core.trading.rules.CrossedUpIndicatorRule;
import org.ta4j.core.trading.rules.IsEqualRule;
import org.ta4j.core.trading.rules.OverIndicatorRule;
import org.ta4j.core.trading.rules.UnderIndicatorRule;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXDatePicker;

import application.Exchanges;
import application.FxDialogs;
import application.Main;
import application.SocketCommunication;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingNode;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;

public class BacktestController {
    @FXML private TableView<Person> BackEntryTable = new TableView<Person>();
    @FXML private TableView<Person> BackExitTable = new TableView<Person>();
    @FXML private JFXButton Backtest;
    @FXML private JFXComboBox<String> BackExchange;
    @FXML private TextField BackBase;
    @FXML private TextField BackAlt;
    @FXML private JFXComboBox<String> timeframefx;
    @FXML private JFXButton BackAddExitRow;
    @FXML private JFXButton BackAddEntryRow;
    @FXML private JFXButton save;
    @FXML private JFXButton load;
    @FXML private JFXDatePicker starttime;
    @FXML private JFXDatePicker endtime;
    @FXML private JFXButton removeEntryRow;
    @FXML private JFXButton removeExitRow;
    public static ObservableList<Person> Backdataentry =  FXCollections.observableArrayList();
    public static ObservableList<Person> Backdataexit =  FXCollections.observableArrayList();
	private static String exchange;
	private static String base;
	private static String alt;
	private static int candles;
	private static LocalDate timestart;
	static ChartPanel panel;
	static JFreeChart chart;
	static ChartViewer viewer;
	static TimeSeries series;
	static TradingRecord tradingRecord;
	@FXML
    public void initialize(){
		System.out.println(ZonedDateTime.now());
		
		String css = this.getClass().getResource("/assets/datepicker.css").toExternalForm();
		Controller.scene.getStylesheets().add(css);;
		setUpEntryTable();
		setUpExitTable();
        List<String> list = new ArrayList<String>(Exchanges.list);
        BackExchange.getItems().addAll(list);
        List<String> timelist = new ArrayList<String>();
        for (String key : IndicatorMaps.timeframes.keySet()) {
        	timelist.add(key);
        }
        timeframefx.getItems().addAll(timelist);
    }
	
    @FXML
    void addEntryRow(ActionEvent event) {
	    Person person = new Person(Indicators.Select.getCode(), false, Indicators.Select.getCode(), TradingRules.CrossedUpIndicatorRule.getCode(), null, null, null, null);
	    Backdataentry.add(person);
	    BackEntryTable.setItems(Backdataentry);
	    BackEntryTable.refresh();
    }

    @FXML
    void addExitRow(ActionEvent event) {
	    Person person = new Person(Indicators.Select.getCode(), false, Indicators.Select.getCode(), TradingRules.CrossedDownIndicatorRule.getCode(), null,null, null, null);
	    Backdataexit.add(person);
	    BackExitTable.setItems(Backdataexit);
	    BackExitTable.refresh();
    }

    @FXML
    void runBackTest(ActionEvent event) throws NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, ClassNotFoundException {

    	
    	String base = BackBase.getText();
    	String alt = BackAlt.getText();
    	boolean noerror = true;
		StringBuilder stringBuilder = new StringBuilder();
    	for (Person person : BackEntryTable.getItems()) {
    		String indicator1 = person.getIndicator1();
    		String indicator2 = person.getIndicator2();
    		if (indicator1.equals("Select") || indicator2.equals("Select")) {
    			noerror=false;
    			stringBuilder.append("Please select an indicator. Indicator can't be 'Select'\n");
    		}
    	}
    	for (Person person : BackExitTable.getItems()) {
    		String indicator1 = person.getIndicator1();
    		String indicator2 = person.getIndicator2();
    		if (indicator1.equals("Select") || indicator2.equals("Select")) {
    			noerror=false;
    			stringBuilder.append("Please select an indicator. Indicator can't be 'Select'\n");
    		}
    	}
		boolean noexchange = false;
    	String exchange = "";
     	
    	try {
    		exchange = BackExchange.getValue().toString();
    	} catch (NullPointerException e) {
    		noerror=false;
    		noexchange=true;
    		stringBuilder.append("Please enter a exchange\n");
    	}
    	if (!Exchanges.list.contains(exchange) && noexchange!=true) {
    		noerror=false;
    		stringBuilder.append(exchange + " is not a valid exchange.\n");
    	}
    	if(base==null) {
    		noerror=false;
    		stringBuilder.append("Base can not be empty\n");
    	}
    	if (alt==null) {
    		noerror=false;
    		stringBuilder.append("Alt can not be empty\n");
    	}
    	if (Backdataentry.get(0).isor()==true) {
    		noerror=false;
    		stringBuilder.append("First row in entry can not have 'or?' enabled\n");
    	}
    	if (Backdataexit.get(0).isor()==true) {
    		noerror=false;
    		stringBuilder.append("First row in exitcan not have 'or?' enabled\n");
    	}
    	if (noerror==true) {
    	JSONObject backtestJSON = new JSONObject();
    	try {
    		
    		LocalDate starttime2 = starttime.getValue();
    		this.timestart=starttime2;
    		LocalDate endtime2 = endtime.getValue();
    		int timeframe = IndicatorMaps.timeframes.get(timeframefx.getValue().toString());
    		
    		int days = (int) ChronoUnit.DAYS.between(starttime2, endtime2);
    		int candles = (days*1440)/timeframe;
    		this.candles=candles;
    		this.base = base;
    		this.alt = alt;
    		this.exchange = BackExchange.getValue().toString();
			backtestJSON.put("base", base);
	    	backtestJSON.put("alt", alt);
	    	backtestJSON.put("request", "Historic");
	    	backtestJSON.put("Exchanges", exchange);
	    	backtestJSON.put("Timeframe", timeframefx.getValue().toString());
	    	backtestJSON.put("StartTime", starttime.getValue() + " 00:00:00");
	    	backtestJSON.put("Candles", candles);
	    	backtestJSON.put("licenceKey", SocketCommunication.licencekey);
	    	backtestJSON.put("millisstart", System.currentTimeMillis());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	SocketCommunication.out.print(backtestJSON.toString());
    	SocketCommunication.out.flush();
    	} else {
    		String finalString = stringBuilder.toString();
	    	FxDialogs.showError(null, finalString);
    	}
    	
    }
    
    public static void recievedBackTest(JSONObject jsonmessage) throws IOException{
    	JSONArray returned = jsonmessage.getJSONArray("Return");
    	Bar[] ticksarray = new Bar[candles];
    	ZonedDateTime startTime = timestart.atStartOfDay(ZoneOffset.UTC);
    	System.out.println(candles);
    	int multiplier = IndicatorMaps.timeframes.get(jsonmessage.getString("Timeframe"));
    	for (int x=0;x<candles;x++) {
    		JSONArray ohlcv = returned.getJSONArray(x);
    		ticksarray[x] = (new BaseBar(startTime.plusMinutes(x*multiplier), (double) ohlcv.get(1), (double) ohlcv.get(2), (double) ohlcv.get(3), (double) ohlcv.get(4), (double) ohlcv.get(5)));
    	}
    	List<Bar> ticks = Arrays.asList(ticksarray);
    	TimeSeries series = new BaseTimeSeries("series",ticks);
    	System.out.println("Sclass: " + series.getClass());
    	
    	ClosePriceIndicator closeprice = new ClosePriceIndicator(series);
        ArrayList<Rule> entryrules = new ArrayList<Rule>();
        boolean first = true;
        Rule andentryrule = null;
    	for (Person entryrow : Backdataentry) { 		
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
    	for (Person exitrow : Backdataexit) {
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
    	TimeSeriesManager seriesManager = new TimeSeriesManager(series);
		TradingRecord tradingRecord = seriesManager.run(tradingstrategy);
		System.out.println("TC: " + tradingRecord.getTradeCount() + "GT:  " + series.getBarCount());
		
		
		
		//CHART
		TimeSeriesCollection dataset = new TimeSeriesCollection();
		dataset.addSeries(buildChartTimeSeries(series, new ClosePriceIndicator(series), exchange  + " - "+ base + "/" + alt));
		for (Person exitrow : Backdataexit) {
			Indicator indic1 = (Indicator) exitrow.getfirstindicator();
			Indicator indic2 = (Indicator) exitrow.getsecondindicator();
			dataset.addSeries(buildChartTimeSeries(series, indic1, indic1.toString()));
			dataset.addSeries(buildChartTimeSeries(series, indic2, indic2.toString()));
		}
		for (Person entryrow : Backdataentry) {
			Indicator indic1 = (Indicator) entryrow.getfirstindicator();
			Indicator indic2 = (Indicator) entryrow.getsecondindicator();
			dataset.addSeries(buildChartTimeSeries(series, indic1, indic1.toString()));
			dataset.addSeries(buildChartTimeSeries(series, indic2, indic2.toString()));
		}
        JFreeChart chart = ChartFactory.createTimeSeriesChart(
                "Backtesting Chart", // title
                "Date", // x-axis label
                "Price", // y-axis label
                dataset, // data
                true, // create legend?
                true, // generate tooltips?
                false // generate URLs?
                );
        XYPlot plot = (XYPlot) chart.getPlot();
        DateAxis axis = (DateAxis) plot.getDomainAxis();
        
        Date startdate = new Date(Long.parseLong(returned.getJSONArray(0).get(0).toString()));
        System.out.println("S: " + startdate);
        int retlen = returned.length();
        Date enddate = new Date(Long.parseLong(returned.getJSONArray(retlen-1).get(0).toString()));
        System.out.println("E: " + enddate);
        axis.setRange(startdate, enddate);
        axis.setDateFormatOverride(new SimpleDateFormat("MM-dd HH:mm"));
        for (Trade trade : tradingRecord.getTrades()) {
        	System.out.println(trade.toString());
            // Buy signal
            double buySignalTickTime = new Minute(Date.from(series.getBar(trade.getEntry().getIndex()).getEndTime().toInstant())).getFirstMillisecond();
            Marker buyMarker = new ValueMarker(buySignalTickTime);
            buyMarker.setPaint(java.awt.Color.GREEN);
            buyMarker.setLabel("B");
            buyMarker.setLabelPaint(java.awt.Color.GREEN);
            plot.addDomainMarker(buyMarker);
            // Sell signal
            double sellSignalTickTime = new Minute(Date.from(series.getBar(trade.getExit().getIndex()).getEndTime().toInstant())).getFirstMillisecond();
            Marker sellMarker = new ValueMarker(sellSignalTickTime);
            sellMarker.setPaint(java.awt.Color.RED);
            sellMarker.setLabel("S");
            sellMarker.setLabelPaint(java.awt.Color.RED);
            plot.addDomainMarker(sellMarker);
        }
        
        // Chart panel
        ChartPanel panel = new ChartPanel(chart);
        panel.setFillZoomRectangle(true);
        panel.setMouseWheelEnabled(true);
        panel.setPreferredSize(new Dimension(1024, 400));
        ChartViewer viewer = new ChartViewer(chart);
        BacktestController.chart=chart;
        BacktestController.viewer=viewer;
        Platform.runLater(new Runnable() {
            public void run() {
		        FXMLLoader fxmlLoader = new FXMLLoader();
		        fxmlLoader.setLocation(BacktestController.class.getResource("/backtestresults.fxml"));
		        Scene scene;
				try {
					scene = new Scene(fxmlLoader.load(), 1000, 700);
			        Stage stage = new Stage();
			        stage.setTitle("Backtest Results");
			        stage.setScene(scene);
			        stage.show();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        }});
        BacktestController.series=series;
        BacktestController.tradingRecord=tradingRecord;
		
    }

    private static org.jfree.data.time.TimeSeries buildChartTimeSeries(TimeSeries tickSeries, Indicator<Decimal> indicator, String name) {
        org.jfree.data.time.TimeSeries chartTimeSeries = new org.jfree.data.time.TimeSeries(name);
        for (int i = 0; i < tickSeries.getBarCount(); i++) {
        	Bar tick = tickSeries.getBar(i);
            chartTimeSeries.add(new Minute(Date.from(tick.getEndTime().toInstant())), indicator.getValue(i).toDouble());
        }
        return chartTimeSeries;
}
    private static Indicator createindicator(Person row, String indicatorname, Object[] parameters, TimeSeries series, ClosePriceIndicator closeprice) {
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
				break;
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
    
    void setUpEntryTable() {
    	// Editable
    	BackEntryTable.setEditable(true);
    	        TableColumn<Person, Indicators> Indicator1//
    	                = new TableColumn<Person, Indicators>("Indicator1");
    	 
    	        TableColumn<Person, TradingRules> TradingRule//
    	        = new TableColumn<Person, TradingRules>("TradingRule");
    	        
    	        TableColumn<Person, Indicators> Indicator2//
    	        = new TableColumn<Person, Indicators>("Indicator2");
    	        
    	        TableColumn<Person, java.lang.Boolean> andor//
    	                = new TableColumn<Person, java.lang.Boolean>("Or?");
    	 
    	        // ==== Indicator1 (COMBO BOX) ===
    	 
    	        ObservableList<Indicators> indicatorList = FXCollections.observableArrayList(//
    	                Indicators.values());
    	 
    	        Indicator1.setCellValueFactory(new Callback<CellDataFeatures<Person, Indicators>, ObservableValue<Indicators>>() {
    	 
    	            @Override
    	            public ObservableValue<Indicators> call(CellDataFeatures<Person, Indicators> param) {
    	                Person person = param.getValue();
    	                String indicatorCode = person.getIndicator1();
    	                Indicators gender = Indicators.getByCode(indicatorCode);
    	                return new SimpleObjectProperty<Indicators>(gender);
    	            }
    	        });
    	 
    	        Indicator1.setCellFactory(ComboBoxTableCell.forTableColumn(indicatorList));
    	 
    	        Indicator1.setOnEditCommit((CellEditEvent<Person, Indicators> event) -> {
    	            TablePosition<Person, Indicators> pos = event.getTablePosition();
    	 
    	            Indicators newIndicator1 = event.getNewValue();
    	 
    	            int row = pos.getRow();
    	            Person person = event.getTableView().getItems().get(row);
    	            createFormGUI(person,1, newIndicator1.getCode());
    	           
    	        });
    	 
    	        Indicator1.setMinWidth(120);
    	 
    	        
    	        // ==== Indicator2 (COMBO BOX) ===
    	        ObservableList<Indicators> indicatorList2 = FXCollections.observableArrayList(//
    	                Indicators.values());
    	 
    	        Indicator2.setCellValueFactory(new Callback<CellDataFeatures<Person, Indicators>, ObservableValue<Indicators>>() {
    	 
    	            @Override
    	            public ObservableValue<Indicators> call(CellDataFeatures<Person, Indicators> param) {
    	                Person person = param.getValue();
    	                String indicatorCode = person.getIndicator2();
    	                Indicators gender = Indicators.getByCode(indicatorCode);
    	                return new SimpleObjectProperty<Indicators>(gender);
    	            }
    	        });
    	 
    	        Indicator2.setCellFactory(ComboBoxTableCell.forTableColumn(indicatorList2));
    	 
    	        Indicator2.setOnEditCommit((CellEditEvent<Person, Indicators> event) -> {
    	            TablePosition<Person, Indicators> pos = event.getTablePosition();
    	 
    	            Indicators newIndicator2 = event.getNewValue();
    	 
    	            int row = pos.getRow();
    	            Person person = event.getTableView().getItems().get(row);
    	            createFormGUI(person,2, newIndicator2.getCode());
    	        });
    	 
    	        Indicator2.setMinWidth(120);
    	 
    	     // ==== TradingRule (COMBO BOX) ===
    	        ObservableList<TradingRules> TradingRuleList = FXCollections.observableArrayList(TradingRules.values());
    	 
    	        TradingRule.setCellValueFactory(new Callback<CellDataFeatures<Person, TradingRules>, ObservableValue<TradingRules>>() {
    	 
    	            @Override
    	            public ObservableValue<TradingRules> call(CellDataFeatures<Person, TradingRules> param) {
    	                Person person = param.getValue();
    	                String TradingRulesCode = person.getTradingRule();
    	                TradingRules gender = TradingRules.getByCode(TradingRulesCode);
    	                return new SimpleObjectProperty<TradingRules>(gender);
    	            }
    	        });
    	 
    	        TradingRule.setCellFactory(ComboBoxTableCell.forTableColumn(TradingRuleList));
    	 
    	        TradingRule.setOnEditCommit((CellEditEvent<Person, TradingRules> event) -> {
    	            TablePosition<Person, TradingRules> pos = event.getTablePosition();
    	 
    	            TradingRules newTradingRule = event.getNewValue();
    	 
    	            int row = pos.getRow();
    	            Person person = event.getTableView().getItems().get(row);
    	 
    	            person.setTradingRule(newTradingRule.getCode());
    	            System.out.println(newTradingRule.getText());
    	        });
    	 
    	        TradingRule.setMinWidth(120);
    	 
    	        //Check Box
    	        andor.setCellValueFactory(new Callback<CellDataFeatures<Person, java.lang.Boolean>, ObservableValue<java.lang.Boolean>>() {
    	 
    	            @Override
    	            public ObservableValue<java.lang.Boolean> call(CellDataFeatures<Person, java.lang.Boolean> param) {
    	                Person person = param.getValue();
    	 
    	                SimpleBooleanProperty booleanProp = new SimpleBooleanProperty(person.isor());
    	                booleanProp.addListener(new ChangeListener<java.lang.Boolean>() {
    	 
    	                    @Override
    	                    public void changed(ObservableValue<? extends java.lang.Boolean> observable, java.lang.Boolean oldValue,
    	                    		java.lang.Boolean newValue) {
    	                        person.setor(newValue);
    	                    }
    	                });
    	                return booleanProp;
    	            }
    	        });
    	 
    	        andor.setCellFactory(new Callback<TableColumn<Person, java.lang.Boolean>, //
    	        TableCell<Person, java.lang.Boolean>>() {
    	            @Override
    	            public TableCell<Person, java.lang.Boolean> call(TableColumn<Person, java.lang.Boolean> p) {
    	                CheckBoxTableCell<Person, java.lang.Boolean> cell = new CheckBoxTableCell<Person, java.lang.Boolean>();
    	                cell.setAlignment(Pos.CENTER);
    	                return cell;
    	            }
    	        });

    	        BackEntryTable.setItems(Backdataentry);
    	 
    	        BackEntryTable.getColumns().addAll(Indicator1, TradingRule, Indicator2, andor);
    	        String css = this.getClass().getResource("/assets/tableview.css").toExternalForm();
    }
    
    void setUpExitTable() {
    	// Editable
		BackExitTable.setEditable(true);
        TableColumn<Person, Indicators> Indicator1//
                = new TableColumn<Person, Indicators>("Indicator1");
 
        TableColumn<Person, TradingRules> TradingRule//
        = new TableColumn<Person, TradingRules>("TradingRule");
        
        TableColumn<Person, Indicators> Indicator2//
        = new TableColumn<Person, Indicators>("Indicator2");
        
        TableColumn<Person, java.lang.Boolean> andor//
                = new TableColumn<Person, java.lang.Boolean>("Or?");
 
        // ==== Indicator1 (COMBO BOX) ===
 
        ObservableList<Indicators> indicatorList = FXCollections.observableArrayList(//
                Indicators.values());
 
        Indicator1.setCellValueFactory(new Callback<CellDataFeatures<Person, Indicators>, ObservableValue<Indicators>>() {
 
            @Override
            public ObservableValue<Indicators> call(CellDataFeatures<Person, Indicators> param) {
                Person person = param.getValue();
                String indicatorCode = person.getIndicator1();
                Indicators gender = Indicators.getByCode(indicatorCode);
                return new SimpleObjectProperty<Indicators>(gender);
            }
        });
 
        Indicator1.setCellFactory(ComboBoxTableCell.forTableColumn(indicatorList));
 
        Indicator1.setOnEditCommit((CellEditEvent<Person, Indicators> event) -> {
            TablePosition<Person, Indicators> pos = event.getTablePosition();
           
            Indicators newIndicator1 = event.getNewValue();
            
            int row = pos.getRow();
            Person person = event.getTableView().getItems().get(row);
            createFormGUI(person,1,newIndicator1.getCode());
        });
 
        Indicator1.setMinWidth(120);
 
        
        // ==== Indicator2 (COMBO BOX) ===
        ObservableList<Indicators> indicatorList2 = FXCollections.observableArrayList(//
                Indicators.values());
 
        Indicator2.setCellValueFactory(new Callback<CellDataFeatures<Person, Indicators>, ObservableValue<Indicators>>() {
 
            @Override
            public ObservableValue<Indicators> call(CellDataFeatures<Person, Indicators> param) {
                Person person = param.getValue();
                String indicatorCode = person.getIndicator2();
                Indicators gender = Indicators.getByCode(indicatorCode);
                return new SimpleObjectProperty<Indicators>(gender);
            }
        });
 
        Indicator2.setCellFactory(ComboBoxTableCell.forTableColumn(indicatorList2));
 
        Indicator2.setOnEditCommit((CellEditEvent<Person, Indicators> event) -> {
            TablePosition<Person, Indicators> pos = event.getTablePosition();
 
            Indicators newIndicator2 = event.getNewValue();
 
            int row = pos.getRow();
            Person person = event.getTableView().getItems().get(row);
            createFormGUI(person,2,newIndicator2.getCode());
        });
 
        Indicator2.setMinWidth(120);
 
     // ==== TradingRule (COMBO BOX) ===
        ObservableList<TradingRules> TradingRuleList = FXCollections.observableArrayList(TradingRules.values());
 
        TradingRule.setCellValueFactory(new Callback<CellDataFeatures<Person, TradingRules>, ObservableValue<TradingRules>>() {
 
            @Override
            public ObservableValue<TradingRules> call(CellDataFeatures<Person, TradingRules> param) {
                Person person = param.getValue();
                String TradingRulesCode = person.getTradingRule();
                TradingRules gender = TradingRules.getByCode(TradingRulesCode);
                return new SimpleObjectProperty<TradingRules>(gender);
            }
        });
 
        TradingRule.setCellFactory(ComboBoxTableCell.forTableColumn(TradingRuleList));
 
        TradingRule.setOnEditCommit((CellEditEvent<Person, TradingRules> event) -> {
            TablePosition<Person, TradingRules> pos = event.getTablePosition();
 
            TradingRules newTradingRule = event.getNewValue();
 
            int row = pos.getRow();
            Person person = event.getTableView().getItems().get(row);
 
            person.setTradingRule(newTradingRule.getCode());
            System.out.println(newTradingRule.getText());
        });
 
        TradingRule.setMinWidth(120);
 
        //Check Box
        andor.setCellValueFactory(new Callback<CellDataFeatures<Person, java.lang.Boolean>, ObservableValue<java.lang.Boolean>>() {
 
            @Override
            public ObservableValue<java.lang.Boolean> call(CellDataFeatures<Person, java.lang.Boolean> param) {
                Person person = param.getValue();
 
                SimpleBooleanProperty booleanProp = new SimpleBooleanProperty(person.isor());
                booleanProp.addListener(new ChangeListener<java.lang.Boolean>() {
 
                    @Override
                    public void changed(ObservableValue<? extends java.lang.Boolean> observable, java.lang.Boolean oldValue,
                    		java.lang.Boolean newValue) {
                        person.setor(newValue);
                    }
                });
                return booleanProp;
            }
        });
 
        andor.setCellFactory(new Callback<TableColumn<Person, java.lang.Boolean>, //
        TableCell<Person, java.lang.Boolean>>() {
            @Override
            public TableCell<Person, java.lang.Boolean> call(TableColumn<Person, java.lang.Boolean> p) {
                CheckBoxTableCell<Person, java.lang.Boolean> cell = new CheckBoxTableCell<Person, java.lang.Boolean>();
                cell.setAlignment(Pos.CENTER);
                return cell;
            }
        });

        BackExitTable.setItems(Backdataexit);
 
        BackExitTable.getColumns().addAll(Indicator1, TradingRule, Indicator2, andor);
        String css = this.getClass().getResource("/assets/tableview.css").toExternalForm();
    }
    
    public void createFormGUI(Person person, int i, String code) {

    	if (code=="Select") {
    		return;
    	}
    	String indicstring = Indicators.getByCode(code).toString();
    	final Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(Main.primaryStage);
        HBox hbox = new HBox();
        hbox.setPadding(new Insets(8, 8, 8, 8));
        hbox.setSpacing(20);
        hbox.setStyle("-fx-background-color: #3e4047;");
        VBox vbox = new VBox();
        vbox.setSpacing(15);
        vbox.setStyle("-fx-background-color: #3e4047;");
        vbox.setPadding(new Insets(10));
        Label label = new Label(indicstring);
        label.setFont(Font.font("Verdana", FontWeight.BOLD, 20));
        label.setAlignment(Pos.TOP_CENTER);
        label.setTextFill(Color.WHITE);
        VBox vbox2 = new VBox();
        vbox2.setSpacing(15);
        vbox.setPadding(new Insets(30,0,0,10));
        vbox2.setStyle("-fx-background-color: #3e4047;");
        vbox2.getChildren().add(label);
        hbox.getChildren().addAll(vbox2, vbox);
        String css = this.getClass().getResource("/assets/formgui.css").toExternalForm();
        hbox.getStylesheets().add(css);
        
        JFXButton button = new JFXButton("Done");
        String[] parametersstring = IndicatorMaps.indicatorparameters.get(indicstring);
        TextField[] TextFields = new TextField[parametersstring.length];
    	createSpecificGui(parametersstring,TextFields,vbox.getChildren(),vbox2.getChildren());
    	
    	vbox2.getChildren().add(button);
        Scene dialogScene = new Scene(hbox, 400, 300);
        dialog.setScene(dialogScene);
        dialog.show();
       
		StringBuilder stringBuilder = new StringBuilder();
    	button.setOnAction(e -> {
    		boolean noerror = true;
    		Object[] parameters = new Object[TextFields.length];
    		int x=0;
    		for (TextField text : TextFields) {	
    			if (text!=null) {
    				String stringtext = text.getText();
    				if(!NumberUtils.isCreatable(stringtext)) {
    					stringBuilder.append(stringtext + " is not a valid number.\n");
    					noerror=false;
    				} else {
    					//Also have if/else to parse some inputs into decimal.
    					if (IndicatorMaps.indicatorparameters.get(indicstring)[x].matches("K multiplier|alpha|beta|Acceleration factor|Max Acceleration|Acceleration Increment")) {
    						parameters[x] = Decimal.valueOf(stringtext);
    					} else {
    					parameters[x] = Integer.valueOf(stringtext);
    					}
    				}
    			}
    			x++;
    		}
    		if (noerror==true) {
	    		if (i==1) {
	    			System.out.println("setting first indic");
	    			person.setIndic1Param(parameters);
	    			person.setIndicator1(code);
	    		} else if (i==2) {
	    			System.out.println("setting second indic");
	    			person.setIndic2Param(parameters);
	    			person.setIndicator2(code);
	    		}
	    		dialog.close();
    		} else {
    		    String finalString = stringBuilder.toString();
    	    	FxDialogs.showError(null, finalString);
    		}
    	});
    }

	private void createSpecificGui(String[] parametersstring, Object[] TextFields,ObservableList<Node> observableList, ObservableList<Node> observableList2) {
		int i=0;
		for (String x : parametersstring) {
			if (x!=null && x!="closeprice" && x!="series" && x!="MedianPriceIndicator" && x!="StochasticOscillatorKIndicator") {
				TextField textfield = new TextField();
				observableList.add(textfield);
				Label label = new Label(x);
				observableList2.add(label);
				TextFields[i] = textfield;
			}
			i++;
		}
	}
	@FXML
	void saveStrategy(ActionEvent event) throws IOException, ClassNotFoundException  {
		
		
		List<Person> entryrules = BackEntryTable.getItems();
		List<Person> exitrules = BackExitTable.getItems();
		Person[] entryrulesarray = new Person[entryrules.size()];
		Person[] exitrulesarray = new Person[entryrules.size()];
		int i=0;
		for(Person entry : entryrules) {
			entryrulesarray[i] = entry;
			i++;
		}
		i=0;
		for(Person exit : exitrules) {
			exitrulesarray[i] = exit;
			i++;
		}
		FullStrategy fullstrat = new FullStrategy(entryrulesarray, exitrulesarray);
		
		
		FileChooser fileChooser = new FileChooser();
		 
        //Set extension filter for text files
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("SER files (*.ser)", "*.ser");
        fileChooser.getExtensionFilters().add(extFilter);

        //Show save file dialog
        File file = fileChooser.showSaveDialog(Main.primaryStage);

        if (file != null) {
    		FileOutputStream fos = new FileOutputStream(file,false);
    		ObjectOutputStream oos = new ObjectOutputStream(fos);
    		oos.writeObject(fullstrat);
    		oos.close();
        }



	}
	@FXML
	void loadStrategy(ActionEvent event) throws IOException, ClassNotFoundException {
		 final FileChooser fileChooser = new FileChooser();
		File file = fileChooser.showOpenDialog(Main.primaryStage);
        if (file != null) {
        	FileInputStream fis = new FileInputStream(file);
    		ObjectInputStream ois = new ObjectInputStream(fis);
    		FullStrategy result = (FullStrategy) ois.readObject();
    		ois.close();
    		Person[] entryrules = result.getentryrules();
    		Person[] exitrules = result.getexitrules();
    		Backdataentry.clear();
    		Backdataexit.clear();
    		for (Person entry : entryrules) {
    			Backdataentry.add(entry);
    		}
    		for (Person exit : exitrules) {
    			Backdataexit.add(exit);
    		}
    		BackEntryTable.setItems(Backdataentry);
    		BackEntryTable.refresh();
    	    
    	    BackExitTable.setItems(Backdataexit);
    	    BackExitTable.refresh();
        }
	}
	@FXML
	void removeEntryRow(ActionEvent event) {
		Person selectedItem = BackEntryTable.getSelectionModel().getSelectedItem();
		BackEntryTable.getItems().remove(selectedItem);
	}
	@FXML
	void removeExitRow(ActionEvent event) {
		Person selectedItem = BackExitTable.getSelectionModel().getSelectedItem();
		BackExitTable.getItems().remove(selectedItem);
	}
}
