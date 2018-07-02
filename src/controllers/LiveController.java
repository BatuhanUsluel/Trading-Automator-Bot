package controllers;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.ta4j.core.Rule;
import org.ta4j.core.TimeSeries;
import org.ta4j.core.indicators.EMAIndicator;
import org.ta4j.core.indicators.StochasticOscillatorKIndicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.indicators.helpers.MedianPriceIndicator;
import org.ta4j.core.trading.rules.*;
import org.apache.commons.lang3.math.NumberUtils;
import org.json.JSONObject;
import org.ta4j.core.BaseTimeSeries;
import org.ta4j.core.Decimal;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;

import application.AverageTrading;
import application.Exchanges;
import application.FxDialogs;
import application.LiveTrading;
import application.Main;
import application.SocketCommunication;

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
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;
import controllers.Indicators;
import controllers.Person;
public class LiveController {

    @FXML private JFXButton RunTStop;
    @FXML private JFXComboBox<String> LiveExchange;
    @FXML private JFXComboBox<String> timeframefx;
    @FXML private TextField LiveBase;
    @FXML private TextField LiveAlt;
    @FXML private TableView<Person> EntryTable = new TableView<Person>();
    @FXML private TableView<Person> ExitTable = new TableView<Person>();
    public static ObservableList<Person> dataentry =  FXCollections.observableArrayList();
    public static ObservableList<Person> dataexit =  FXCollections.observableArrayList();
	private int candles;
	private String base;
	private String alt;
	private String exchange;
	private LocalDate timestart;
	public static HashMap<Integer, LiveTrading> LiveTradingMap = new HashMap<Integer, LiveTrading>();
	@SuppressWarnings("unchecked")
	@FXML
    public void initialize() throws ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException{		 
		List<String> list = new ArrayList<String>(Exchanges.list);
		LiveExchange.getItems().addAll(list);
		List<String> timelist = new ArrayList<String>();
        for (String key : IndicatorMaps.timeframes.keySet()) {
        	timelist.add(key);
        }
        timeframefx.getItems().addAll(timelist);
        setUpEntryTable();
        setUpExitTable();
	}
    
	@FXML
    void runLiveTrade(ActionEvent event) throws NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, ClassNotFoundException {  	
    	String base = LiveBase.getText();
    	String alt = LiveAlt.getText();
    	boolean noerror = true;
		StringBuilder stringBuilder = new StringBuilder();
    	for (Person person : EntryTable.getItems()) {
    		String indicator1 = person.getIndicator1();
    		String indicator2 = person.getIndicator2();
    		if (indicator1.equals("Select") || indicator2.equals("Select")) {
    			noerror=false;
    			stringBuilder.append("Please select an indicator. Indicator can't be 'Select'\n");
    		}
    	}
    	for (Person person : ExitTable.getItems()) {
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
    		exchange = LiveExchange.getValue().toString();
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
    	if (dataentry.get(0).isor()==true) {
    		noerror=false;
    		stringBuilder.append("First row in entry can not have 'or?' enabled\n");
    	}
    	if (dataexit.get(0).isor()==true) {
    		noerror=false;
    		stringBuilder.append("First row in exitcan not have 'or?' enabled\n");
    	}
    	if (noerror==true) {
    	JSONObject liveJSON = new JSONObject();
    	try {
    		
    		int timeframe = IndicatorMaps.timeframes.get(timeframefx.getValue().toString());
			liveJSON.put("base", base);
			liveJSON.put("alt", alt);
			liveJSON.put("request", "LiveTrading");
			liveJSON.put("Exchanges", exchange);
			liveJSON.put("Timeframe", timeframefx.getValue().toString());
			liveJSON.put("licenceKey", SocketCommunication.licencekey);
			liveJSON.put("millisstart", System.currentTimeMillis());
	    	Random rand = new Random();
	    	int value = rand.nextInt(1000000000);
	    	liveJSON.put("orderid", value);
	    	liveJSON.put("endtime","N/A");
	    	liveJSON.put("running","True");
	    	LiveTrading livetradingclass = new LiveTrading(liveJSON,dataentry,dataexit);
	    	LiveTradingMap.put(value, livetradingclass);
	    	Thread t = new Thread(livetradingclass);
	    	t.start();
		} catch (Exception e) {
			e.printStackTrace();
		}  	
    	SocketCommunication.out.print(liveJSON.toString());
    	SocketCommunication.out.flush();
    	} else {
    		String finalString = stringBuilder.toString();
	    	FxDialogs.showError(null, finalString);
    	}
    	
    }

    @FXML
    void addEntryRow(ActionEvent event) {
	    Person person = new Person(Indicators.Select.getCode(), false, Indicators.Select.getCode(), TradingRules.CrossedUpIndicatorRule.getCode(), null, null, null, null);
	    dataentry.add(person);
	    EntryTable.setItems(dataentry);
	    EntryTable.refresh();
    }

    @FXML
    void addExitRow(ActionEvent event) {
	    Person person = new Person(Indicators.Select.getCode(), false, Indicators.Select.getCode(), TradingRules.CrossedDownIndicatorRule.getCode(), null,null, null, null);
	    dataexit.add(person);
	    ExitTable.setItems(dataexit);
	    ExitTable.refresh();
    }
    
    void setUpEntryTable() {
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

        EntryTable.setItems(dataentry);
        EntryTable.getColumns().addAll(Indicator1, TradingRule, Indicator2, andor);
        String css = this.getClass().getResource("/assets/tableview.css").toExternalForm();
    }
    
    void setUpExitTable() {
    	// Editable
		ExitTable.setEditable(true);
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

        ExitTable.setItems(dataexit);
 
        ExitTable.getColumns().addAll(Indicator1, TradingRule, Indicator2, andor);
        String css = this.getClass().getResource("/assets/tableview.css").toExternalForm();
    }
        
    public void createFormGUI(Person person, int i, String code) {
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
        System.out.println(indicstring);
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
	
}
