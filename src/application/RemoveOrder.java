package application;

import org.json.JSONException;

public class RemoveOrder {
	public static void removeOrder(DashboardController.Person person) throws JSONException {
		String ordertype = person.getOrderType();
		System.out.println("REMOVING");
		if (ordertype.equals("averageTrading")) {
			AverageTrading.removeOrder(person.getOrderID());
		} else if(ordertype.equals("trailingStop")) {
			TrailingController.removeOrder(person.getOrderID());
		} else if(ordertype.equals("pendingOrder")) {
			
		} else if(ordertype.equals("marketMaking")) {
			
		} else if(ordertype.equals("pendingOrder")) {
			
		} else if(ordertype.equals("arbitrage")) {
			
		} else if(ordertype.equals("technical")) {
			
		}
	}
}
