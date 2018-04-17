package controllers;

import java.io.IOException;

import org.json.JSONException;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;

import application.Exchanges;
import application.Main;
import application.MarketMaking;
import application.SocketCommunication;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;

public class Controller {

    //Main
		 @FXML private PasswordField Pass;
		 @FXML private TextField Email;
		 @FXML private JFXButton LoggedIn;
		 @FXML private JFXCheckBox RememberMe;
		 @FXML private JFXButton SignUp;
		 @FXML private BorderPane mainView;

	 //Navigation
		 @FXML private JFXButton arbitrage;
		 @FXML private JFXButton mmake;
		 @FXML private JFXButton backtest;
		 @FXML private JFXButton lives;
		 @FXML private JFXButton home;
		 @FXML private JFXButton avtrad;
		 @FXML private JFXButton qbuy;
		 @FXML private JFXButton porder;
		 @FXML private JFXButton tstop;
		 @FXML private JFXButton fobook;
		 @FXML private JFXButton settings;

	@FXML
    private void handleChangeView(ActionEvent event) {
    	System.out.println("Changing");
        try {
            String menuItemID = ((JFXButton) event.getSource()).getId();
            System.out.println(menuItemID);
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/" + menuItemID + ".fxml"));
            
            System.out.println(loader);
            mainView.setCenter(loader.load()); 
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
	
	@FXML
    @SuppressWarnings("unchecked")
    private void DashBoard(ActionEvent event) throws IOException
    {
	   	MarketMaking.testtheMarket();
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/home.fxml"));
		mainView.setCenter(loader.load());
		System.out.println("TABLE1!");
    }
	
    @FXML
    private void LogIn(ActionEvent event) throws IOException, JSONException, InterruptedException
    {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/LoggedIn.fxml"));
        AnchorPane rootLayout = loader.load(); 
        Scene scene = new Scene(rootLayout);
        Main.primaryStage.setScene(scene);
        Main.primaryStage.show();
        Exchanges ex = new Exchanges();
        ex.createExchanges();
        SocketCommunication.setup();
    }

}


