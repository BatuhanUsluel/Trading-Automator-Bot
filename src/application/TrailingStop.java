package application;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

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
public class TrailingStop implements Runnable {
	ArrayList<JSONObject> OrdersTrailing = new ArrayList<JSONObject>();
	boolean run;
	boolean firstrun = true;
	String lastorder;
	double prevprice;
	LimitOrder LastOrder;
	Person person;
	private String base;
	private String alt;
	private double volume;
	private String exchangeS;
	private double trail;
	private CurrencyPair pair;
	private boolean buy;
	private Exchange exchange;
	private JSONObject json;
	
	public TrailingStop(String base, String alt, String volume, String exchangeS, String trail, String buysell, JSONObject json) {
		this.base = base;
		this.alt=alt;
		this.volume = Double.parseDouble(volume);
		this.exchangeS = exchangeS;
		this.exchange = Exchanges.exchangemap.get(exchangeS);
		this.trail = Double.parseDouble(trail);
		this.pair = new CurrencyPair(alt,base);
		if (buysell.equals("Buy")) {
			this.buy=true;
		} else if (buysell.equals("Sell")) {
			this.buy=false;
		}
		this.json=json;
	}
	public void run() {
    		Main.logger.log(Level.INFO, "Running trailing stop");
			Thread thread = new Thread(new Runnable() {
				public void run() {
				run=true;
				DashboardController dash = new DashboardController();
	        	try {
					person = dash.newOrder(json);
					person.addOrderData("Starting Trailing Stop\n"
					+ String.format("%-10s:%10s\n","Base",base)
					+ String.format("%-10s:%10s\n","Alt", alt)
					+ String.format("%-10s:%10s\n","Volume", volume)
					+ String.format("%-10s:%10s\n","Exchange", exchangeS)
					+ String.format("%-10s:%10s\n","Trail", trail)
					+ String.format("%-10s:%10s\n","Order Type", buy)
					+ "--------------------------------------\n");
				} catch (JSONException e) {
					e.printStackTrace();
				}

	            try {
		            while (run==true) {
		            		person.addOrderData("\nRequesting price\n");
		            		checkPrice();
		            	TimeUnit.MINUTES.sleep(5);
		            }
				} catch (InterruptedException | JSONException e) {
					e.printStackTrace();
				}
	        }
		});
		thread.setDaemon(true);
	    thread.start();
	}
	
	public void checkPrice() {
		double price;
		try {
			price = exchange.getMarketDataService().getTicker(pair).getBid().doubleValue();
				if (firstrun==true) {
					if (buy==true) {
						double altvolume = volume/(price+trail);
						person.addOrderData("First run\nCurrent price: " + price + "\nPlacing Buy Order @ " + round((price+trail),6) + "\n");
						LastOrder = new LimitOrder((OrderType.BID), new BigDecimal(altvolume).setScale(8, RoundingMode.HALF_DOWN), pair, null, null, new BigDecimal(price+trail).setScale(8, RoundingMode.HALF_DOWN));
						System.out.println(LastOrder);
						lastorder = exchange.getTradeService().placeLimitOrder(LastOrder);
						prevprice = price;
					} else {
						person.addOrderData("First run\nCurrent price: " + price + "\nSell order price: " + round((price-trail),6) +"\n");
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
								person.addOrderData("Price has decreased. Changing price of buy.\nPrevious Lowest Price: " + prevprice + "\nCurrent lowest price: " + price + "\nNew buy order price: " + (price+trail) + "\n");
								System.out.println("---------------------------CHANGING BUY----------------------");
								exchange.getTradeService().cancelOrder(lastorder);
								double altvolume = volume/(price+trail);
								LastOrder = new LimitOrder((OrderType.BID), new BigDecimal(altvolume).setScale(8, RoundingMode.HALF_DOWN), pair, null, null, new BigDecimal(price+trail).setScale(8, RoundingMode.HALF_DOWN));
								lastorder = exchange.getTradeService().placeLimitOrder(LastOrder);
								prevprice = price;
							} else {
								person.addOrderData("Price hasn't decreased.\nLowest price: " + prevprice + "\nCurrent price: " + price + "\nBuy order price: " + (prevprice+trail) + "\n");
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
							double altvolume = volume/(price);
							LimitOrder SellingOrder = new LimitOrder((OrderType.ASK), new BigDecimal(altvolume).setScale(8, RoundingMode.HALF_DOWN), pair, null, null, new BigDecimal(price).setScale(8, RoundingMode.HALF_DOWN));
							System.out.println(SellingOrder);
							lastorder = exchange.getTradeService().placeLimitOrder(SellingOrder);
							run=false;
						} else {
							person.addOrderData("Price hasn't increased.\nHighest price: " + prevprice + "\nCurrent price: " + price + "\nSell order price: " + round((prevprice-trail),6) + "\n");
							System.out.println("Price hasn't increased. Keeping sell at same price");
						}
					}
				}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void stopOrder() {
		person.addOrderData("\nTrailing Stop order has been manually stopped from dashboard.\n-------------------------------------------\n Stopping Trailing Stop.");
		run=false;
	}
	public static double round(double value, int places) {
	    if (places < 0) throw new IllegalArgumentException();

	    BigDecimal bd = new BigDecimal(value);
	    bd = bd.setScale(places, RoundingMode.HALF_DOWN);
	    return bd.doubleValue();
	}
}
