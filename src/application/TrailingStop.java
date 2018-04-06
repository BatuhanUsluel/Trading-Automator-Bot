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
import org.knowm.xchange.dto.Order;
import org.knowm.xchange.dto.Order.OrderType;
import org.knowm.xchange.dto.trade.LimitOrder;
import org.knowm.xchange.dto.trade.StopOrder;
import org.knowm.xchange.exceptions.ExchangeException;
import org.knowm.xchange.exceptions.NotAvailableFromExchangeException;
import org.knowm.xchange.exceptions.NotYetImplementedForExchangeException;

public class TrailingStop {
	ArrayList<JSONObject> OrdersTrailing = new ArrayList<JSONObject>();
	boolean run = true;
	boolean firstrun = true;
	String lastorder;
	double prevprice;
	LimitOrder LastOrder;
	public void runOrder(JSONObject message) {
			final JSONObject messagefinal = message;
			Thread thread = new Thread(new Runnable() {
				public void run() {
	            boolean run=true;
	            try {
					//int loop = Integer.parseInt(message.getString("loop"));
		            while (run==true) {
		            		System.out.println("Sending Order Request");
		            		messagefinal.put("millis", System.currentTimeMillis());
		                	OrdersTrailing.add(messagefinal);
		                	System.out.println(messagefinal);
		                	SocketCommunication.out.print(messagefinal.toString());
		                	SocketCommunication.out.flush();
		            	TimeUnit.SECONDS.sleep(60);
		            }
				} catch (InterruptedException | JSONException e) {
					e.printStackTrace();
				}
	        }
		});
	    thread.start();
	}
	
	public void recievedTrailingStop(JSONObject message) throws JSONException, NotAvailableFromExchangeException, NotYetImplementedForExchangeException, ExchangeException, IOException {
		System.out.println("Recieved TrailingStop Message!");
		for (int i = 0; i < OrdersTrailing.size(); i++) {
			JSONObject listitem = OrdersTrailing.get(i);
			if ((listitem.getString("base").equals(message.getString("base")))
			&& (listitem.getString("alt").equals(message.getString("alt")))
			&& (listitem.getString("request").equals(message.getString("request")))
			&& (listitem.getString("volume").equals(message.getString("volume")))
			&& (listitem.getString("trail").equals(message.getString("trail")))
			&& (listitem.getString("buysell").equals(message.getString("buysell")))
			&& (listitem.getString("exchange").equals(message.getString("exchange")))
			&& (listitem.getString("licenceKey").equals(message.getString("licenceKey")))
			&& listitem.getLong("millisstart") == (message.getLong("millisstart"))
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
				double volume = Double.parseDouble(listitem.getString("volume"));
				double trail = Double.parseDouble(listitem.getString("trail"));
				double price = Double.parseDouble(message.getString("price"));
				CurrencyPair pair = new CurrencyPair(altcoin,basecoin);
				
				if (firstrun==true) {
					
					if (buy==true) {
						System.out.println("---------------------------TRADING FIRST RUN BUY----------------------");
						LastOrder = new LimitOrder((OrderType.BID), new BigDecimal(volume).setScale(8, RoundingMode.HALF_DOWN), pair, null, null, new BigDecimal(price+trail).setScale(8, RoundingMode.HALF_DOWN));
						System.out.println(LastOrder);
						//lastorder = exchange.getTradeService().placeLimitOrder(LastOrder);
						prevprice = price;
					} else {
						System.out.println("---------------------------TRADING FIRST RUN SELL----------------------");
						prevprice = price;
					}
					firstrun=false;
				} else {
					if (buy==true) {
						if (LastOrder.getStatus()== Order.OrderStatus.PARTIALLY_FILLED ) {
							System.out.println("Order is partially filled, not doing anything");
						} else if(LastOrder.getStatus() == Order.OrderStatus.FILLED) {
							run=false;
						} else {
							if (price<prevprice) {
								System.out.println("---------------------------CHANGING BUY----------------------");
								//exchange.getTradeService().cancelOrder(lastorder);
								LastOrder = new LimitOrder((OrderType.BID), new BigDecimal(volume).setScale(8, RoundingMode.HALF_DOWN), pair, null, null, new BigDecimal(price+trail).setScale(8, RoundingMode.HALF_DOWN));
								//lastorder = exchange.getTradeService().placeLimitOrder(BuyingOrder);
								System.out.println("Price:" + price);
								System.out.println("PrevPrice: " +prevprice);
								prevprice = price;
							} else {
								System.out.println("---------------------------NOT TRADING BUY----------------------");
								System.out.println("Price:" + price);
								System.out.println("PrevPrice: " +prevprice);
							}
						}
					} else {
						if (price>prevprice) {
							System.out.println("---------------------------CHANGING PREVPRICE SELL----------------------");
							prevprice = price;
						} else if (price<=prevprice-trail) {
							System.out.println("---------------------------SELLING!!!----------------------");
							LimitOrder SellingOrder = new LimitOrder((OrderType.ASK), new BigDecimal(volume).setScale(8, RoundingMode.HALF_DOWN), pair, null, null, new BigDecimal(price).setScale(8, RoundingMode.HALF_DOWN));
							System.out.println(SellingOrder);
							//lastorder = exchange.getTradeService().placeLimitOrder(SellingOrder);
							run=false;
						}
						
					}
				}
				
			}
		}		
	}
}