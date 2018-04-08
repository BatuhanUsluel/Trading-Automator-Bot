package application;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;

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
    	String exchange = ExchangeMM.getValue().toString();
    	//String exchange = "bittrex";

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
}
