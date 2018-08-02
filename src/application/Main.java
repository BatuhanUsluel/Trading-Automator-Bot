package application;
	
import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.*;

import javax.crypto.Cipher;

import controllers.Controller;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;


public class Main extends Application {
	public static Stage primaryStage;
	public static Logger logger;
	public static void main(java.lang.String[] args) {
		launch(args);	
	}
	
	@Override
	public void start(Stage primaryStage) {
		try {		
	        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Sample.fxml"));
	        AnchorPane rootLayout = loader.load();
	        Scene scene = new Scene(rootLayout);
	        primaryStage.setScene(scene);
	        primaryStage.setOpacity(0.99);
	        primaryStage.show();
	        primaryStage.setResizable(false);
	        Main.primaryStage = primaryStage;
	        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
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
	        Logger logger2 = Logger.getLogger(Main.class.getName());
	        SimpleDateFormat format = new SimpleDateFormat("M-d_HH.mm.ss");
	        FileHandler fh = new FileHandler("C:\\Users\\Batuhan Usluel\\Desktop\\LogFile_" + format.format(Calendar.getInstance().getTime()) + ".log");
	        logger2.addHandler(fh);
	        fh.setFormatter(new SimpleFormatter() {
	            private static final String format = "[%1$tF %1$tT] [%2$-7s] %3$s %n";

	            @Override
	            public synchronized String format(LogRecord lr) {
	                return String.format(format,
	                        new Date(lr.getMillis()),
	                        lr.getLevel().getLocalizedName(),
	                        lr.getMessage()
	                );
	            }
	        });
	        logger = logger2;
		} catch(java.lang.Exception e) {
			e.printStackTrace();
		}
	}
	void usetScene(Scene scene) {
		primaryStage.setScene(scene);
	}
	void getNTPTime() {
		
	}
}
