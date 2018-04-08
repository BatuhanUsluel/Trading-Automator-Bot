package application;

import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;

public class DashboardController {
	@FXML private TableView<Person> tableView = new TableView<Person>();
	public static ObservableList<Person> data =  FXCollections.observableArrayList();
	@FXML
    public void initialize(){
		
        System.out.println("TEST!ASDADSA");
        tableEnable();
    }

    @SuppressWarnings("unchecked")
	@FXML
	 private void tableEnable() {

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
                    final Button btn = new Button("Stop");
                    @Override
                   
                    public void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                            setText(null);
                        } else {
                            btn.setOnAction(event -> {
                                Person person = getTableView().getItems().get(getIndex());
                                person.setRunning("False");
                                System.out.println(person.getOrderID());
                                btn.setVisible(false);
                                tableView.refresh();
                                try {
									RemoveOrder.removeOrder(person);
								} catch (JSONException e) {
									e.printStackTrace();
								}
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
        
        public void setRunning(String fName) {
        	Running.set(fName);
        }
        public String getStartTime() {
            return StartTime.get();
        }
        
    }
    
    public void newOrder(JSONObject json) throws JSONException {
    	data.add(new Person(json.getString("request"), json.getString("base"), json.getString("alt"), json.getString("Exchanges"), String.valueOf(json.getLong("millisstart")), "Endtime", "True", String.valueOf(json.getInt("orderid"))));
    	tableView.setItems(data);
    	tableView.refresh();
    }
}
