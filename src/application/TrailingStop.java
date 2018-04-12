package application;
import  java.lang.Double;
import java.lang.Thread;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import org.json.JSONException;
import org.json.JSONObject;
import org.knowm.xchange.Exchange;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.Order;
import org.knowm.xchange.dto.Order.OrderType;
import org.knowm.xchange.dto.trade.LimitOrder;
import org.knowm.xchange.exceptions.ExchangeException;
import org.knowm.xchange.exceptions.NotAvailableFromExchangeException;
import org.knowm.xchange.exceptions.NotYetImplementedForExchangeException;

import controllers.DashboardController;
import controllers.DashboardController.Person;

import  java.lang.Thread;
public class TrailingStop {
	ArrayList<JSONObject> OrdersTrailing = new ArrayList<JSONObject>();
	boolean run;
	boolean firstrun = true;
	String lastorder;
	double prevprice;
	LimitOrder LastOrder;
	Person person;
	public void runOrder(JSONObject message) {
			final JSONObject messagefinal = message;
			Thread thread = new Thread(new Runnable() {
				public void run() {
				run=true;
				DashboardController dash = new DashboardController();
	        	try {
					person = dash.newOrder(message);
					person.addOrderData("Starting Trailing Stop\nParameters:\nBase: " + message.getString("base") + "\nAlt: " + message.getString("alt") + "\nVolume: " +  message.getString("volume") + "\nExchange: " +  message.getString("Exchanges") + "\nTrail: " + message.getString("trail") + "\nOrder Type: " + message.getString("buysell") + "\n--------------------------------------\n\n");
				} catch (JSONException e) {
					e.printStackTrace();
				}

	            try {
					//int loop = Integer.parseInt(message.getString("loop"));
		            while (run==true) {
		            		person.addOrderData("\nRequesting price\n");
		            		System.out.println("Sending Order Request");
		            		messagefinal.put("millis", System.currentTimeMillis());
		                	OrdersTrailing.add(messagefinal);
		                	System.out.println(messagefinal);
		                	SocketCommunication.out.print(messagefinal.toString());
		                	SocketCommunication.out.flush();
		            	TimeUnit.SECONDS.sleep(20);
		            }
				} catch (InterruptedException | JSONException e) {
					e.printStackTrace();
				}
	        }
		});
	    thread.start();
	}
	
	public void recievedTrailingStop(JSONObject message) throws JSONException, NotAvailableFromExchangeException, NotYetImplementedForExchangeException, ExchangeException, IOException {
		
		for (int i = 0; i < OrdersTrailing.size(); i++) {
			JSONObject listitem = OrdersTrailing.get(i);
			if ((listitem.getString("base").equals(message.getString("base")))
			&& (listitem.getString("alt").equals(message.getString("alt")))
			&& (listitem.getString("request").equals(message.getString("request")))
			&& (listitem.getString("volume").equals(message.getString("volume")))
			&& (listitem.getString("trail").equals(message.getString("trail")))
			&& (listitem.getString("buysell").equals(message.getString("buysell")))
			&& (listitem.getString("Exchanges").equals(message.getString("Exchanges")))
			&& (listitem.getString("licenceKey").equals(message.getString("licenceKey")))
			&& listitem.getLong("millisstart") == (message.getLong("millisstart"))
			&& listitem.getLong("millis") == (message.getLong("millis"))) {
				OrdersTrailing.remove(listitem);
				person.addOrderData("Recieved price\n");
				String basecoin = listitem.getString("base");
				String altcoin = listitem.getString("alt");
				String exchangeString = listitem.getString("Exchanges");
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
				double volume = Double.parseDouble(listitem.getString("volume"));
				double trail = Double.parseDouble(listitem.getString("trail"));
				double price = Double.parseDouble(message.getString("price"));
				CurrencyPair pair = new CurrencyPair(altcoin,basecoin);
				
				if (firstrun==true) {
					
					if (buy==true) {
						person.addOrderData("First run, Placing Buy Order @ " + (price+trail) + " with volume: " + volume + "\n");
						LastOrder = new LimitOrder((OrderType.BID), new BigDecimal(volume).setScale(8, RoundingMode.HALF_DOWN), pair, null, null, new BigDecimal(price+trail).setScale(8, RoundingMode.HALF_DOWN));
						System.out.println(LastOrder);
						//lastorder = exchange.getTradeService().placeLimitOrder(LastOrder);
						prevprice = price;
					} else {
						person.addOrderData("First run, Sell, Current price: " + price + " trail: " + trail + " volume: " + volume + "\n");
						System.out.println("---------------------------TRADING FIRST RUN SELL----------------------");
						prevprice = price;
					}
					firstrun=false;
				} else {
					if (buy==true) {
						if (LastOrder.getStatus()== Order.OrderStatus.PARTIALLY_FILLED ) {
							person.addOrderData("Order has already been partially filled, not moving order\n");
						} else if(LastOrder.getStatus() == Order.OrderStatus.FILLED) {
							person.addOrderData("Order has been filled! Stopping TrailingStop\n");
							run=false;
						} else {
							if (price<prevprice) {
								person.addOrderData("Changing price of buy. New order @ " + price+trail + "\n");
								person.addOrderData("Price has decreased. Changing price of buy.\nPrevious Lowest Price: " + prevprice + "\nNew lowest price: " + price + "\nNew buy order price: " + (price-trail) + "\n");
								System.out.println("---------------------------CHANGING BUY----------------------");
								//exchange.getTradeService().cancelOrder(lastorder);
								LastOrder = new LimitOrder((OrderType.BID), new BigDecimal(volume).setScale(8, RoundingMode.HALF_DOWN), pair, null, null, new BigDecimal(price+trail).setScale(8, RoundingMode.HALF_DOWN));
								//lastorder = exchange.getTradeService().placeLimitOrder(BuyingOrder);
								System.out.println("Price:" + price);
								System.out.println("PrevPrice: " +prevprice);
								prevprice = price;
							} else {
								person.addOrderData("Price hasn't decreased.\nLowest price: " + prevprice + "\nCurrent price: " + price + "\n Buy order price: " + (prevprice+trail) + "\n");
								System.out.println("---------------------------NOT TRADING BUY----------------------");
								System.out.println("Price:" + price);
								System.out.println("PrevPrice: " +prevprice);
							}
						}
					} else {
						if (price>prevprice) {
							person.addOrderData("Price has increased. Changing price of sell.\nPrevious Highest Price: " + prevprice + "\nCurrent highest price: " + price + "\nNew sell order price: " + (price-trail) + "\n");
							System.out.println("---------------------------CHANGING PREVPRICE SELL - " + price + "   " + prevprice);
							prevprice = price;
						} else if (price<=prevprice-trail) {
							person.addOrderData("--------------------------------------------\nPlacing sell order now! \nStopping TrailingStop\n--------------------------------------------");
							System.out.println("---------------------------SELLING!!!----------------------");
							LimitOrder SellingOrder = new LimitOrder((OrderType.ASK), new BigDecimal(volume).setScale(8, RoundingMode.HALF_DOWN), pair, null, null, new BigDecimal(price).setScale(8, RoundingMode.HALF_DOWN));
							System.out.println(SellingOrder);
							//lastorder = exchange.getTradeService().placeLimitOrder(SellingOrder);
							run=false;
						} else {
							person.addOrderData("Price hasn't increased.\n Highest price: " + prevprice + "\nNew highest price: " + price + "\nSell order price: " + (prevprice-trail) + "\n");
							System.out.println("Price hasn't increased. Keeping sell at same price");
						}
						
					}
				}
				
			}
		}		
	}
	public void stopOrder() {
		run=false;
	}
}
