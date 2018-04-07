package application;

import java.util.ArrayList;
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
	
	@FXML
    public void averageTrading(ActionEvent event) throws JSONException {
     	 System.out.println("running averageTrading");
    	JSONObject averageTrading = new JSONObject();
    	averageTrading.put("Basecoin", BPAvU.getText());
    	averageTrading.put("Altcoin", APAvU.getText());
    	averageTrading.put("Exchanges", ExAv.getValue().toString());
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

}
