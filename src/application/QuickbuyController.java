package application;

import java.util.ArrayList;
import java.util.List;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class QuickbuyController {
	//QuickBuy
    @FXML private TextField qBase;
    @FXML private TextField qAlt;
    @FXML private TextField qVolume;
    @FXML private TextField qBAA;
    @FXML private JFXButton RunQBuy;
    @FXML private JFXComboBox<String> qEx;		   
    
	@FXML
    public void initialize(){
        List<String> list = new ArrayList<String>(Exchanges.list);
        qEx.getItems().addAll(list);
    }
	
	@FXML
    public void quickPrice(ActionEvent event) {
      	 System.out.println("running quickPrice");
      	 QuickBuy.sendQuickPriceRequest(qBase.getText(),qAlt.getText(),qEx.getValue().toString(), Double.parseDouble(qVolume.getText()), Double.parseDouble(qBAA.getText()));
    	}    
}
