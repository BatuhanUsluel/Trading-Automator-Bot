package controllers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXDatePicker;

import application.Exchanges;
import application.Main;
import application.SocketCommunication;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Separator;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.collections.FXCollections;
import controllers.Person;


import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.ta4j.core.Rule;
import org.ta4j.core.Strategy;
import org.ta4j.core.Tick;
import org.ta4j.core.TimeSeries;
import org.ta4j.core.TimeSeriesManager;
import org.ta4j.core.TradingRecord;
import org.ta4j.core.analysis.criteria.*;
import org.ta4j.core.indicators.*;
import org.ta4j.core.indicators.adx.*;
import org.ta4j.core.indicators.bollinger.*;
import org.ta4j.core.indicators.candles.*;
import org.ta4j.core.indicators.ichimoku.*;
import org.ta4j.core.indicators.pivotpoints.*;
import org.ta4j.core.indicators.volume.*;
import org.ta4j.core.indicators.helpers.*;
import org.ta4j.core.trading.rules.*;
import org.apache.commons.lang3.math.NumberUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.ta4j.core.BaseStrategy;
import org.ta4j.core.BaseTick;
import org.ta4j.core.BaseTimeSeries;
import org.ta4j.core.BaseTimeSeries.*;
import org.ta4j.core.Decimal;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import org.ta4j.core.Indicator;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.util.StringConverter;
import javafx.util.converter.DefaultStringConverter;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;
import javafx.scene.Node;
import javafx.beans.binding.Bindings;


import javafx.application.Application;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.Background;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import controllers.Indicators;
import controllers.Person;


public class BacktestController {
    @FXML private TableView<Person> BackEntryTable = new TableView<Person>();
    @FXML private TableView<Person> BackExitTable = new TableView<Person>();
    @FXML private JFXButton Backtest;
    @FXML private JFXComboBox<String> BackExchange;
    @FXML private TextField BackBase;
    @FXML private TextField BackAlt;
    @FXML private TextField LiveBase;
    @FXML private JFXComboBox<String> Timeframe;
    @FXML private JFXButton BackAddExitRow;
    @FXML private JFXButton BackAddEntryRow;
    @FXML private JFXDatePicker starttime;
    @FXML private JFXDatePicker endtime;
    public static ObservableList<Person> Backdataentry =  FXCollections.observableArrayList();
    public static ObservableList<Person> Backdataexit =  FXCollections.observableArrayList();
    static HashMap<String, String[]> indicatorparameters = new HashMap<String, String[]>();
    static HashMap<String, String> indicatorclasspaths = new HashMap<String, String>();
    HashMap<String, Integer> timeframes = new HashMap<String, Integer>();
	private static int candles;
	private static LocalDate timestart;
	@FXML
    public void initialize(){
		System.out.println(ZonedDateTime.now());
		timeframes.put("1m", 1);
		timeframes.put("5m", 5);
		timeframes.put("1h", 60);
		timeframes.put("4h", 240);
		timeframes.put("1d", 1440);
		timeframes.put("1w", 10080);
		indicatorparameters.put("AccelerationDecelerationIndicator",  new String[]{"series","timeFrameSma1" , "timeFrameSma2"});
		indicatorparameters.put("AroonDownIndicator",  new String[]{"series","timeFrame"});
		indicatorparameters.put("AroonOscillatorIndicator", new String[]{"series","timeFrame"});
		indicatorparameters.put("AroonUpIndicator", new String[]{"series","timeFrame"});
		indicatorparameters.put("ATRIndicator", new String[]{"series","timeFrame"});//??
		indicatorparameters.put("AwesomeOscillatorIndicator", new String[]{"closeprice","timeFrameSma1", "timeFrameSma2"});
		indicatorparameters.put("CCIIndicator", new String[]{"series","timeFrame"});
		indicatorparameters.put("ChandelierExitLongIndicator", new String[]{"series","timeFrame","K multiplier"});
		indicatorparameters.put("ChandelierExitShortIndicator", new String[]{"series","timeFrame","K multiplier"});
		indicatorparameters.put("CMOIndicator", new String[]{"closeprice","timeFrame"});
		indicatorparameters.put("CoppockCurveIndicator", new String[]{"closeprice","longRoCTimeFrame", "shortRoCTimeFrame", "wmaTimeFrame"});
		indicatorparameters.put("DoubleEMAIndicator", new String[]{"closeprice","timeFrame"});
		indicatorparameters.put("DPOIndicator", new String[]{"closeprice","timeFrame"});
		indicatorparameters.put("EMAIndicator", new String[]{"closeprice","timeFrame"});
		indicatorparameters.put("FisherIndicator", new String[]{"MedianPriceIndicator","timeFrame", "alpha","beta"});///
		indicatorparameters.put("HMAIndicator", new String[]{"closeprice","timeFrame"});
		indicatorparameters.put("KAMAIndicator", new String[]{"closeprice","timeFrameEffectiveRatio", "timeFrameFast", "timeFrameSlow"});
		indicatorparameters.put("MACDIndicator", new String[]{"closeprice","shortTimeFrame", "longTimeFrame"});
		indicatorparameters.put("MMAIndicator", new String[]{"series","timeFrame"});//???
		indicatorparameters.put("ParabolicSarIndicator", new String[]{"series","Acceleration factor", "Max Acceleration", "Acceleration Increment"});
		indicatorparameters.put("PPOIndicator", new String[]{"closeprice","shortTimeFrame", "longTimeFrame"});
		indicatorparameters.put("RandomWalkIndexHighIndicator", new String[]{"series","timeFrame"});
		indicatorparameters.put("RandomWalkIndexLowIndicator", new String[]{"series","timeFrame"});
		indicatorparameters.put("RAVIIndicator", new String[]{"closeprice","shortSmaTimeFrame" , "longSmaTimeFrame"});
		indicatorparameters.put("ROCIndicator", new String[]{"closeprice","timeFrame"});
		indicatorparameters.put("RSIIndicator", new String[]{"closeprice","timeFrame"});
		indicatorparameters.put("SMAIndicator", new String[]{"closeprice","timeFrame"});
		//?
		indicatorparameters.put("StochasticOscillatorDIndicator", new String[]{"series","timeFrame"}); //?
		indicatorparameters.put("StochasticOscillatorKIndicator", new String[]{"series","timeFrame"});
		indicatorparameters.put("StochasticRSIIndicator", new String[]{"series","timeFrame"});
		indicatorparameters.put("TripleEMAIndicator", new String[]{"closeprice","timeFrame"});
		indicatorparameters.put("UlcerIndexIndicator", new String[]{"closeprice","timeFrame"});
		indicatorparameters.put("WilliamsRIndicator", new String[]{"series","timeFrame"});
		indicatorparameters.put("WMAIndicator", new String[]{"closeprice","timeFrame"});
		indicatorparameters.put("ZLEMAIndicator", new String[]{"closeprice","timeFrame"});
		
		
		indicatorclasspaths.put("AccelerationDecelerationIndicator", "org.ta4j.core.indicators.AccelerationDecelerationIndicator");
		indicatorclasspaths.put("AroonDownIndicator", "org.ta4j.core.indicators.AroonDownIndicator");
		indicatorclasspaths.put("AroonOscillatorIndicator", "org.ta4j.core.indicators.AroonOscillatorIndicator");
		indicatorclasspaths.put("AroonUpIndicator", "org.ta4j.core.indicators.AroonUpIndicator");
		indicatorclasspaths.put("ATRIndicator", "org.ta4j.core.indicators.ATRIndicator");
		indicatorclasspaths.put("AwesomeOscillatorIndicator", "org.ta4j.core.indicators.AwesomeOscillatorIndicator");
		indicatorclasspaths.put("CCIIndicator", "org.ta4j.core.indicators.CCIIndicator");
		indicatorclasspaths.put("ChandelierExitLongIndicator", "org.ta4j.core.indicators.ChandelierExitLongIndicator");
		indicatorclasspaths.put("ChandelierExitShortIndicator", "org.ta4j.core.indicators.ChandelierExitShortIndicator");
		indicatorclasspaths.put("CMOIndicator", "org.ta4j.core.indicators.CMOIndicator");
		indicatorclasspaths.put("CoppockCurveIndicator", "org.ta4j.core.indicators.CoppockCurveIndicator");
		indicatorclasspaths.put("DoubleEMAIndicator", "org.ta4j.core.indicators.DoubleEMAIndicator");
		indicatorclasspaths.put("DPOIndicator", "org.ta4j.core.indicators.DPOIndicator");
		indicatorclasspaths.put("EMAIndicator", "org.ta4j.core.indicators.EMAIndicator");
		indicatorclasspaths.put("FisherIndicator", "org.ta4j.core.indicators.FisherIndicator");
		indicatorclasspaths.put("HMAIndicator", "org.ta4j.core.indicators.HMAIndicator");
		indicatorclasspaths.put("KAMAIndicator", "org.ta4j.core.indicators.KAMAIndicator");
		indicatorclasspaths.put("MACDIndicator", "org.ta4j.core.indicators.MACDIndicator");
		indicatorclasspaths.put("MMAIndicator", "org.ta4j.core.indicators.MMAIndicator");
		indicatorclasspaths.put("ParabolicSarIndicator", "org.ta4j.core.indicators.ParabolicSarIndicator");
		indicatorclasspaths.put("PPOIndicator", "org.ta4j.core.indicators.PPOIndicator");
		indicatorclasspaths.put("RandomWalkIndexHighIndicator", "org.ta4j.core.indicators.RandomWalkIndexHighIndicator");
		indicatorclasspaths.put("RandomWalkIndexLowIndicator", "org.ta4j.core.indicators.RandomWalkIndexLowIndicator");
		indicatorclasspaths.put("RAVIIndicator", "org.ta4j.core.indicators.RAVIIndicator");
		indicatorclasspaths.put("ROCIndicator", "org.ta4j.core.indicators.ROCIndicator");
		indicatorclasspaths.put("ROCIndicator", "org.ta4j.core.indicators.ROCIndicator");
		indicatorclasspaths.put("RSIIndicator", "org.ta4j.core.indicators.RSIIndicator");
		indicatorclasspaths.put("SMAIndicator", "org.ta4j.core.indicators.SMAIndicator");
		indicatorclasspaths.put("StochasticOscillatorDIndicator", "org.ta4j.core.indicators.StochasticOscillatorDIndicator");
		indicatorclasspaths.put("StochasticOscillatorKIndicator", "org.ta4j.core.indicators.StochasticOscillatorKIndicator");
		indicatorclasspaths.put("StochasticRSIIndicator", "org.ta4j.core.indicators.StochasticRSIIndicator");
		indicatorclasspaths.put("TripleEMAIndicator", "org.ta4j.core.indicators.TripleEMAIndicator");
		indicatorclasspaths.put("UlcerIndexIndicator", "org.ta4j.core.indicators.UlcerIndexIndicator");
		indicatorclasspaths.put("WilliamsRIndicator", "org.ta4j.core.indicators.WilliamsRIndicator");
		indicatorclasspaths.put("WMAIndicator", "org.ta4j.core.indicators.WMAIndicator");
		indicatorclasspaths.put("ZLEMAIndicator", "org.ta4j.core.indicators.ZLEMAIndicator");
		String css = this.getClass().getResource("/assets/datepicker.css").toExternalForm();
		Controller.scene.getStylesheets().add(css);;
		setUpEntryTable();
		setUpExitTable();
        List<String> list = new ArrayList<String>(Exchanges.list);
        BackExchange.getItems().addAll(list);
    }
	
    @FXML
    void addEntryRow(ActionEvent event) {
	    Person person = new Person(Indicators.PPOIndicator.getCode(), true, Indicators.CCIIndicator.getCode(), TradingRules.IsEqualRule.getCode(), null, null, null, null);
	    Backdataentry.add(person);
	    BackEntryTable.setItems(Backdataentry);
	    BackEntryTable.refresh();
    }

    @FXML
    void addExitRow(ActionEvent event) {
	    Person person = new Person(Indicators.PPOIndicator.getCode(), true, Indicators.CCIIndicator.getCode(), TradingRules.IsEqualRule.getCode(), null,null, null, null);
	    Backdataexit.add(person);
	    BackExitTable.setItems(Backdataexit);
	    BackExitTable.refresh();
    }

    @FXML
    void runBackTest(ActionEvent event) throws NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, ClassNotFoundException {
    	for (Person person : BackEntryTable.getItems()) {
    		String indicator1 = person.getIndicator1();
    		String indicator2 = person.getIndicator2();

        //There are 78 (excluded statistics & helpers) indicators.
        //Todo:
        	//Create all indicators with dynamic form
        	//Store them in the person object
        	//Figure out how and/or works in ta4j
        	//Create strategies from the indicators
        		//Loop for each person, get the two indicators, switch case for the rule, create new rule with that
        		//Combine them with and/or
        		//Do this for both entry and exit
        		//Create overall strategy, and run it
        
    	}
    	for (Person person : BackExitTable.getItems()) {
    		
    	}

    	JSONObject backtestJSON = new JSONObject();
    	try {
    		
    		LocalDate starttime2 = starttime.getValue();
    		this.timestart=starttime2;
    		LocalDate endtime2 = endtime.getValue();
    		//int timeframe = timeframes.get(Timeframe.getValue().toString());
    		int timeframe = 1440;
    		int days = (int) ChronoUnit.DAYS.between(starttime2, endtime2);
    		int candles = (days*1440)/timeframe;
    		this.candles=candles;
			backtestJSON.put("base", BackBase.getText());
	    	backtestJSON.put("alt", BackAlt.getText());
	    	backtestJSON.put("request", "Historic");
	    	backtestJSON.put("Exchanges", BackExchange.getValue().toString());
	    	backtestJSON.put("Timeframe", "1d");
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
    	
    }
    
    public static void recievedBackTest(JSONObject jsonmessage){
    	JSONArray test = jsonmessage.getJSONArray("Return");
    	Tick[] ticksarray = new Tick[candles];
    	ZonedDateTime endTime = timestart.atStartOfDay(ZoneOffset.UTC);
    	System.out.println(test);
    	for (int x=0;x<candles;x++) {
    		JSONArray ohlcv = test.getJSONArray(x);
    		ticksarray[x] = (new BaseTick(endTime.plusDays(x), (double) ohlcv.get(1), (double) ohlcv.get(2), (double) ohlcv.get(3), (double) ohlcv.get(4), (double) ohlcv.get(5)));
    	}
    	List<Tick> ticks = Arrays.asList(ticksarray);
    	BaseTimeSeries series = new BaseTimeSeries("ticks", ticks);
    	TimeSeriesManager seriesManager = new TimeSeriesManager(series);
    	ClosePriceIndicator closeprice = new ClosePriceIndicator(series);
    	for (Person row : Backdataentry) {
    		//1
    		String indicatorname1 = Indicators.getByCode(row.getIndicator1()).toString();
    		System.out.println("Indic1 name: " + indicatorname1);
    		String[] requiredparam1 = indicatorparameters.get(indicatorname1);
    		Object[] parameters1 = row.getIndic1Param();
    		int i = 0;
    		for (String x : requiredparam1) {
    			if (x=="closeprice") {
    				parameters1[i] = closeprice;
    			} else if (x=="series") {
    				parameters1[i] = series;
    			} else if (x=="MedianPriceIndicator") {
    				parameters1[i] = new MedianPriceIndicator(series);
    			} else {
    			}
    			i++;
    		}
    		try {
    		Class myClass1 = Class.forName(indicatorclasspaths.get(indicatorname1));
            Constructor constructor1;	
            System.out.println("Class: " + parameters1[0].getClass());
			constructor1 = myClass1.getConstructor();
            Object firstindicator = constructor1.newInstance(parameters1);
            row.setfirstindicator(firstindicator);
            System.out.println("Create  " + firstindicator.toString());
			} catch (NoSuchMethodException | SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    		//2
    		String indicatorname2 =Indicators.getByCode(row.getIndicator2()).toString();
    		System.out.println("Indic2 name: " + indicatorname2);
    		String[] requiredparam2 = indicatorparameters.get(indicatorname2);
    		Object[] parameters2 = row.getIndic2Param();
    		i = 0;
    		for (String x : requiredparam2) {
    			if (x=="closeprice") {
    				parameters2[i] = closeprice;
    			} else if (x=="series") {
    				parameters2[i] = series;
    			} else if (x=="MedianPriceIndicator") {
    				parameters2[i] = new MedianPriceIndicator(series);
    			} else {
    				System.out.println("????");
    			}
    			i++;
    		}
			try {
			Class myClass2 = Class.forName(indicatorclasspaths.get(indicatorname2));
            Constructor constructor2 = myClass2.getConstructor();
            Object secondindicator = constructor2.newInstance(parameters2);
            row.setsecondindicator(secondindicator);
			} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
    	for (Person row2 : Backdataexit) {
    		
    	}
    	
    	SMAIndicator shortSma = new SMAIndicator(new ClosePriceIndicator(series), 5);
    	SMAIndicator longSma = new SMAIndicator(new ClosePriceIndicator(series), 10);
		Strategy myStrategy =  new BaseStrategy(new CrossedUpIndicatorRule(shortSma, longSma), new CrossedDownIndicatorRule(shortSma, longSma));
		TradingRecord tradingRecord = seriesManager.run(myStrategy);
		
		// Total profit
        TotalProfitCriterion totalProfit = new TotalProfitCriterion();
        System.out.println("Total profit: " + totalProfit.calculate(series, tradingRecord));
        // Number of bars
        System.out.println("Number of bars: " + new NumberOfTicksCriterion().calculate(series, tradingRecord));
        // Average profit (per bar)
        System.out.println("Average profit (per bar): " + new AverageProfitCriterion().calculate(series, tradingRecord));
        // Number of trades
        System.out.println("Number of trades: " + new NumberOfTradesCriterion().calculate(series, tradingRecord));
        // Profitable trades ratio
        System.out.println("Profitable trades ratio: " + new AverageProfitableTradesCriterion().calculate(series, tradingRecord));
        // Maximum drawdown
        System.out.println("Maximum drawdown: " + new MaximumDrawdownCriterion().calculate(series, tradingRecord));
        // Reward-risk ratio
        System.out.println("Reward-risk ratio: " + new RewardRiskRatioCriterion().calculate(series, tradingRecord));
        // Total transaction cost
        System.out.println("Total transaction cost (from $1000): " + new LinearTransactionCostCriterion(1000, 0.005).calculate(series, tradingRecord));
        // Buy-and-hold
        System.out.println("Buy-and-hold: " + new BuyAndHoldCriterion().calculate(series, tradingRecord));
        // Total profit vs buy-and-hold
        System.out.println("Custom strategy profit vs buy-and-hold strategy profit: " + new VersusBuyAndHoldCriterion(totalProfit).calculate(series, tradingRecord));
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
    	 
    	            person.setIndicator1(newIndicator1.getCode());
    	            createFormGUI(person,1);
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
    	 
    	            person.setIndicator2(newIndicator2.getCode());
    	            createFormGUI(person,2);
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
    	 
    	        // ==== SINGLE? (CHECH BOX) ===
    	        andor.setCellValueFactory(new Callback<CellDataFeatures<Person, java.lang.Boolean>, ObservableValue<java.lang.Boolean>>() {
    	 
    	            @Override
    	            public ObservableValue<java.lang.Boolean> call(CellDataFeatures<Person, java.lang.Boolean> param) {
    	                Person person = param.getValue();
    	 
    	                SimpleBooleanProperty booleanProp = new SimpleBooleanProperty(person.isor());
    	 
    	                // Note: singleCol.setOnEditCommit(): Not work for
    	                // CheckBoxTableCell.
    	 
    	                // When "Single?" column change.
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
 
            person.setIndicator1(newIndicator1.getCode());
            createFormGUI(person,1);
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
 
            person.setIndicator2(newIndicator2.getCode());
            createFormGUI(person,2);
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
 
        // ==== SINGLE? (CHECH BOX) ===
        andor.setCellValueFactory(new Callback<CellDataFeatures<Person, java.lang.Boolean>, ObservableValue<java.lang.Boolean>>() {
 
            @Override
            public ObservableValue<java.lang.Boolean> call(CellDataFeatures<Person, java.lang.Boolean> param) {
                Person person = param.getValue();
 
                SimpleBooleanProperty booleanProp = new SimpleBooleanProperty(person.isor());
 
                // Note: singleCol.setOnEditCommit(): Not work for
                // CheckBoxTableCell.
 
                // When "Single?" column change.
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
    
    public void createFormGUI(Person person, int i) {
    	String indic1code = person.getIndicator1();
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
        Label label = new Label(Indicators.getByCode(indic1code).toString());
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
        String[] parametersstring = indicatorparameters.get(Indicators.getByCode(indic1code).toString());
        TextField[] TextFields = new TextField[parametersstring.length];
        
        //Just noticed this can't work. We create the indicators, that requires a series, before we even get the data or press run. It should get the parameters here, then do a switch case later in order to create the indicators
        //In order to get the parameters, use a hashmap that has its keys being the indicator codes and its values being a string array that has the parameters names
        //Loop for this array, create the gui. Then when the button is pressed get the values by looping trough the textboxes, create an array with them, and store them in the person object.
        //Later on, when looping for each person, you can just get the indicators given parameters. Then switch case the indicator, and fill in the other values.
        //To make the case switch smaller. Have the parameters required include stuff like series, closedata, etc. If it requires special(bollinger), then also have that. When creating dynamic gui ignore those values
        //Then later on, when switch casing, do the special things on their own. Then run a default value, and in there check if the parameter is series, closeprice, etc. and input that.
        /*switch(indic1code) {
	        case "Accel":      	
				parametersstring[0] = "timeFrameSma1";
				parametersstring[1]	= "timeFrameSma2";
				button.setOnAction(e -> {
					AccelerationDecelerationIndicator formedindic = new AccelerationDecelerationIndicator(series, Integer.parseInt(parameters[0].getText()),Integer.parseInt(parameters[1].getText()));
					person.setfirstindicator(formedindic);
					dialog.close();
				});
	        	break;
	        case "AroonDown":
	        	parametersstring[0] = "TimeFrame";
	        	button.setOnAction(e -> {
	        		AroonDownIndicator formedindic = new AroonDownIndicator(series, Integer.parseInt(parameters[0].getText()));
	        		setindicator(person, formedindic, i, dialog);
	        	});
	        	break;
	        case "AroonOscil":
	        	parametersstring[0] = "TimeFrame";
	        	button.setOnAction(e -> {
	        		AroonOscillatorIndicator formedindic = new AroonOscillatorIndicator(series, Integer.parseInt(parameters[0].getText()));
	        		setindicator(person, formedindic, i, dialog);
	        	});
	        	break;
	        case "ArronUp":
	        	parametersstring[0] = "TimeFrame";
	        	button.setOnAction(e -> {
	        		AroonUpIndicator formedindic = new AroonUpIndicator(series, Integer.parseInt(parameters[0].getText()));
	        		setindicator(person, formedindic, i, dialog);
	        	});
	        	break;
	        case "ATR":
	        	parametersstring[0] = "";
	        	parametersstring[1] = "";
	        	button.setOnAction(e -> {
	        		//AwesomeOscillatorIndicator formedindic = new AwesomeOscillatorIndicator(formedindic, 0, 0);
	        		//setindicator(person, formedindic, i, dialog);
	        	});
	        	break;
	        case "AWS":
	        	parametersstring[0] = "";
	        	parametersstring[1] = "";
	        	button.setOnAction(e -> {
	        		
	        		//person.setfirstindicator(formedindic);
					dialog.close();
	        	});
	        	break;
	        case "CCI":
	        	break;
	        case "CELI":
	        	break;
	        case "CESI":
	        	break;
	        case "CMO":
	        	break;
	        case "CoCI":
	        	break;
	        case "DEI":
	        	break;
	        case "DPO":
	        	break;
	        case "EMA":
	        	break;
	        case "FI":
	        	break;
	        case "HMA":
	        	break;
	        case "KAMA":
	        	break;
	        case "MACD":
	        	break;
	        case "MMA":
	        	break;
	        case "PSI":
	        	break;
	        case "PPO":
	        	break;
	        case "RWIHI":
	        	break;
	        case "RWILI":
	        	break;
	        case "RAVI":
	        	break;
	        case "ROCI":
	        	break;
	        case "RSI":
	        	break;
	        case "SMA":
	        	break;
	        case "SODI":
	        	break;
	        case "SOKI":
	        	break;
	        case "SRI":
	        	break;
	        case "TEI":
	        	break;
	        case "UII":
	        	break;
	        case "ZLEMA":
	        	break;
	        case "BBLI":
	        	break;
	        default:
	        	System.out.println("def: " + indic1code);
	        	break;
        	}*/
        	createSpecificGui(parametersstring,TextFields,vbox.getChildren(),vbox2.getChildren());
        	
        	vbox2.getChildren().add(button);
            Scene dialogScene = new Scene(hbox, 400, 300);
            dialog.setScene(dialogScene);
            dialog.show();
        	button.setOnAction(e -> {
        		Object[] parameters = new Object[TextFields.length];
        		int x=0;
        		for (TextField text : TextFields) {	
        			if (text!=null) {
        				String stringtext = text.getText();
        				if(!NumberUtils.isCreatable(stringtext)) {
        					
        				} else {
        					parameters[x] = stringtext;
        				}
        			}
        			x++;
        		}
        		if (i==1) {
        			person.setIndic1Param(parameters);
        		} else if (i==2) {
        			person.setIndic2Param(parameters);
        		} else {
        			//error
        		}
        		dialog.close();
        	});
        }

	private void createSpecificGui(String[] parametersstring, Object[] TextFields,ObservableList<Node> observableList, ObservableList<Node> observableList2) {
		int i=0;
		for (String x : parametersstring) {
			if (x!=null && x!="closeprice" && x!="series" && x!="MedianPriceIndicator") {
				TextField textfield = new TextField();
				observableList.add(textfield);
				Label label = new Label(x);
				observableList2.add(label);
				TextFields[i] = textfield;
			}
			i++;
		}
	}
}
