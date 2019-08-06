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
		 if (LoggedIn!=null) {
			 LoggedIn.setDefaultButton(true);
		 }
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
	
    @FXML
    private void LogIn(ActionEvent event) throws IOException, JSONException, InterruptedException, NoSuchAlgorithmException
    {
        String password = Pass.getText();
        String salt = "1234";
        int iterations = 10000;
        int keyLength = 512;
        char[] passwordChars = password.toCharArray();
        byte[] saltBytes = salt.getBytes();

        byte[] hashedBytes = hashPassword(passwordChars, saltBytes, iterations, keyLength);
        String encryptedString = Hex.encodeHexString(hashedBytes);
		File licencetxtfile = new File("licencekey.txt");
		File exchangetxtfile = new File("exchanges.txt");
		File licencekeyencrypted = new File("licencekey.encrypted");
		File exchangesencrypted = new File("exchanges.encrypted");
		pass=password;
		Main.primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
            	System.out.println("Closing2");
            	File licencetxtfile = new File("licencekey.txt");
	    		File exchangetxtfile = new File("exchanges.txt");
	    		File licencekeyencrypted = new File("licencekey.encrypted");
	    		File exchangesencrypted = new File("exchanges.encrypted");
	    		System.out.println("Pass: " + Controller.pass);
	    		if (Controller.fileProcessor(Cipher.ENCRYPT_MODE, Controller.pass, licencetxtfile, licencekeyencrypted, 1)) {
	    			Main.logger.log(Level.INFO, "Successfully encrypted licencekey into licencekey.encrypted");
	    			if (licencetxtfile.delete()) {
	    				Main.logger.log(Level.INFO, "Successfully deleted licencekey.txt");
	    			} else {
	    				Main.logger.log(Level.SEVERE, "Unable to delete licencekey.txt. Could be caused by the file being used");
	    			}
	    		} else {
	    			Main.logger.log(Level.SEVERE, "Unable to encrypt licencekey.txt. Skipping deletion of licencekey.txt");
	    		}
	        	if (Controller.fileProcessor(Cipher.ENCRYPT_MODE, Controller.pass, exchangetxtfile, exchangesencrypted, 1)) {
	        		Main.logger.log(Level.INFO, "Successfully encrypted exchanges.txt into exchanges.encrypted");
	    			if (exchangetxtfile.delete()) {
	    				Main.logger.log(Level.INFO, "Successfully deleted exchanges.txt");
	    			} else {
	    				Main.logger.log(Level.SEVERE, "Unable to delete exchanges.txt. Could be caused by the file being used");
	    			}
	        	} else {
	        		Main.logger.log(Level.SEVERE, "Unable to encrypt exchanges.txt. Skipping deletion of exchanges.txt");
	        	}
	        	Main.logger.log(Level.INFO, "---------------------------------------------------------------------");
	        	 Platform.exit();
	             System.exit(0);
            }
        });
        if(new File("password.hashed").isFile()) {
        	Main.logger.log(Level.INFO, "password.hashed file found");
            FileReader fileReader = new FileReader("password.hashed");
            BufferedReader bufferedReader =  new BufferedReader(fileReader);
            String readpass = bufferedReader.readLine();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
            	readpass = readpass + line;
            }
            
        	if (encryptedString.equals(readpass)) { //If file exists & the password is correct
        		
        		if (licencetxtfile.isFile()) {
        			if (licencetxtfile.length()==0) {
        				Main.logger.log(Level.WARNING, "licencekey.txt file is empty. Please paste your licencekey into the file. If you wish to use stored licencekey(licencekey.encrypted), delete the licencekey.txt file");
        				FxDialogs.showError("Licence Key File Error", "licencekey.txt file is empty. Please paste your licencekey into the file.");
        			} else {
        				Main.logger.log(Level.INFO, "Inputted licencekey from licencekey.txt");
	        			FileReader fileReaderlicence = new FileReader("licencekey.txt");
	                    BufferedReader bufferedReaderlicence =  new BufferedReader(fileReaderlicence);
	        			SocketCommunication.licencekey = bufferedReaderlicence.readLine();
	        			bufferedReaderlicence.close();
        			}
        		} else if (licencekeyencrypted.isFile()) {
        			fileProcessor(Cipher.DECRYPT_MODE,password, licencekeyencrypted, licencetxtfile, 0);
        			Main.logger.log(Level.INFO, "Inputted licencekey from licencekey.encrypted");
        		} else {
        			Main.logger.log(Level.WARNING, "Could not find file licencekey.txt or licencekey.encrypted. Empty licencekey.txt has been automatically created. Please paste your licence key in the file");
        			FxDialogs.showError("Licence Key File Error", "Could not find file licencekey.txt or licencekey.encrypted. Empty licencekey.txt has been automatically created. Please paste your licence key in the file");
        			licencetxtfile.createNewFile();
        		}
        		
        		if (exchangetxtfile.isFile()) {
        			if (exchangetxtfile.length()==0) {
        				Main.logger.log(Level.WARNING, "Exchanges.txt file is empty. Continuing with 0 exchanges");
        				FxDialogs.showWarning("Exchange config file error", "Exchanges.txt is empty. Continuing with 0 exchanges");
        			} else {
        				Main.logger.log(Level.INFO, "Inputting exchanges from exchanges.txt");
        			}
	        	} else if (exchangesencrypted.isFile()) {
	        		fileProcessor(Cipher.DECRYPT_MODE,password,exchangesencrypted, exchangetxtfile, 1);
	        		Main.logger.log(Level.INFO, "Inputting exchanges from exchanges.encrypted");
        		} else {
        			Main.logger.log(Level.WARNING, "Could not find file exchanges.txt or exchanges.encrypted. Empty exchanges.txt has been automatically created. Continuing with 0 exchanges");
        			FxDialogs.showWarning("Exchange config file error", "Could not find file exchanges.txt or exchanges.encrypted. Empty exchanges.txt has been automatically created. Continuing with 0 exchanges");
        			exchangetxtfile.createNewFile();
        		}
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/LoggedIn.fxml"));
                AnchorPane rootLayout = loader.load(); 
                Scene scene = new Scene(rootLayout);
                this.scene=scene;
                Main.primaryStage.setScene(scene);
                Main.primaryStage.show();
                IndicatorMaps.addValues();
                Exchanges ex = new Exchanges();
                ex.createExchanges();
                SocketCommunication.setup();
        	} else { //If file exists but the password is incorrect
        		System.out.println("Encrypted String: " +encryptedString);
        		System.out.println("File String: " + readpass);
        		FxDialogs.showError("Invalid password", "Hashed password does not match password.hashed.");
        		Main.logger.log(Level.WARNING, "Invalid password for login. Hashed password does not match password.hashed. If you wish to use a new password, delete the password.hashed file");
        	}
        	bufferedReader.close();
        } else { //If file does not exist
        	FxDialogs.showWarning("New password", "password.hashed file does not exist. Creating new password.hashed with the inputed password");
        	Main.logger.log(Level.WARNING, "password.hashed file does not exist. Creating new password.hashed with the inputed password");
            FileWriter fileWriter = new FileWriter("password.hashed");
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write(encryptedString);
            bufferedWriter.close();
            if (exchangetxtfile.isFile()) {
            	if (exchangetxtfile.length()==0) {
    				Main.logger.log(Level.WARNING, "Exchanges.txt file is empty. Continuing with 0 exchanges");
    				FxDialogs.showWarning("Exchange config file error", "Exchanges.txt is empty. Continuing with 0 exchanges");
    			} else {
    				Main.logger.log(Level.INFO, "Inputting exchanges from exchanges.txt");
    			}
            } else {
            	Main.logger.log(Level.WARNING, "Could not find file exchanges.txt. Empty exchanges.txt has been automatically created. Continuing with 0 exchanges");
    			FxDialogs.showWarning("Exchange config file error", "Could not find file exchanges.txt. Empty exchanges.txt has been automatically created. Continuing with 0 exchanges");
    			exchangetxtfile.createNewFile();
            }
	        if (licencetxtfile.isFile()) {
	        	if (licencetxtfile.length()==0) {
    				Main.logger.log(Level.WARNING, "licencekey.txt file is empty. Please paste your licencekey into the file.");
    				FxDialogs.showError("Licence Key File Error", "licencekey.txt file is empty. Please paste your licencekey into the file.");
    			} else {
    				Main.logger.log(Level.INFO, "Inputted licencekey from licencekey.txt");
        			FileReader fileReaderlicence = new FileReader("licencekey.txt");
                    BufferedReader bufferedReaderlicence =  new BufferedReader(fileReaderlicence);
        			SocketCommunication.licencekey = bufferedReaderlicence.readLine();
        			bufferedReaderlicence.close();
    			}
	        } else {
	        	Main.logger.log(Level.WARNING, "Could not find file licencekey.txt. Empty licencekey.txt has been automatically created. Please paste your licence key in the file");
    			FxDialogs.showError("Licence Key File Error", "Could not find file licencekey.txt. Empty licencekey.txt has been automatically created. Please paste your licence key in the file");
    			licencetxtfile.createNewFile();
	        }
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/LoggedIn.fxml"));
            AnchorPane rootLayout = loader.load(); 
            Scene scene = new Scene(rootLayout);
            this.scene=scene;
            Main.primaryStage.setScene(scene);
            Main.primaryStage.show();
            IndicatorMaps.addValues();
            Exchanges ex = new Exchanges();
            ex.createExchanges();
            SocketCommunication.setup();
        }
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
    
    public static byte[] hashPassword( final char[] password, final byte[] salt, final int iterations, final int keyLength ) {

        try {
            SecretKeyFactory skf = SecretKeyFactory.getInstance( "PBKDF2WithHmacSHA512" );
            PBEKeySpec spec = new PBEKeySpec( password, salt, iterations, keyLength );
            SecretKey key = skf.generateSecret( spec );
            byte[] res = key.getEncoded( );
            return res;
        } catch ( NoSuchAlgorithmException | InvalidKeySpecException e ) {
            throw new RuntimeException( e );
        }
    }
}


