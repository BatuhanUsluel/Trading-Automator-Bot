package controllers;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

import org.ta4j.core.Rule;
import org.ta4j.core.indicators.EMAIndicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.trading.rules.*;

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
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import controllers.Indicators;
import controllers.Person;
public class LiveController {

    @FXML private JFXButton RunTStop;
    @FXML private JFXComboBox<?> LiveExchange;
    @FXML private TextField LiveBase;
    @FXML private TextField LiveAlt;
    @FXML private TableView<Person> EntryTable = new TableView<Person>();
    @FXML private TableView<Person> ExitTable = new TableView<Person>();
    public static ObservableList<Person> dataentry =  FXCollections.observableArrayList();
    public static ObservableList<Person> dataexit =  FXCollections.observableArrayList();
    HashMap<String, String[]> indicatorparameters = new HashMap<String, String[]>();

	@SuppressWarnings("unchecked")
	@FXML
    public void initialize() throws ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException{		 
		indicatorparameters.put("Accel", new String[]{"timeFrameSma1","timeFrameSma2"});
		indicatorparameters.put("AroonDown", new String[]{"timeFrame"});
		indicatorparameters.put("AroonOscil", new String[]{"timeFrame"});
		indicatorparameters.put("ArronUp", new String[]{"timeFrame"});
		indicatorparameters.put("ATR", new String[]{"timeFrame"});
		// Editable
		EntryTable.setEditable(true);
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
 
                SimpleBooleanProperty booleanProp = new SimpleBooleanProperty(person.isSingle());
 
                // Note: singleCol.setOnEditCommit(): Not work for
                // CheckBoxTableCell.
 
                // When "Single?" column change.
                booleanProp.addListener(new ChangeListener<java.lang.Boolean>() {
 
                    @Override
                    public void changed(ObservableValue<? extends java.lang.Boolean> observable, java.lang.Boolean oldValue,
                    		java.lang.Boolean newValue) {
                        person.setSingle(newValue);
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
 
        ObservableList<Person> list = getPersonList();
        EntryTable.setItems(list);
 
        EntryTable.getColumns().addAll(Indicator1, TradingRule, Indicator2, andor);
        String css = this.getClass().getResource("/assets/tableview.css").toExternalForm();

        /*Entry Table
        EntryTable.getStylesheets().setAll(css);
        EntryTable.setItems(dataentry); 	
        EntryTable.getColumns().addAll(Indicator1,Indicator2,ParameterRule,TradeRule);
        
        //Exit Table
        ExitTable.getStylesheets().setAll(css);
        ExitTable.setItems(dataexit);
        //ExitTable.getColumns().addAll(ParameterRule,TradeRule,Indicator2);
		ClosePriceIndicator closePrice = new ClosePriceIndicator(null);
        EMAIndicator shortEma = new EMAIndicator(closePrice, 9);
        EMAIndicator longEma = new EMAIndicator(closePrice, 26);
        System.out.println(shortEma.toString());
        
        Rule entryRule = new CrossedUpIndicatorRule(shortEma, longEma);
        Rule test = new IsFallingRule(shortEma, 1,2);
        Class myClass = Class.forName("org.ta4j.core.indicators.EMAIndicator");
        Constructor constructor = myClass.getConstructor(Indicator.class, int.class);

        Object[] parameters = {closePrice, 10};
        Object instanceOfMyClass = constructor.newInstance(parameters);
        System.out.println(instanceOfMyClass.toString());
        */
    }

    private ObservableList<Person> getPersonList() {
    	 
        Person person1 = new Person("Susan Smith", Indicators.ROCIndicator.getCode(), true, Indicators.DPOIndicator.getCode(), TradingRules.UnderIndicatorRule.getCode());
        Person person2 = new Person("Anne McNeil", Indicators.DPOIndicator.getCode(), true, Indicators.ROCIndicator.getCode(), TradingRules.OverIndicatorRule.getCode());
        Person person3 = new Person("Kenvin White", Indicators.CCIIndicator.getCode(), false, Indicators.CCIIndicator.getCode(), TradingRules.UnderIndicatorRule.getCode());
 
        ObservableList<Person> list = FXCollections.observableArrayList(person1, person2, person3);
        return list;
    }
    
    @FXML
    public void addEntryRow(ActionEvent event){
	    Person person = new Person("Susan Smith", Indicators.PPOIndicator.getCode(), true, Indicators.CCIIndicator.getCode(), TradingRules.IsEqualRule.getCode());
	    dataentry.add(person);
	    EntryTable.setItems(dataentry);
	    EntryTable.refresh();
    }
}
