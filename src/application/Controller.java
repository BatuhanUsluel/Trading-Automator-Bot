package application;

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

public class Controller {
	QuickBuy quickbuy = new QuickBuy();
	AverageTrading averageTrading = new AverageTrading();
	public static HashMap<JSONObject, TrailingStop> TrailingStopMap = new HashMap<JSONObject, TrailingStop>();
	public static HashMap<JSONObject, PendingOrder> PendingOrderMap = new HashMap<JSONObject, PendingOrder>();
	public static HashMap<JSONObject, MarketMaking> marketMakingMap = new HashMap<JSONObject, MarketMaking>();
	
    private Main application;
    private Scene scene;
    public Stage primaryStage;
    
	
	 
	 /*private final ObservableList<Person> data =
		        FXCollections.observableArrayList(
		            new Person("AverageTrading","BTC","OMG", "Bittrex", "28.2(5:30)", "NA", "Running")
		        );
	 */
    //Main
		 @FXML private PasswordField Pass;
		 @FXML private TextField Email;
		 @FXML private JFXButton LoggedIn;
		 @FXML private JFXCheckBox RememberMe;
		 @FXML private JFXButton SignUp;
		 @FXML private BorderPane mainView;

	 //Navigation
		 @FXML private JFXButton arbitrage;
		 @FXML private JFXButton mmake;
		 @FXML private JFXButton backtest;
		 @FXML private JFXButton lives;
		 @FXML private JFXButton home;
		 @FXML private JFXButton avtrad;
		 @FXML private JFXButton qbuy;
		 @FXML private JFXButton porder;
		 @FXML private JFXButton tstop;
		 @FXML private JFXButton fobook;
		 @FXML private JFXButton settings;
   
	 //Average Trading
	 	@FXML private Label BPAv;
	    @FXML private TextField BPAvU;
	    @FXML private Label APAv;
	    @FXML private TextField APAvU;
	    @FXML private Label LTAv;
	    @FXML private TextField LPAvU;
	    @FXML private JFXRadioButton SAv;
	    @FXML private ToggleGroup toggleBAAv;
	    @FXML private ToggleGroup toggleBSAv;
	    @FXML private JFXRadioButton BAv;
	    @FXML private JFXRadioButton AskAv;
	    @FXML private JFXRadioButton BidAv;
	    @FXML private Label ExLabelAv;
	    @FXML private JFXComboBox<String> ExAv;
	    @FXML private JFXButton StartAv;
	    @FXML private Label MBAv;
	    @FXML private TextField VpOAV;
	    @FXML private JFXTextField MABAv;

	//QuickBuy
	    @FXML private TextField qBase;
	    @FXML private TextField qAlt;
	    @FXML private TextField qVolume;
	    @FXML private TextField qBAA;
	    @FXML private JFXButton RunQBuy;
	    @FXML private JFXComboBox<?> qEx;		    
	    
	//Fill Order Book
	    @FXML private TextField FOBase;
	    @FXML private TextField FOAlt;
	    @FXML private TextField FOStartP;
	    @FXML  private TextField FOEndP;
	    @FXML private TextField FOBalanceUsed;
	    @FXML private TextField FONumberOrders;
	    @FXML private JFXRadioButton SellFO;
	    @FXML private ToggleGroup FoBuySell;
	    @FXML private JFXRadioButton BuyFO;
	    @FXML private JFXButton RunFOBook;
	    @FXML  private JFXComboBox<?> FOEx;
	    
	//Trailing Stop
	    @FXML private TextField TStopBase;
	    @FXML private TextField TStopAlt;
	    @FXML private TextField TStopVolume;
	    @FXML private TextField TStopTrail;
	    @FXML private JFXButton RunTStop;
	    @FXML private JFXComboBox<?> TStopExchange;
	    @FXML private JFXRadioButton TStopSell;
	    @FXML private ToggleGroup TStopToggleBS;
	    @FXML private JFXRadioButton TStopBuy;
	    
	//Pending Order
	    @FXML private TextField POBPU;
	    @FXML private TextField APPOU;
	    @FXML private TextField PPOU;
	    @FXML private TextField BVPOU;
	    @FXML private JFXComboBox<?> ExPOU;
	    @FXML private TextField POVPOU;
	    @FXML private JFXButton RunPO;
	    @FXML private JFXRadioButton SellPO;
	    @FXML private JFXRadioButton BuyPO;
	    @FXML private ToggleGroup toggleGroupPO;
	
	//Market Making
	    @FXML private TextField BaseMM;
	    @FXML private TextField AltMM;
	    @FXML private JFXButton RunMM;
	    @FXML private JFXComboBox<?> ExchangeMM;
	    @FXML private TextField SpreadMM;
	    @FXML private TextField MaxBalMM;
	    @FXML private TextField MinBalMM;
	    

	@FXML
    private void handleChangeView(ActionEvent event) {
    	System.out.println("Changing");
        try {
            String menuItemID = ((JFXButton) event.getSource()).getId();
            System.out.println(menuItemID);
            //if (menuItemID.toString() == "avtrad") {
            	System.out.println("Adding ComboBox");
            	final JFXComboBox<String> ExAv = new JFXComboBox<String>();
                ExAv.getItems().addAll(
                		"Highest",
                        "High",
                        "Normal",
                        "Low",
                        "Lowest" 
                    );   
            //}
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/" + menuItemID + ".fxml"));
            loader.setController(this);
            
            System.out.println(loader);
            mainView.setCenter(loader.load()); 
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
	
	@FXML
    @SuppressWarnings("unchecked")
    private void DashBoard(ActionEvent event) throws IOException
    {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/home.fxml"));
		mainView.setCenter(loader.load());
		System.out.println("TABLE1!");
    }
	
    @FXML
    private void LogIn(ActionEvent event) throws IOException
    {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/LoggedIn.fxml"));
        AnchorPane rootLayout = loader.load(); 
        Scene scene = new Scene(rootLayout);
        primaryStage.setScene(scene);
        primaryStage.show();
        SocketCommunication.setup();
    }
    public void quickPrice(ActionEvent event) {
   	 System.out.println("running quickPrice");
   	 //quickbuy.sendQuickPriceRequest(qBase.getText(),qAlt.getText(),qEx.getValue().toString(), Double.parseDouble(qVolume.getText()), Double.parseDouble(qBAA.getText()));
   	quickbuy.sendQuickPriceRequest(qBase.getText(),qAlt.getText(),"bittrex", Double.parseDouble(qVolume.getText()), Double.parseDouble(qBAA.getText()));
 	}    
    public void trailingStop(ActionEvent event) throws JSONException {
    	JSONObject trailingStop = new JSONObject();
    	String base = TStopBase.getText();
    	String alt = TStopAlt.getText();
    	String volume = TStopVolume.getText();
    	String trail = TStopTrail.getText();
    	String buysell = ((RadioButton) TStopToggleBS.getSelectedToggle()).getText();
    	//String exchange =TStopExchange.getValue().toString());
    	String exchange = "bittrex";
    	
    	trailingStop.put("base", base);
    	trailingStop.put("alt", alt);
    	trailingStop.put("request", "trailingStop");
    	trailingStop.put("volume", volume);
    	trailingStop.put("trail", trail);
    	trailingStop.put("buysell", buysell);
    	trailingStop.put("exchange",exchange);
    	trailingStop.put("licenceKey", SocketCommunication.licencekey);
    	trailingStop.put("millisstart", System.currentTimeMillis());
    	TrailingStop trailingstopclass = new TrailingStop();
    	trailingstopclass.runOrder(trailingStop);
    	TrailingStopMap.put(trailingStop, trailingstopclass);
    }  
    public void averageTrading(ActionEvent event) throws JSONException {
      	 System.out.println("running averageTrading");
     	JSONObject averageTrading = new JSONObject();
     	averageTrading.put("Basecoin", BPAvU.getText());
     	averageTrading.put("Altcoin", APAvU.getText());
     	//averageTrading.put("Exchanges", ExAv.getValue().toString());
     	averageTrading.put("Exchanges", "bittrex");
     	averageTrading.put("request", "averageTrading");
     	averageTrading.put("coinstotrade", MABAv.getText());
     	averageTrading.put("volumeperorder", VpOAV.getText());
     	averageTrading.put("licenceKey", SocketCommunication.licencekey);
     	averageTrading.put("millisstart", System.currentTimeMillis());
     	averageTrading.put("atbid", ((RadioButton) toggleBAAv.getSelectedToggle()).getText());
     	averageTrading.put("buy", ((RadioButton) toggleBSAv.getSelectedToggle()).getText());
     	averageTrading.put("loop", LPAvU.getText());
     	System.out.println("--------------");
     	System.out.println(averageTrading);
      	AverageTrading.runOrder(averageTrading);
    	}
    public void pendingOrder(ActionEvent event) throws JSONException  {
    	JSONObject pendingOrder = new JSONObject();
    	String base = POBPU.getText();
    	String alt = APPOU.getText();
    	String price = PPOU.getText();
    	String volume = BVPOU.getText();
    	String percent = POVPOU.getText();
    	String buysell = ((RadioButton) toggleGroupPO.getSelectedToggle()).getText();
    	//String exchange = ExPOU.getValue().toString();
    	String exchange = "bittrex";
    	
    	pendingOrder.put("base",base);
    	pendingOrder.put("alt",alt);
    	pendingOrder.put("request","pendingOrder");
    	pendingOrder.put("priceorder",price);
    	pendingOrder.put("volume",volume);
    	pendingOrder.put("percent",percent);
    	pendingOrder.put("licenceKey", SocketCommunication.licencekey);
    	pendingOrder.put("millisstart", System.currentTimeMillis());
    	pendingOrder.put("buysell",buysell);
    	pendingOrder.put("exchange",exchange);
    	//PendingOrder pendingorderclass = new PendingOrder(pendingOrder);
    	PendingOrder pend = new PendingOrder(pendingOrder);
    	PendingOrderMap.put(pendingOrder, pend);
    	Thread t = new Thread(pend);
    	t.start();
    } 
    public void marketMaking(ActionEvent event)  throws JSONException {
    	JSONObject marketMaking = new JSONObject();
    	String base = BaseMM.getText();
    	String Alt = AltMM.getText();
    	String Spread = SpreadMM.getText();
    	String MaxBal = MaxBalMM.getText();
    	String MinBal = MinBalMM.getText();
    	//String exchange = ExchangeMM.getValue().toString();
    	String exchange = "bittrex";

		marketMaking.put("base", base);
    	marketMaking.put("alt", Alt);
    	marketMaking.put("spread", Spread);
    	marketMaking.put("MaxBal", MaxBal);
    	marketMaking.put("MinBal", MinBal);
    	marketMaking.put("exchange", exchange);
    	marketMaking.put("licencekey", SocketCommunication.licencekey);
    	marketMaking.put("millisstart", System.currentTimeMillis());
    	marketMaking.put("request","marketMaking");
    	MarketMaking market = new MarketMaking(marketMaking);
    	marketMakingMap.put(marketMaking, market);
    	Thread t = new Thread(market);
    	t.start();
    }    
    public void fillOrderBook(ActionEvent event) {
    	String base = FOBase.getText();
    	String alt = FOAlt.getText();
    	String startprice = FOStartP.getText();
    	String endprice = FOEndP.getText();
    	String balanceused = FOBalanceUsed.getText();
    	String nooforders = FONumberOrders.getText();
    	String BuySell = ((RadioButton) FoBuySell.getSelectedToggle()).getText();
    	//String exchange = FOEx.getValue().toString();
    	String exchange = "bittrex";
    	//Add check for valid inputs!!
    	try {
			FillOrderBook.fillOrderBook(base, alt, startprice,endprice, balanceused, nooforders, BuySell, exchange);
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    public void setPrimaryStage(Stage stage) {
    	  this.primaryStage = stage;
    	}

}


