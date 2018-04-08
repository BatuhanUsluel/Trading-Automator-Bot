package application;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang3.math.NumberUtils;
import org.controlsfx.control.NotificationPane;
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
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.BorderPaneBuilder;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.stage.StageStyle;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.StageStyle;

import java.awt.*;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
    	StringBuilder stringBuilder = new StringBuilder();
    	JSONObject pendingOrder = new JSONObject();
    	String base = POBPU.getText();
    	String alt = APPOU.getText();
    	String price = PPOU.getText();
    	String volume = BVPOU.getText();
    	String percent = POVPOU.getText();
    	String buysell = ((RadioButton) toggleGroupPO.getSelectedToggle()).getText();
    	
    	String exchange = ExPOU.getValue().toString();
    	if (!Exchanges.list.contains(exchange)) {
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
    	PendingOrder pend = new PendingOrder(pendingOrder);
    	PendingOrderMap.put(pendingOrder, pend);
    	Thread t = new Thread(pend);
    	t.start();
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
	

}
