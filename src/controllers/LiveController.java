package controllers;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.ta4j.core.Rule;
import org.ta4j.core.indicators.EMAIndicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.trading.rules.CrossedDownIndicatorRule;
import org.ta4j.core.trading.rules.CrossedUpIndicatorRule;

import org.ta4j.core.Decimal;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import org.ta4j.core.Indicator;
import controllers.DashboardController.Person;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.util.StringConverter;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Separator;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;
import javafx.scene.Node;
import javafx.beans.binding.Bindings;
public class LiveController {

    @FXML private JFXButton RunTStop;
    @FXML private JFXComboBox<?> LiveExchange;
    @FXML private TextField LiveBase;
    @FXML private TextField LiveAlt;
    @FXML private TableView<Person> EntryTable = new TableView<Person>();
    @FXML private TableView<Person> ExitTable = new TableView<Person>();
    public static ObservableList<Person> dataentry =  FXCollections.observableArrayList();
    public static ObservableList<Person> dataexit =  FXCollections.observableArrayList();
	@SuppressWarnings("unchecked")
	@FXML
    public void initialize() throws ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException{		 
        TableColumn Indicator1 = new TableColumn("Indicator1");
        TableColumn Indicator2 = new TableColumn("Indicator2");
        TableColumn ParameterRule = new TableColumn("ParameterRule");
        TableColumn TradeRule = new TableColumn("TradeRule");
        
        Indicator1.setCellValueFactory(
			    new PropertyValueFactory<Person,String>("Indicator1")
		);
        
        Indicator2.setCellValueFactory(
		    new PropertyValueFactory<Person,String>("Indicator2")
		);
        ParameterRule.setCellValueFactory(
		    new PropertyValueFactory<Person,String>("ParameterRule")
		);
        
        TradeRule.setCellValueFactory(
		    new PropertyValueFactory<Person,String>("TradeRule")
		);
        
        
        ObservableList<String> options = FXCollections.observableArrayList(
                "1",
                "2",
                "3"
                );
        
        TableColumn<Person, StringProperty> column = new TableColumn<>("option");
        column.setCellValueFactory(i -> {
            final StringProperty value = i.getValue().optionProperty();
            // binding to constant value
            return Bindings.createObjectBinding(() -> value);
        });

        column.setCellFactory(col -> {
            TableCell<Person, StringProperty> c = new TableCell<>();
            final ComboBox<String> comboBox = new ComboBox<>(options);
            c.itemProperty().addListener((observable, oldValue, newValue) -> {
                if (oldValue != null) {
                    comboBox.valueProperty().unbindBidirectional(oldValue);
                }
                if (newValue != null) {
                    comboBox.valueProperty().bindBidirectional(newValue);
                }
            });
            c.graphicProperty().bind(Bindings.when(c.emptyProperty()).then((Node) null).otherwise(comboBox));
            return c;
        });
        
        
        String css = this.getClass().getResource("/assets/tableview.css").toExternalForm();

        //Entry Table
        EntryTable.getStylesheets().setAll(css);
        EntryTable.setItems(dataentry); 	
        EntryTable.getColumns().addAll(Indicator1,Indicator2,ParameterRule,TradeRule,column);
        
        //Exit Table
        ExitTable.getStylesheets().setAll(css);
        ExitTable.setItems(dataexit);
        //ExitTable.getColumns().addAll(ParameterRule,TradeRule,Indicator2);
		ClosePriceIndicator closePrice = new ClosePriceIndicator(null);
        EMAIndicator shortEma = new EMAIndicator(closePrice, 9);
        EMAIndicator longEma = new EMAIndicator(closePrice, 26);
        System.out.println(shortEma.toString());
        
        //Rule entryRule = new CrossedUpIndicatorRule(shortEma, longEma);
        Class myClass = Class.forName("org.ta4j.core.indicators.EMAIndicator");
        Constructor constructor = myClass.getConstructor(Indicator.class, int.class);

        Object[] parameters = {closePrice, 10};
        Object instanceOfMyClass = constructor.newInstance(parameters);
        System.out.println(instanceOfMyClass.toString());
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
        
        //Dropdown
        private final StringProperty option = new SimpleStringProperty();

        public String getOption() {
            return option.get();
        }

        public void setOption(String value) {
            option.set(value);
        }

        public StringProperty optionProperty() {
            return option;
        }
        
    }
    @FXML
    public void addEntryRow(ActionEvent event){
	    Person person = new Person("test1","test2","test3","test4");
	    dataentry.add(person);
	    EntryTable.setItems(dataentry);
	    EntryTable.refresh();
    }
}
