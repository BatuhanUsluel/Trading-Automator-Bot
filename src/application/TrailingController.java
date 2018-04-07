package application;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXRadioButton;
import com.jfoenix.controls.JFXTextField;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
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

	@FXML
    public void initialize(){
        List<String> list = new ArrayList<String>(Exchanges.list);
        TStopExchange.getItems().addAll(list);
    }
	
	@FXML
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
}
