package controllers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;

import controllers.DashboardController.Person;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Separator;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

public class LiveController {

    @FXML private JFXButton RunTStop;
    @FXML private JFXComboBox<?> LiveExchange;
    @FXML private TextField LiveBase;
    @FXML private TextField LiveAlt;
    @FXML private TableView<Person> EntryTable = new TableView<Person>();
    @FXML private TableView<Person> ExitTable = new TableView<Person>();
    public static ObservableList<Person> data =  FXCollections.observableArrayList();
	@SuppressWarnings("unchecked")
	@FXML
    public void initialize(){
		System.out.println("TEST!!!!");
        TableColumn<Person, String> Indicator1 = new TableColumn<Person, String>("Indicator1");
        Indicator1.setCellValueFactory(new PropertyValueFactory<>("Indicator1"));
        
        TableColumn<Person, String> Indicator2 = new TableColumn<Person, String>("Indicator2");
        Indicator2.setCellValueFactory(new PropertyValueFactory<>("Indicator2"));
        
        TableColumn<Person, String> TradeRule = new TableColumn<Person, String>("TradeRule");
        TradeRule.setCellValueFactory(new PropertyValueFactory<>("TradeRule"));
        
        TableColumn<Person, String> ParameterRule = new TableColumn<Person, String>("ParameterRule");
        ParameterRule.setCellValueFactory(new PropertyValueFactory<>("ParameterRule"));
        
        String css = this.getClass().getResource("/assets/tableview.css").toExternalForm();
        //Entry Table
        //EntryTable.getStylesheets().setAll(css);
       // EntryTable.setItems(data);
        	
        EntryTable.getColumns().addAll(ParameterRule,Indicator1,TradeRule,Indicator2);
        //Exit Table
        //ExitTable.getStylesheets().setAll(css);
        //ExitTable.setItems(data);
        //ExitTable.getColumns().addAll(ParameterRule,Indicator1,TradeRule,Indicator2);
    }
	
    public static class Person {
    	private final SimpleStringProperty Indicator1;
    	private final SimpleStringProperty Indicator2;
    	private final SimpleStringProperty TradeRule;
    	private final SimpleStringProperty ParameterRule;
        private Person(String Indicator1, String Indicator2, String TradeRule, String ParameterRule) {
			this.Indicator1 = new SimpleStringProperty(Indicator1);
			this.Indicator2 = new SimpleStringProperty(Indicator2);
			this.TradeRule = new SimpleStringProperty(TradeRule);
			this.ParameterRule = new SimpleStringProperty(ParameterRule);
        }
        public String getIndicator1() {
            return Indicator1.get();
        }
        
        public String getIndicator2() {
            return Indicator2.get();
        }
        
        public String getTradeRule() {
            return TradeRule.get();
        }
        
        public String getParameterRule() {
            return ParameterRule.get();
        }
       
        
    }
}
