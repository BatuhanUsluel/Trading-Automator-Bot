package application;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXRadioButton;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;

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
   
    @FXML
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
	@FXML
    public void initialize(){
        List<String> list = new ArrayList<String>(Exchanges.list);
        ExPOU.getItems().addAll(list);
    }
	

}
