package application;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import org.json.JSONException;
import org.json.JSONObject;
import org.knowm.xchange.Exchange;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.Order.OrderType;
import org.knowm.xchange.dto.trade.LimitOrder;
import org.knowm.xchange.dto.trade.StopOrder;
import org.knowm.xchange.exceptions.ExchangeException;
import org.knowm.xchange.exceptions.NotAvailableFromExchangeException;
import org.knowm.xchange.exceptions.NotYetImplementedForExchangeException;

public class TrailingStop {
	public static ArrayList<JSONObject> OrdersTrailing = new ArrayList<JSONObject>();
	public static ArrayList<JSONObject> OrdersTrailing1 = new ArrayList<JSONObject>();
	
	public static void runOrder(JSONObject message) {
		class OneShotTask implements Runnable {
			JSONObject message;
			OneShotTask(JSONObject message2) {message= message2; }
	        public void run() {
	            boolean run=true;
	            try {
	            	OrdersTrailing1.add(message);
					//int loop = Integer.parseInt(message.getString("loop"));
					
		            while (run==true) {
		            	if (OrdersTrailing1.contains(message)) {
		            		System.out.println("Orders contains");
		                	OrdersTrailing.add(message);
		                	System.out.println(message);
		                	SocketCommunication.out.print(message.toString());
		                	SocketCommunication.out.flush();
		            	} else {
		            		System.out.println("Removing Order");
		            		run=false;
		            	}
		            	TimeUnit.SECONDS.sleep(60);
		            }
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
	        }
		}
		
	    Thread t = new Thread(new OneShotTask(message));
	    t.start();
	}
	
	public static void recievedTrailingStop(JSONObject message) throws JSONException, NotAvailableFromExchangeException, NotYetImplementedForExchangeException, ExchangeException, IOException {
		System.out.println("Recieved TrailingStop!");
		System.out.println(OrdersTrailing.toString());
		for (int i = 0; i < OrdersTrailing.size(); i++) {
			JSONObject listitem = OrdersTrailing.get(i);
			System.out.println("Looping");
			
			if ((listitem.getString("base").equals(message.getString("base")))
			&& (listitem.getString("alt").equals(message.getString("alt")))
			&& (listitem.getString("volume").equals(message.getString("volume")))
			&& (listitem.getString("trail").equals(message.getString("trail")))
			&& (listitem.getString("buysell").equals(message.getString("buysell")))
			&& (listitem.getString("exchange").equals(message.getString("exchange")))
			&& (listitem.getString("licenceKey").equals(message.getString("licenceKey")))
			&& listitem.getLong("millis") == (message.getLong("millis"))) {
				
				OrdersTrailing.remove(listitem);
				String basecoin = listitem.getString("base");
				String altcoin = listitem.getString("alt");
				String exchangeString = listitem.getString("exchange");
				Exchange exchange = Exchanges.exchangemap.get(exchangeString);
				String buystring = listitem.getString("buysell");
				boolean buy = true;
				if (buystring.equals("Buy")) {
					buy=true;
				} else if (buystring.equals("Sell")) {
					buy=false;
				} else {
					System.out.println("error!");
				}
				System.out.println("Buystring:" + buystring);
				double volume = Double.parseDouble(listitem.getString("volume"));
				double trail = Double.parseDouble(listitem.getString("trail"));
				double price = Double.parseDouble(message.getString("price"));
				CurrencyPair pair = new CurrencyPair(altcoin,basecoin);
				
				//Buy order tracks above the price, sell order tracks below
				if (buy==true) {
					LimitOrder BuyingOrder = new LimitOrder((OrderType.BID), new BigDecimal(volume).setScale(8, RoundingMode.HALF_DOWN), pair, null, null, new BigDecimal(price+trail).setScale(8, RoundingMode.HALF_DOWN));
					System.out.println(BuyingOrder);
					//String limitOrderReturnValueBUY = exchange.getTradeService().placeLimitOrder(BuyingOrder);
				} else {
					StopOrder SellingOrder = new StopOrder((OrderType.ASK), new BigDecimal(volume).setScale(8, RoundingMode.HALF_DOWN), pair, null, null, new BigDecimal(price-trail).setScale(8, RoundingMode.HALF_DOWN));
					System.out.println(SellingOrder);
					//String limitOrderReturnValueSELL = exchange.getTradeService().placeLimitOrder(SellingOrder);
				}
			}
		}		
	}
}
