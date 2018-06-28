package controllers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXDatePicker;

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
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import org.ta4j.core.Rule;
import org.ta4j.core.indicators.*;
import org.ta4j.core.indicators.adx.*;
import org.ta4j.core.indicators.bollinger.*;
import org.ta4j.core.indicators.candles.*;
import org.ta4j.core.indicators.ichimoku.*;
import org.ta4j.core.indicators.pivotpoints.*;
import org.ta4j.core.indicators.volume.*;
import org.ta4j.core.indicators.helpers.*;
import org.ta4j.core.trading.rules.*;
import org.json.JSONObject;
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
    @FXML private JFXComboBox<?> BackExchange;
    @FXML private TextField BackBase;
    @FXML private TextField BackAlt;
    @FXML private TextField LiveBase;
    @FXML private JFXButton BackAddExitRow;
    @FXML private JFXButton BackAddEntryRow;
    @FXML private JFXDatePicker starttime;
    @FXML private JFXDatePicker endtime;
    public static ObservableList<Person> Backdataentry =  FXCollections.observableArrayList();
    public static ObservableList<Person> Backdataexit =  FXCollections.observableArrayList();
    HashMap<String, String[]> indicatorparameters = new HashMap<String, String[]>();
    HashMap<String, String> indicatorclasspaths = new HashMap<String, String>();
	@FXML
    public void initialize(){
		indicatorparameters.put("Accel", new String[]{"timeFrameSma1","timeFrameSma2"});
		indicatorparameters.put("AroonDown", new String[]{"timeFrame"});
		indicatorparameters.put("AroonOscil", new String[]{"timeFrame"});
		indicatorparameters.put("ArronUp", new String[]{"timeFrame"});
		indicatorparameters.put("ATR", new String[]{"timeFrame"});
		indicatorparameters.put("EMA", new String[]{"timeFrame"});
		
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
    }
	
    @FXML
    void addEntryRow(ActionEvent event) {
	    Person person = new Person(Indicators.PPOIndicator.getCode(), true, Indicators.CCIIndicator.getCode(), TradingRules.IsEqualRule.getCode(), null, null);
	    Backdataentry.add(person);
	    BackEntryTable.setItems(Backdataentry);
	    BackEntryTable.refresh();
    }

    @FXML
    void addExitRow(ActionEvent event) {
	    Person person = new Person(Indicators.PPOIndicator.getCode(), true, Indicators.CCIIndicator.getCode(), TradingRules.IsEqualRule.getCode(), null,null);
	    Backdataexit.add(person);
	    BackExitTable.setItems(Backdataexit);
	    BackExitTable.refresh();
    }

    @FXML
    void runBackTest(ActionEvent event) throws NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, ClassNotFoundException {

        
        
    	int EntrySize = BackEntryTable.getItems().size();
    	int ExitSize = BackExitTable.getItems().size();
    	for (Person person : BackEntryTable.getItems()) {
    		String indicator1 = person.getIndicator1();
    		String indicator2 = person.getIndicator2();
    		String entryRule = person.getTradingRule();
    		boolean andor = person.isor();
    		String indic1code = Indicators.getByString(indicator1);
    		String indic2code = Indicators.getByString(indicator2);
    		
    		
    		  ClosePriceIndicator closePrice = new ClosePriceIndicator(null);
        EMAIndicator shortEma = new EMAIndicator(closePrice, 9);
        EMAIndicator longEma = new EMAIndicator(closePrice, 26);
        System.out.println(shortEma.toString());
        BollingerBandsMiddleIndicator bol = new BollingerBandsMiddleIndicator(null);
        //Rule entryRule = new CrossedUpIndicatorRule(shortEma, longEma);
        Rule test = new IsFallingRule(shortEma,1);
        //Indicator 1
        Class myClass = Class.forName(indicatorclasspaths.get(Indicators.getByCode(indicator1)));
        
        //--
        //There are 78 (excluded statistics & helpers) indicators. Could just write switch statement for all of them. Also won't need classpath, or indicator parameter hashmap.
        //Have it get the values in the switch. Store it in string array. Later on create the parameters from that like before, and still use classpath, to create indicator.
        //Also have code for custom ones, such as bolinger bands.
        //In the Indicators.java, maybe instead of doing the current code/string thing which is useless, have it be strings and their classpaths, so you can also use other folders.
        //Also have an hashmap(indicatorparamters) that lists the required parameters of each indicator. If it is something like closeprice or price list, don't ask user.
        //If not, ask the user in a dynamic array, with the values in the array string(in hashmap value) being the input parameters.
        //Do a similar thing for the rules, as there is just a few it won't be a problem. Pay atention to the constructors, as there can be multiple.
        //--
        

        Constructor constructor = myClass.getConstructors()[0];
        Object[] parameters = {closePrice, 10};
        Object firstindicator = constructor.newInstance(parameters);
        System.out.println("1: " + firstindicator);
        //Indicator 2
        Class myClass2 = Class.forName("org.ta4j.core.indicators." + indicator2);
        Constructor constructor2 = myClass2.getConstructor(Indicator.class, int.class);
        Object[] parameters2 = {closePrice, 10};
        Object secondindicator = constructor.newInstance(parameters);
        //Entry Rule
        Class myClass3 = Class.forName("org.ta4j.core.trading.rules." + entryRule);
        Constructor constructor3 = myClass3.getConstructor();
        Object[] parameters3 = {firstindicator, secondindicator};
        Object ruleentry = constructor.newInstance(parameters);
        
        System.out.println(ruleentry.toString());
    	}
    	for (Person person : BackExitTable.getItems()) {
    		
    	}

    	JSONObject backtestJSON = new JSONObject();
    	try {
			backtestJSON.put("base", BackBase.getText());
	    	backtestJSON.put("alt", BackAlt.getText());
	    	backtestJSON.put("request", "Historic");
	    	backtestJSON.put("TimeFrame", "1d");
	    	LocalDate ld = starttime.getValue();
	    	Calendar c =  Calendar.getInstance();
	    	//c.set(ld.getYear(), ld.getMonthValue() - 1, ld.getDayOfMonth());
	    	//Date date = c.getTime();
	    	backtestJSON.put("StartTime", starttime.getValue());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	//String historicalrequest = ("{\"Coin\":\"" + coin + "\",\"Exchanges\":\"" + exchange + "\",\"request\":\"Historic\",\"licenceKey\":\"" + licencekey + "\", \"Historic\":{\"StartTime\": \"" + starttime + "\", \"Timeframe\":\"" + timeframe + "\", \"Index\":" + index + "}}");
    	//SocketCommunication.out.print(backtestJSON.toString());
    	//SocketCommunication.out.flush();
    	
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
    	            createFormGUI(person);
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
            createFormGUI(person);
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
    
    public void createFormGUI(Person person) {
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
    	
        String[] parametersstring = new String[5];
        Object[] parameters = new Object[5];
        switch(indic1code) {
	        case "Accel":
	        	Object series = null;
				parameters[0] = series;
				parametersstring[0] = "timeFrameSma1";
				parametersstring[1]	= "timeFrameSma2";
	        	break;
	        case "AroonDown":
	        	parametersstring[0] = "timeFrame";
	        	break;
	        case "AroonOscil":
	        	break;
	        case "ArronUp":
	        	break;
	        case "ATR":
	        	break;
	        case "AWS":
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
        	}
        	createSpecificGui(parametersstring,parameters,1,vbox.getChildren(),vbox2.getChildren());
            Scene dialogScene = new Scene(hbox, 400, 300);
            dialog.setScene(dialogScene);
            dialog.show();
        }

	private void createSpecificGui(String[] parametersstring, Object[] parameters, int i,ObservableList<Node> observableList, ObservableList<Node> observableList2) {
		for (String x : parametersstring) {
			if (x!=null) {
				TextField textfield = new TextField();
				observableList.add(textfield);
				Label label = new Label(x);
				observableList2.add(label);
				parameters[i] = textfield;
				i++;
			}
		}
	}
}
