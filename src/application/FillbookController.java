package application;

import java.util.ArrayList;
import java.util.List;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXRadioButton;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;

public class FillbookController {
    
//Fill Order Book
    @FXML private TextField FOBase;
    @FXML private TextField FOAlt;
    @FXML private TextField FOStartP;
    @FXML  private TextField FOEndP;
    @FXML private TextField FOBalanceUsed;
    @FXML private TextField FONumberOrders;
    @FXML private JFXRadioButton SellFO;
    @FXML private ToggleGroup FoBuySell;
    @FXML private JFXRadioButton BuyFO;
    @FXML private JFXButton RunFOBook;
    @FXML  private JFXComboBox<String> FOEx;
    
	@FXML
    public void initialize(){
        List<String> list = new ArrayList<String>(Exchanges.list);
        FOEx.getItems().addAll(list);
    }
	
	@FXML
    public void fillOrderBook(ActionEvent event) {
    	String base = FOBase.getText();
    	String alt = FOAlt.getText();
    	String startprice = FOStartP.getText();
    	String endprice = FOEndP.getText();
    	String balanceused = FOBalanceUsed.getText();
    	String nooforders = FONumberOrders.getText();
    	String BuySell = ((RadioButton) FoBuySell.getSelectedToggle()).getText();
    	String exchange = FOEx.getValue().toString();
    	//Add check for valid inputs!!
    	try {
			FillOrderBook.fillOrderBook(base, alt, startprice,endprice, balanceused, nooforders, BuySell, exchange);
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
}
