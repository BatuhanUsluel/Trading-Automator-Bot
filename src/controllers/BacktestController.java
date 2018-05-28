package controllers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXDatePicker;

import application.Main;
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
	@FXML
    public void initialize(){
		String css = this.getClass().getResource("/assets/datepicker.css").toExternalForm();
		Controller.scene.getStylesheets().add(css);;
		setUpEntryTable();
		setUpExitTable();
    }
	
    @FXML
    void addEntryRow(ActionEvent event) {
	    Person person = new Person("Susan Smith", Indicators.PPOIndicator.getCode(), true, Indicators.CCIIndicator.getCode(), TradingRules.IsEqualRule.getCode());
	    Backdataentry.add(person);
	    BackEntryTable.setItems(Backdataentry);
	    BackEntryTable.refresh();
    }

    @FXML
    void addExitRow(ActionEvent event) {
	    Person person = new Person("Susan Smith", Indicators.PPOIndicator.getCode(), true, Indicators.CCIIndicator.getCode(), TradingRules.IsEqualRule.getCode());
	    Backdataexit.add(person);
	    BackExitTable.setItems(Backdataexit);
	    BackExitTable.refresh();
    }

    @FXML
    void runBackTest(ActionEvent event) {
    	int EntrySize = BackEntryTable.getItems().size();
    	int ExitSize = BackExitTable.getItems().size();
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

        BackExitTable.setItems(Backdataexit);
 
        BackExitTable.getColumns().addAll(Indicator1, TradingRule, Indicator2, andor);
        String css = this.getClass().getResource("/assets/tableview.css").toExternalForm();
    }
}
