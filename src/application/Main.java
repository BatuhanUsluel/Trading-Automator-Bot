package application;
	
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;


public class Main extends Application {
	public static Stage primaryStage;
	
	public static void main(String[] args) {
		launch(args);
	}
	
	@Override
	public void start(Stage primaryStage) {
		try {
			/*
			ExchangeSpecification exSpec = new ExchangeSpecification("org.knowm.xchange.binance.BinanceExchange");
			exSpec.setUserName("Batu");
			exSpec.setApiKey("da9f8904679a4533837ed516ae5f9862");
			exSpec.setSecretKey("c43393ea57634117bbdcf093db4c5309");
		    Exchange bittrex2 = ExchangeFactory.INSTANCE.createExchange(exSpec); 
		    System.out.println(bittrex2.getMarketDataService().getTicker(new CurrencyPair("ETH", "BTC")));
		    */
	        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Sample.fxml"));
	        AnchorPane rootLayout = loader.load(); 
	        Scene scene = new Scene(rootLayout);
	        primaryStage.setScene(scene);
	        primaryStage.setOpacity(0.99);
	        primaryStage.show();
	        primaryStage.setResizable(false);
	        this.primaryStage = primaryStage;
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	void usetScene(Scene scene) {
		primaryStage.setScene(scene);
	}
}
