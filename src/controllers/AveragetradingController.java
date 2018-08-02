package controllers;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.logging.Level;

import org.apache.commons.lang3.math.NumberUtils;
import org.json.JSONException;
import org.json.JSONObject;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXRadioButton;
import com.jfoenix.controls.JFXTextField;

import application.AverageTrading;
import application.Exchanges;
import application.FxDialogs;
import application.Main;
import application.SocketCommunication;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
public class AveragetradingController {
	
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
    
	@FXML
    public void initialize(){
    	System.out.println("Adding ComboBox");
        List<String> list = new ArrayList<String>(Exchanges.list);
        ExAv.getItems().addAll(list);
    }
	public static HashMap<JSONObject, AverageTrading> AverageTradingMap = new HashMap<JSONObject, AverageTrading>();
	@FXML
    public void averageTrading(ActionEvent event) throws JSONException {
     	System.out.println("running averageTrading");
    	JSONObject averageTrading = new JSONObject();
    	String base = BPAvU.getText().toUpperCase();
    	String alt =APAvU.getText().toUpperCase();
    	
    	String coinstotrade = MABAv.getText();
    	String volumeperorder = VpOAV.getText();
    	String loop = LPAvU.getText();
    	String atbid = ((RadioButton) toggleBAAv.getSelectedToggle()).getText();
    	String buy = ((RadioButton) toggleBSAv.getSelectedToggle()).getText();
    	
    	boolean noerror = true;
		StringBuilder stringBuilder = new StringBuilder();
		boolean noexchange = false;
    	String exchange = "";
     	
    	try {
    		exchange = ExAv.getValue().toString();
    	} catch (NullPointerException e) {
    		noerror=false;
    		noexchange=true;
    		stringBuilder.append("Please enter a exchange\n");
    	}
    	
    	if (!Exchanges.list.contains(exchange) && noexchange!=true) {
    		noerror=false;
    		stringBuilder.append(exchange + " is not a valid exchange.\n");
    	}
    	
    	if(!NumberUtils.isCreatable(coinstotrade)) {
    		noerror=false;
    		stringBuilder.append(coinstotrade + " is not a valid number(coinstotrade).\n");
    	}
    	
    	if(!NumberUtils.isCreatable(volumeperorder)) {
    		noerror=false;
    		stringBuilder.append(volumeperorder + " is not a valid number(volumeperorder).\n");
    	}
    	
    	if(!NumberUtils.isCreatable(loop)) {
    		noerror=false;
    		stringBuilder.append(loop + " is not a valid number(loop).\n");
    	}
    	
    	if (noerror==true) {
    		String confirm = ("Base: " + base + "\nAlt: " + alt + "\nCoinstoTrade: " + coinstotrade + "\nVolumeperOrder: " + volumeperorder + "\nAtBid: " + atbid + "\nBuySell: " + buy + "\nLoop: " + loop + "\nExchange: " + exchange);
    		String run = FxDialogs.showConfirm("Run Order?",confirm, "Run", "Cancel");
    		System.out.println(run);
			if (run.equals("Run")) {
		    	averageTrading.put("base", base);
		    	averageTrading.put("alt", alt);
		    	averageTrading.put("Exchanges",exchange);
		    	averageTrading.put("request", "averageTrading");
		    	averageTrading.put("coinstotrade", coinstotrade);
		    	averageTrading.put("volumeperorder", volumeperorder);
		    	averageTrading.put("licenceKey", SocketCommunication.licencekey);
		    	averageTrading.put("millisstart", System.currentTimeMillis());
		    	averageTrading.put("atbid", atbid);
		    	averageTrading.put("buy", buy);
		    	averageTrading.put("loop", loop);
		    	Random rand = new Random(); 
		    	int value = rand.nextInt(1000000000); 
		    	averageTrading.put("orderid", value);
		    	averageTrading.put("endtime","N/A");
		    	averageTrading.put("running","True");
		    	System.out.println("--------------");
		    	System.out.println(averageTrading);
		    	AverageTrading averagetradingclass = new AverageTrading(averageTrading);
		    	AverageTradingMap.put(averageTrading, averagetradingclass);
		    	Thread t = new Thread(averagetradingclass);
		    	t.start();
		    	Main.logger.log(Level.INFO, "Started running average trading");
			} else {
				Main.logger.log(Level.INFO, "Average trading canceled from dialog");
				System.out.println("Not running order");
			}
    	} else {
		    String finalString = stringBuilder.toString();
	    	FxDialogs.showError(null, finalString);
    	}
	}
	
	public static void cancelAverageOrder(String orderid) {
		for (Map.Entry<JSONObject, AverageTrading> entry : AverageTradingMap.entrySet()) {
		    JSONObject key = entry.getKey();
			try {
				if (key.getInt("orderid") == Integer.parseInt(orderid)) {
					AverageTrading value = entry.getValue();
					value.stopOrder();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

	}
	}
}
