package controllers;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.logging.Level;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Hex;
import org.json.JSONException;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;

import application.Exchanges;
import application.FxDialogs;
import application.Main;
import application.SocketCommunication;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.WindowEvent;

public class Controller {
	public static Scene scene;
	private static String pass;
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
		 @FXML private JFXButton Portifolio;

	
	 @FXML
	    public void initialize(){
		 System.out.println("setting default");
	 }
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
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/home.fxml"));
		mainView.setCenter(loader.load());
    }
	
    public static boolean fileProcessor(int cipherMode,String key,File inputFile,File outputFile, int x){
   	 try {
   		 if (key==null) {
   			 return false;
   		 }
   		   MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
		   String password = key;
		   messageDigest.update(password.getBytes());
		   byte[] hashedkey = messageDigest.digest();
		   hashedkey = Arrays.copyOf(hashedkey, 16);
		   Key secretKey = new SecretKeySpec(hashedkey, "AES");
   	       Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
   	       cipher.init(cipherMode, secretKey);

   	       FileInputStream inputStream = new FileInputStream(inputFile);
   	       byte[] inputBytes = new byte[(int) inputFile.length()];
   	       inputStream.read(inputBytes);

   	       byte[] outputBytes = cipher.doFinal(inputBytes);

   	       FileOutputStream outputStream = new FileOutputStream(outputFile);
   	       outputStream.write(outputBytes);
   	      
   	       inputStream.close();
   	       outputStream.close();
   	       if (x==0) {
   	    	   SocketCommunication.licencekey =  new String(outputBytes, StandardCharsets.UTF_8);
   	       }
   	       System.out.println("returning true");
   	       return true;
   	    } catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException | BadPaddingException | IllegalBlockSizeException | IOException e) {
   	    	e.printStackTrace();
   	    	System.out.println("returning false");
   	    	return false;
               }
        }
}


