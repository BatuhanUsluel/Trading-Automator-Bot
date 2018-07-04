package application;
	
import java.time.Instant;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;


public class Main extends Application {
	public static Stage primaryStage;
	
	public static void main(java.lang.String[] args) {
		launch(args);
	}
	
	@Override
	public void start(Stage primaryStage) {
		try {
			long epoch = System.currentTimeMillis();

			System.out.println("Epoch : " + (epoch / 1000));
			System.out.println(Instant.now().toEpochMilli());
			
	        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Sample.fxml"));
	        AnchorPane rootLayout = loader.load();
	        Scene scene = new Scene(rootLayout);
	        primaryStage.setScene(scene);
	        primaryStage.setOpacity(0.99);
	        primaryStage.show();
	        primaryStage.setResizable(false);
	        Main.primaryStage = primaryStage;
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
