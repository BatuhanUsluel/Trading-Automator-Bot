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

import application.Exchanges;
import application.FxDialogs;
import application.Main;
import application.SocketCommunication;
import application.TrailingStop;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
public class TrailingController {
	public static HashMap<JSONObject, TrailingStop> TrailingStopMap = new HashMap<JSONObject, TrailingStop>();
	//Trailing Stop
    @FXML private TextField TStopBase;
    @FXML private TextField TStopAlt;
    @FXML private TextField TStopVolume;
    @FXML private TextField TStopTrail;
    @FXML private JFXButton RunTStop;
    @FXML private JFXComboBox<String> TStopExchange;
    @FXML private JFXRadioButton TStopSell;
    @FXML private ToggleGroup TStopToggleBS;
    @FXML private JFXRadioButton TStopBuy;
    @FXML private JFXRadioButton AbovePrice;
    @FXML private ToggleGroup BelowAbove;
    @FXML private JFXRadioButton BelowPrice;

	@FXML
	public void initialize(){
	    List<String> list = new ArrayList<String>(Exchanges.list);
	    TStopExchange.getItems().addAll(list);
	}
	
	@FXML
	 public void trailingStop(ActionEvent event) throws JSONException {
			boolean noerror=true;
			boolean noexchange = false;
			String exchange = "";
			StringBuilder stringBuilder = new StringBuilder();
			JSONObject trailingStop = new JSONObject();
	    	String base = TStopBase.getText().toUpperCase();
	    	String alt = TStopAlt.getText().toUpperCase();
	    	String volume = TStopVolume.getText();
	    	String trail = TStopTrail.getText();
	    	String buysell = ((RadioButton) TStopToggleBS.getSelectedToggle()).getText();
	    	String abovebelow = ((RadioButton) BelowAbove.getSelectedToggle()).getText();
	    	try {
	    		exchange = TStopExchange.getValue().toString();
			} catch (NullPointerException e) {
				noerror=false;
				noexchange=true;
				stringBuilder.append("Please enter a exchange\n");
			}
	    	
	    	if (!Exchanges.list.contains(exchange)  && noexchange!=true) {
	    		noerror=false;
	    		stringBuilder.append(exchange + " is not a valid exchange.\n");
	    	}
	    	
	    	if(!NumberUtils.isCreatable(trail)) {
	    		noerror=false;
	    		stringBuilder.append(trail + " is not a valid number(trail).\n");
	    	}
	    	
	    	if(!NumberUtils.isCreatable(volume)) {
	    		noerror=false;
	    		stringBuilder.append(volume + " is not a valid number(volume).\n");
	    	}
	    	
			if (noerror==true) {
				String confirm = ("Base: " + base + "\nAlt: " + alt + "\nTrail: " + trail + "\nVolume" + volume + "\nBuySell: " + buysell + "\nExchange:" + exchange);
				String run = FxDialogs.showConfirm("Run Order?",confirm, "Run", "Cancel");
	    		System.out.println(run);
				if (run.equals("Run")) {
			    	trailingStop.put("base", base);
			    	trailingStop.put("alt", alt);
			    	trailingStop.put("request", "trailingStop");
			    	trailingStop.put("volume", volume);
			    	trailingStop.put("trail", trail);
			    	trailingStop.put("buysell", buysell);
			    	trailingStop.put("abovebelow", abovebelow);
			    	trailingStop.put("Exchanges",exchange);
			    	trailingStop.put("licenceKey", SocketCommunication.licencekey);
			    	trailingStop.put("millisstart", System.currentTimeMillis());
			    	Random rand = new Random(); 
			    	int value = rand.nextInt(1000000000); 
			    	trailingStop.put("orderid", value);
			    	trailingStop.put("endtime","N/A");
			    	trailingStop.put("running","True");
			    	TrailingStop trailingstopclass = new TrailingStop(base,alt,volume,exchange,trail, buysell, abovebelow, trailingStop);
			    	Thread t = new Thread(trailingstopclass);
			    	t.start();
				} else {
					Main.logger.log(Level.INFO, "Trailing stop has been canceled from dialog");
				}
			} else {
		    	String finalString = stringBuilder.toString();
	    		FxDialogs.showError(null, finalString);
		    }
	}
		public static void removeOrder(String orderid) {
			for (Map.Entry<JSONObject, TrailingStop> entry : TrailingStopMap.entrySet()) {
			    JSONObject key = entry.getKey();
				try {
					if (key.getInt("orderid") == Integer.parseInt(orderid)) {
						TrailingStop value = entry.getValue();
						value.stopOrder();
						Main.logger.log(Level.INFO, "Stopped trailing stop");
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
	
		}
	}
}
