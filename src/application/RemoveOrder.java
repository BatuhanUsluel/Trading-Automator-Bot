package application;

import org.json.JSONException;

import controllers.ArbitrageController;
import controllers.AveragetradingController;
import controllers.DashboardController;
import controllers.LiveController;
import controllers.MarketController;
import controllers.PendingController;
import controllers.TrailingController;

public class RemoveOrder {
	public static void removeOrder(DashboardController.Person person) throws JSONException {
		String ordertype = person.getOrderType();
		System.out.println("REMOVING");
		if (ordertype.equals("averageTrading")) {
            person.setRunning("False");
			AveragetradingController.cancelAverageOrder(person.getOrderID());
		} else if(ordertype.equals("trailingStop")) {
            person.setRunning("False");
			TrailingController.removeOrder(person.getOrderID());
		} else if(ordertype.equals("pendingOrder")) {
            person.setRunning("False");
			PendingController.cancelPendingOrder(person.getOrderID());
		} else if(ordertype.equals("marketMaking")) {
            person.setRunning("False");
			MarketController.cancelMarketOrder(person.getOrderID());
		} else if(ordertype.equals("arbitrage")) {
            person.setRunning("False");
			ArbitrageController.cancelArbitrageOrder(person.getOrderID());
		} else if(ordertype.equals("livetrading")) {
			person.setRunning("False");
			LiveController.cancelLiveOrder(person.getOrderID());
		}
	}
}
