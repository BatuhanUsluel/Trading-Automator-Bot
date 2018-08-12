package controllers;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import org.controlsfx.control.MasterDetailPane;
import org.controlsfx.control.table.TableFilter;
import org.json.JSONException;
import org.json.JSONObject;
import org.knowm.xchange.exceptions.ExchangeException;
import org.knowm.xchange.exceptions.NotAvailableFromExchangeException;
import org.knowm.xchange.exceptions.NotYetImplementedForExchangeException;

import application.FxDialogs;
import application.RemoveOrder;
import application.ShowInfo;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Side;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Font;
import javafx.util.Callback;

public class DashboardController {
	@FXML private TableView<Person> tableView = new TableView<Person>();
    @FXML private MasterDetailPane pane;
	public static ObservableList<Person> data =  FXCollections.observableArrayList();
	@FXML
    public void initialize(){
        try {
			tableEnable();
		} catch (NotAvailableFromExchangeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NotYetImplementedForExchangeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExchangeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    @SuppressWarnings("unchecked")
	@FXML
	 private void tableEnable() throws NotAvailableFromExchangeException, NotYetImplementedForExchangeException, ExchangeException, IOException {

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
                = new Callback<TableColumn<Person, String>, TableCell<Person, String>>() {
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
                                System.out.println(person.getRunning());
                                if(!person.getRunning().equals("False")) {
	                                long millis = System.currentTimeMillis();
	                            	Date date = new Date(millis);
	                            	SimpleDateFormat format = new SimpleDateFormat("dd/MM hh:mm:ss", Locale.US);
	                            	String text = format.format(date);
	                                person.setEndTime(text);
	                                System.out.println(person.getOrderID());
	                                btn.setVisible(false);
	                                tableView.refresh();
	                                try {
										RemoveOrder.removeOrder(person);
								    	//filter.executeFilter();
								    	//filter = new TableFilter<Person>(tableView);
								    	tableView.refresh();
									} catch (JSONException e) {
										e.printStackTrace();
									}
                                } else {
                                	FxDialogs.showError(null, "Order Already Stopped");
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
        
        TableColumn<Person, String> infoCol = new TableColumn<Person, String>("Info");
        Callback<TableColumn<Person, String>, TableCell<Person, String>> infoCellFactory
                = new Callback<TableColumn<Person, String>, TableCell<Person, String>>() {
            @Override
            public TableCell<Person, String> call(final TableColumn<Person, String> param) {
                final TableCell<Person, String> cell = new TableCell<Person, String>() {
                    final Button btn = new Button("info");
                    @Override
                   
                    public void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                            setText(null);
                        } else {
                            btn.setOnAction(event -> {
                                Person person = getTableView().getItems().get(getIndex());
                                System.out.println(person.getRunning());
	                                System.out.println(person.getOrderID());
	                                btn.setVisible(false);
	                                try {
										ShowInfo.showInfo(person);
								    	//filter.executeFilter();
								    	//filter = new TableFilter<Person>(tableView);
								    	tableView.refresh();
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
        infoCol.setCellFactory(infoCellFactory);
        
        
        System.out.println("TABLE30!");
        String css = this.getClass().getResource("/assets/tableview.css").toExternalForm();
        tableView.getStylesheets().setAll(css);
        tableView.setItems(data);
        tableView.getColumns().addAll(OrderTypeCol,actionCol, infoCol, BasePairCol, AltPairCol,ExchangesCol,StartTimeCol,RunningCol,EndTimeCol,OrderIDCol);
        TextArea textArea = new TextArea();
	   	pane.setMasterNode(tableView);
	   	pane.setDetailNode(textArea);
	   	textArea.setFont(Font.font ("Courier New", 15));
	   	pane.setDetailSide(Side.RIGHT);
	   	pane.setShowDetailNode(true);
	    pane.setDividerPosition(0.64);
	   	tableView.setRowFactory(tv -> {
            TableRow<Person> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if ((!row.isEmpty()) ) {
                    Person rowData = row.getItem();
                    System.out.println("Selected: "+rowData.getOrderType());
                    textArea.setText(rowData.getOrderData());
                }
            });
            return row ;
        });
	   	Thread selectedrow = new Thread() {
	   	    public void run() {
	   	    	while(true) {
		   	    	Person person = tableView.getSelectionModel().getSelectedItem();
		   	    	if (person!=null) {
		   	    		textArea.setText(person.getOrderData());
		   	    		textArea.setScrollTop(Double.MAX_VALUE);
		   	    	}
		   	    	try {
						TimeUnit.SECONDS.sleep(2);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
	   	    }
	   	    }
	   	};
	   	selectedrow.start();
	   	
        //filter = new TableFilter<Person>(tableView);
	   	try {
	   		//TableFilter.forTableView(tableView).apply();
	   	} catch (NoSuchMethodError e) {
	   		
	   	}
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
    	private final SimpleStringProperty OrderData;
        private Person(String OrderType,String BasePair,String AltPair, String Exchanges, String StartTime, String EndTime, String Running, String OrderID, String OrderData) {
			this.OrderType = new SimpleStringProperty(OrderType);
			this.BasePair  = new SimpleStringProperty(BasePair);
			this.AltPair = new SimpleStringProperty(AltPair);
			this.Exchanges = new SimpleStringProperty(Exchanges);
			this.StartTime = new SimpleStringProperty(StartTime);
			this.EndTime = new SimpleStringProperty(EndTime);
			this.Running = new SimpleStringProperty(Running);
			this.OrderID = new SimpleStringProperty(OrderID);
			this.OrderData = new SimpleStringProperty(OrderData);
        }
        
        public String getOrderType() {
            return OrderType.get();
        }

        public String getOrderData() {
            return OrderData.get();
        }
        
        public void setOrderData(String fName) {
        	OrderData.set(fName);
        }
        
        public void addOrderData(String fName) {
        	OrderData.set(getOrderData() + fName);
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
        public void setEndTime(String fName) {
        	EndTime.set(fName);
        }
        
    }
    
    public Person newOrder(JSONObject json) throws JSONException { 
    	Date date = new Date(json.getLong("millisstart"));
    	SimpleDateFormat format = new SimpleDateFormat("dd/MM hh:mm:ss", Locale.US);
    	String text = format.format(date);
    	Person person = new Person(json.getString("request"), json.getString("base"), json.getString("alt"), json.get("Exchanges").toString(),text, json.getString("endtime"), json.getString("running"), String.valueOf(json.getInt("orderid")),"");
    	data.add(person);
    	tableView.setItems(data);
    	tableView.refresh();
    	return person;
    }
}
