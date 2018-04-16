package application;

import org.json.JSONException;

import controllers.AveragetradingController;
import controllers.DashboardController;
import controllers.PendingController;
import controllers.TrailingController;

public class RemoveOrder {
	public static void removeOrder(DashboardController.Person person) throws JSONException {
		String ordertype = person.getOrderType();
		System.out.println("REMOVING");
		if (ordertype.equals("averageTrading")) {
			AveragetradingController.cancelAverageOrder(person.getOrderID());
		} else if(ordertype.equals("trailingStop")) {
			TrailingController.removeOrder(person.getOrderID());
		} else if(ordertype.equals("pendingOrder")) {
			PendingController.cancelPendingOrder(person.getOrderID());
		} else if(ordertype.equals("marketMaking")) {
			
		} else if(ordertype.equals("arbitrage")) {
			
		} else if(ordertype.equals("technical")) {
			
		}
	}
}
