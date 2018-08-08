package application;

import java.io.IOException;

import org.json.JSONException;

import controllers.ArbitrageController;
import controllers.AveragetradingController;
import controllers.BacktestController;
import controllers.DashboardController;
import controllers.LiveController;
import controllers.MarketController;
import controllers.PendingController;
import controllers.TrailingController;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ShowInfo {
	public static void showInfo(DashboardController.Person person) throws JSONException {
		String ordertype = person.getOrderType();
		if (ordertype.equals("averageTrading")) {
			showSpecificInfo(person.getOrderData());
		} else if(ordertype.equals("trailingStop")) {
			showSpecificInfo(person.getOrderData());
		} else if(ordertype.equals("pendingOrder")) {
			showSpecificInfo(person.getOrderData());
		} else if(ordertype.equals("marketMaking")) {
			showSpecificInfo(person.getOrderData());
		} else if(ordertype.equals("arbitrage")) {
			showSpecificInfo(person.getOrderData());
		} else if(ordertype.equals("livetrading")) {
			LiveController.showMenu(person.getOrderID());
		}
	}

	private static void showSpecificInfo(String orderData) {
		Platform.runLater(new Runnable() {
            public void run() {
            	TextArea textArea = new TextArea();
            	textArea.setText(orderData);
            	HBox container  = new HBox(textArea);
            	container.setAlignment(Pos.CENTER);
                container.setPadding(new Insets(10));

                // Set Hgrow for TextField
                HBox.setHgrow(textArea, Priority.ALWAYS);

                BorderPane pane = new BorderPane();
                pane.setCenter(container);
                Scene scene = new Scene(pane, 700, 500);
                Stage stage = new Stage();
		        stage.setTitle("Info");
		        stage.setScene(scene);
		        stage.show();
        }});

		
	}
}
