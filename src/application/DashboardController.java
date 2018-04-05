package application;

import java.util.concurrent.TimeUnit;

import com.jfoenix.controls.JFXButton;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import org.json.JSONException;
import org.json.JSONObject;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXComboBox;
import javafx.event.EventHandler;
import com.jfoenix.controls.JFXRadioButton;
import com.jfoenix.controls.JFXTextField;

import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;

public class DashboardController {
	 @FXML private TableView<Person> tableView = new TableView<Person>();
	
	@FXML
    public void initialize(){
		
        System.out.println("TEST!ASDADSA");
        tableEnable();
    }

    @SuppressWarnings("unchecked")
	@FXML
	 private void tableEnable() {
    	final ObservableList<Person> data
        = FXCollections.observableArrayList(
        		new Person("AverageTrading","BTC","OMG", "Bittrex", "28.2(5:30)", "NA", "Running","000000"),
        		new Person("Something","BTC","OMG", "EXCHNAGE", "28.2(5:30)", "NA", "Running","1111111"),
        		new Person("AverageTrading","BTC","OMG", "Bittrex", "28.2(5:30)", "NA", "Running","222222"),
        		new Person("Yes","BTC","OMG", "Bittrex", "28.2(5:30)", "NA", "Running","333333"),
        		new Person("SuchProfit","MuchWow","Doge", "Bittrex", "28.2(5:30)", "NA", "Running","44444")
        );
		System.out.println("TABLE2!");

		TableColumn<Person, String> OrderTypeCol = new TableColumn<Person, String>("Order Type");
		OrderTypeCol.setCellValueFactory(new PropertyValueFactory<>("OrderType"));
        
        TableColumn<Person, String> BasePairCol = new TableColumn<Person, String>("Base Pair");
        BasePairCol.setCellValueFactory(new PropertyValueFactory<>("BasePair"));

        TableColumn<Person, String> AltPairCol = new TableColumn<Person, String>("Alt Pair");
        AltPairCol.setCellValueFactory(new PropertyValueFactory<>("AltPair"));
        
        TableColumn<Person, String> ExchangesCol = new TableColumn<Person, String>("Exchanges");
        ExchangesCol.setCellValueFactory(new PropertyValueFactory<>("Exchanges"));
        
        TableColumn<Person, String> StartTimeCol = new TableColumn<Person, String>("Start Time");
        StartTimeCol.setCellValueFactory(new PropertyValueFactory<>("StartTime"));
        
        TableColumn<Person, String> EndTimeCol = new TableColumn<Person, String>("End Time");
        EndTimeCol.setCellValueFactory(new PropertyValueFactory<>("EndTime"));
        
        TableColumn<Person, String> RunningCol = new TableColumn<Person, String>("Running");
        RunningCol.setCellValueFactory(new PropertyValueFactory<>("Running"));
        
        TableColumn<Person, String> OrderIDCol = new TableColumn<Person, String>("OrderID");
        OrderIDCol.setCellValueFactory(new PropertyValueFactory<>("OrderID"));
        
        TableColumn<Person, String> actionCol = new TableColumn<Person, String>("Action");
        Callback<TableColumn<Person, String>, TableCell<Person, String>> cellFactory
                = //
                new Callback<TableColumn<Person, String>, TableCell<Person, String>>() {
            @Override
            public TableCell<Person, String> call(final TableColumn<Person, String> param) {
                final TableCell<Person, String> cell = new TableCell<Person, String>() {
                    final Button btn = new Button("Just Do It");
                    @Override
                   
                    public void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                            setText(null);
                        } else {
                            btn.setOnAction(event -> {
                                Person person = getTableView().getItems().get(getIndex());
                                
                                System.out.println(person.getOrderID());
                            });
                            setGraphic(btn);
                            setText(null);
                        }
                    }
                };
                return cell;
            }
        };
        actionCol.setCellFactory(cellFactory);
        System.out.println("TABLE30!");
        String css = this.getClass().getResource("/assets/tableview.css").toExternalForm();
        tableView.getStylesheets().setAll(css);
        tableView.setItems(data);
        tableView.getColumns().addAll(OrderTypeCol, BasePairCol, AltPairCol,ExchangesCol,StartTimeCol,EndTimeCol,RunningCol,OrderIDCol,actionCol);
	}
    
	@FXML
	private void tableUpdate() {
		ObservableList<Person> data2 = FXCollections.observableArrayList();
		data2.addAll(new Person("Market Making","BTC","ETH", "Bittrex", "28.2(5:30)", "NA", "Running","111111"));
		data2.addAll(new Person("Arbitrage","BTC","DCR", "Poloniex & Bittrex", "28.2(5:30)", "NA", "Running","22222"));
		data2.addAll(new Person("QuickBuy","BTC","XRP", "Binance", "28.2(5:30)", "28.2(5:30)", "Stopped" , "33333"));
		data2.addAll(new Person("AverageTrading","BTC","OMG", "Bittrex", "28.2(5:30)", "NA", "Running", "44444"));
		tableView.setItems(data2);
		tableView.refresh();
	}
    public static class Person {
    	private final SimpleStringProperty OrderType;
    	private final SimpleStringProperty BasePair;
    	private final SimpleStringProperty AltPair;
    	private final SimpleStringProperty Exchanges;
    	private final SimpleStringProperty StartTime;
    	private final SimpleStringProperty EndTime;
    	private final SimpleStringProperty Running;
    	private final SimpleStringProperty OrderID;
        private Person(String OrderType,String BasePair,String AltPair, String Exchanges, String StartTime, String EndTime, String Running, String OrderID) {
			this.OrderType = new SimpleStringProperty(OrderType);
			this.BasePair  = new SimpleStringProperty(BasePair);
			this.AltPair = new SimpleStringProperty(AltPair);
			this.Exchanges = new SimpleStringProperty(Exchanges);
			this.StartTime = new SimpleStringProperty(StartTime);
			this.EndTime = new SimpleStringProperty(EndTime);
			this.Running = new SimpleStringProperty(Running);
			this.OrderID = new SimpleStringProperty(OrderID);
        }
        
        public String getOrderType() {
            return OrderType.get();
        }

        public void setOrderType(String fName) {
        	OrderType.set(fName);
        }
        
        public String getOrderID() {
            return OrderID.get();
        }
        
        public String getBasePair() {
            return BasePair.get();
        }
        
        public String getAltPair() {
            return AltPair.get();
        }
        
        public String getExchanges() {
            return Exchanges.get();
        }
        
        public String getEndTime() {
            return EndTime.get();
        }
        
        public String getRunning() {
            return Running.get();
        }
        public String getStartTime() {
            return StartTime.get();
        }
    }
}
