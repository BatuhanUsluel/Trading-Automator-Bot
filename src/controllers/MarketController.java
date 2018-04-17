package controllers;
import java.lang.Thread;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang3.math.NumberUtils;
import org.json.JSONException;
import org.json.JSONObject;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;

import application.Exchanges;
import application.FxDialogs;
import application.MarketMaking;
import application.SocketCommunication;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
public class MarketController {
	//Market Making
    @FXML private TextField BaseMM;
    @FXML private TextField AltMM;
    @FXML private JFXButton RunMM;
    @FXML private JFXComboBox<String> ExchangeMM;
    @FXML private TextField SpreadMM;
    @FXML private TextField MaxBalMM;
    @FXML private TextField MinBalMM;
    
	public static HashMap<JSONObject, MarketMaking> marketMakingMap = new HashMap<JSONObject, MarketMaking>();

	@FXML
    public void initialize(){
        List<String> list = new ArrayList<String>(Exchanges.list);
        ExchangeMM.getItems().addAll(list);
    }
	
	public void marketMaking(ActionEvent event)  throws JSONException {
    	JSONObject marketMaking = new JSONObject();
    	String base = BaseMM.getText();
    	String Alt = AltMM.getText();
    	String Spread = SpreadMM.getText();
    	String MaxBal = MaxBalMM.getText();
    	String MinBal = MinBalMM.getText();
    	boolean noerror=true;
    	boolean noexchange = false;
    	String exchange = "";
    	StringBuilder stringBuilder = new StringBuilder();
    	
    	try {
    		exchange = ExchangeMM.getValue().toString();
    	} catch (NullPointerException e) {
    		noerror=false;
    		noexchange=true;
    		stringBuilder.append("Please enter a exchange\n");
    	}
    	
    	if (!Exchanges.list.contains(exchange) && noexchange!=true) {
    		noerror=false;
    		stringBuilder.append(exchange + " is not a valid exchange.\n");
    	}
    	
    	if(!NumberUtils.isCreatable(Spread)) {
    		noerror=false;
    		stringBuilder.append(Spread + " is not a valid number(Spread).\n");
    	}
    	
    	if(!NumberUtils.isCreatable(MaxBal)) {
    		noerror=false;
    		stringBuilder.append(MaxBal + " is not a valid number(MaxBal).\n");
    	}
    	if(!NumberUtils.isCreatable(MinBal)) {
    		noerror=false;
    		stringBuilder.append(MinBal + " is not a valid number(MinBal).\n");
    	}
    	if (noerror==true) {
    		String confirm = ("Base: " + base + "\nAlt: " + Alt + "\nSpread: " + Spread + "\nMaxBalance: " + MaxBal + "\nMinBalance: " + MinBal + "\nExchange: " + exchange);
    		String run = FxDialogs.showConfirm("Run Order?", confirm, "Run", "Cancel");
    		System.out.println(run);
			if (run.equals("Run")) {
				marketMaking.put("base", base);
		    	marketMaking.put("alt", Alt);
		    	marketMaking.put("spread", Spread);
		    	marketMaking.put("MaxBal", MaxBal);
		    	marketMaking.put("MinBal", MinBal);
		    	marketMaking.put("Exchanges", exchange);
		    	marketMaking.put("licenceKey", SocketCommunication.licencekey);
		    	marketMaking.put("millisstart", System.currentTimeMillis());
		    	marketMaking.put("request","marketMaking");
		    	marketMaking.put("endtime","N/A");
		    	MarketMaking market = new MarketMaking(marketMaking);
		    	marketMakingMap.put(marketMaking, market);
		    	Thread t = new Thread(market);
		    	t.start();
			} else {
				System.out.println("Not running order");
			}
	    } else {
		    String finalString = stringBuilder.toString();
	    	FxDialogs.showError(null, finalString);
	    }
	}
}
