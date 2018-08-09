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
import org.knowm.xchange.dto.trade.MarketOrder;
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
	private boolean above;
	private String buysell;
	private OrderType ordertype;
	
	public TrailingStop(String base, String alt, String volume, String exchangeS, String trail, String buysell, String abovebelow, JSONObject json) {
		this.base = base;
		this.alt=alt;
		this.volume = Double.parseDouble(volume);
		this.exchangeS = exchangeS;
		this.exchange = Exchanges.exchangemap.get(exchangeS);
		this.trail = Double.parseDouble(trail);
		this.pair = new CurrencyPair(alt,base);
		this.buysell=buysell;
		if (buysell.equals("Buy")) {
			this.ordertype = OrderType.BID;
			this.buy=true;
		} else if (buysell.equals("Sell")) {
			this.ordertype = OrderType.ASK;
			this.buy=false;
		}
		this.json=json;
		if(abovebelow.equals("Above Price")) {
			this.above = true;
		} else {
			this.above = false;
		}
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
				if (firstrun) {
					if (above) { //Above Price
						person.addOrderData("First run\nCurrent price: " + price + "\nWill " + buysell + " if price hits " + round((price+trail),6) + "\n");
						prevprice = price;
					} else { //Below Price
						person.addOrderData("First run\nCurrent price: " + price + "\nWill " + buysell + " if price hits: " + round((price-trail),6) +"\n");
						prevprice = price;
					}
					firstrun=false;
				} else { //Not first
					if (above) { //Above
							if (price<prevprice) {
								person.addOrderData("Price has decreased. Changing price of order.\nPrevious Lowest Price: " + prevprice + "\nCurrent lowest price: " + price + "\nNew order price: " + round((price+trail),8) + "\n");
								prevprice = price;
							} else if (price>=prevprice+trail) {
								person.addOrderData("Order activation price has been reached");
								double altvolume = volume/(price);
								MarketOrder marketOrder = new MarketOrder(ordertype, new BigDecimal(altvolume).setScale(8, RoundingMode.HALF_DOWN), pair);
								person.addOrderData(marketOrder.toString());
								person.addOrderData("Sending Market Order to exchange");
								String orderReturnValue = exchange.getTradeService().placeMarketOrder(marketOrder);
								person.addOrderData(orderReturnValue);
								run=false;
							} else {
								person.addOrderData("Price hasn't decreased.\nLowest price: " + prevprice + "\nCurrent price: " + price + "\nOrder price: " + (prevprice+trail) + "\n");
							}
							
					} else { //Below
						if (price>prevprice) {
							person.addOrderData("Price has increased. Changing price of order.\nPrevious Highest Price: " + prevprice + "\nCurrent highest price: " + price + "\nNew order price: " + (price-trail) + "\n");
							prevprice = price;
						} else if (price<=prevprice-trail) {
							person.addOrderData("Order activation price has been reached");
							double altvolume = volume/(price);
							MarketOrder marketOrder = new MarketOrder(ordertype, new BigDecimal(altvolume).setScale(8, RoundingMode.HALF_DOWN), pair);
							person.addOrderData(marketOrder.toString());
							person.addOrderData("Sending Market Order to exchange");
							String orderReturnValue = exchange.getTradeService().placeMarketOrder(marketOrder);
							person.addOrderData(orderReturnValue);
							run=false;
						} else {
							person.addOrderData("Price hasn't increased.\nHighest price: " + prevprice + "\nCurrent price: " + price + "\nOrder price: " + round((prevprice-trail),6) + "\n");
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
