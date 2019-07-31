package application;
	
import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Arrays;
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
	        
	        Logger logger2 = Logger.getLogger(Main.class.getName());
	        SimpleDateFormat format = new SimpleDateFormat("M-d_HH.mm.ss");
	        FileHandler fh = new FileHandler("C:\\Users\\batuh\\Desktop\\LogFile_" + format.format(Calendar.getInstance().getTime()) + ".log");
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
