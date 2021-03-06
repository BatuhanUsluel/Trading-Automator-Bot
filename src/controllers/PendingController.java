package controllers;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.logging.Level;

import org.apache.commons.lang3.math.NumberUtils;
import org.controlsfx.control.NotificationPane;
import org.json.JSONException;
import org.json.JSONObject;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXRadioButton;

import application.Exchanges;
import application.FxDialogs;
import application.Main;
import application.PendingOrder;
import application.SocketCommunication;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.AnchorPane;
public class PendingController {
    
//Pending Order
	public static HashMap<JSONObject, PendingOrder> PendingOrderMap = new HashMap<JSONObject, PendingOrder>();

    @FXML private TextField POBPU;
    @FXML private TextField APPOU;
    @FXML private TextField PPOU;
    @FXML private TextField BVPOU;
    @FXML private JFXComboBox<String> ExPOU;
    @FXML private TextField POVPOU;
    @FXML private JFXButton RunPO;
    @FXML private JFXRadioButton SellPO;
    @FXML private JFXRadioButton BuyPO;
    @FXML private ToggleGroup toggleGroupPO;
    @FXML private AnchorPane PAnchor;
    
    NotificationPane notificationPane;
    @FXML
    public void pendingOrder(ActionEvent event) throws JSONException  {
    	boolean noerror=true;
    	boolean noexchange = false;
    	String exchange = "";
    	StringBuilder stringBuilder = new StringBuilder();
    	JSONObject pendingOrder = new JSONObject();
    	String base = POBPU.getText().toUpperCase();
    	String alt = APPOU.getText().toUpperCase();
    	String price = PPOU.getText();
    	String volume = BVPOU.getText();
    	String percent = POVPOU.getText();
    	String buysell = ((RadioButton) toggleGroupPO.getSelectedToggle()).getText();
    	
    	try {
    		exchange = ExPOU.getValue().toString();
    	} catch (NullPointerException e) {
    		noerror=false;
    		noexchange=true;
    		stringBuilder.append("Please enter a exchange\n");
    	}
    	
    	if (!Exchanges.list.contains(exchange) && noexchange!=true) {
    		noerror=false;
    		stringBuilder.append(exchange + " is not a valid exchange.\n");
    	}
    	
    	if(!NumberUtils.isCreatable(price)) {
    		noerror=false;
    		stringBuilder.append(price + " is not a valid number(price).\n");
    	}
    	
    	if(!NumberUtils.isCreatable(volume)) {
    		noerror=false;
    		stringBuilder.append(volume + " is not a valid number(volume).\n");
    	}
    	
    	if(!NumberUtils.isCreatable(percent)) {
    		noerror=false;
    		stringBuilder.append(percent + " is not a valid number(percent).\n");
    	}
    	
		if (noerror==true) {
			String confirm = ("Base: " + base + "\nAlt: " + alt + "\nPrice: " + price + "\nVolume" + volume + "\nPercent: " + percent + "\nBuySell: " + buysell + "\nExchange:" + exchange);
			String run = FxDialogs.showConfirm("Run Order?",confirm, "Run", "Cancel");
    		System.out.println(run);
			if (run.equals("Run")) {
		    	pendingOrder.put("base",base);
		    	pendingOrder.put("alt",alt);
		    	pendingOrder.put("request","pendingOrder");
		    	pendingOrder.put("priceorder",price);
		    	pendingOrder.put("volume",volume);
		    	pendingOrder.put("percent",percent);
		    	pendingOrder.put("licenceKey", SocketCommunication.licencekey);
		    	pendingOrder.put("millisstart", System.currentTimeMillis());
		    	pendingOrder.put("buysell",buysell);
		    	pendingOrder.put("Exchanges",exchange);
		    	pendingOrder.put("endtime","N/A");
		    	Random rand = new Random(); 
		    	int value = rand.nextInt(1000000000); 
		    	pendingOrder.put("orderid", value);
		    	pendingOrder.put("running","True");
		    	PendingOrder pend = new PendingOrder(pendingOrder);
		    	PendingOrderMap.put(pendingOrder, pend);
		    	Thread t = new Thread(pend);
		    	t.start();
			} else {
				Main.logger.log(Level.INFO, "Pending order canceled from dialog");
				System.out.println("Not running order");
			}
    	} else {
    		String finalString = stringBuilder.toString();
    		FxDialogs.showError(null, finalString);
    	}
    } 
	@FXML
    public void initialize(){
        List<String> list = new ArrayList<String>(Exchanges.list);
        ExPOU.getItems().addAll(list);
    }
	
	public static void cancelPendingOrder(String orderid) {
		for (Map.Entry<JSONObject, PendingOrder> entry : PendingOrderMap.entrySet()) {
		    JSONObject key = entry.getKey();
			try {
				if (key.getInt("orderid") == java.lang.Integer.parseInt(orderid)) {
					PendingOrder value = entry.getValue();
					value.stopOrder();
					Main.logger.log(Level.INFO, "Stopped pending order");
				}
			} catch (java.lang.Exception e) {
				e.printStackTrace();
			}

	}
	}

}
