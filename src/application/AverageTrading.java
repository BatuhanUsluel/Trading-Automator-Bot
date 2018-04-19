package application;
import java.io.IOException;
import java.lang.Double;
import java.lang.Thread;
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
import org.knowm.xchange.exceptions.ExchangeException;
import org.knowm.xchange.exceptions.NotAvailableFromExchangeException;
import org.knowm.xchange.exceptions.NotYetImplementedForExchangeException;

import controllers.DashboardController;
import controllers.DashboardController.Person;

import  java.lang.Thread;
public class AverageTrading  implements Runnable  {
	public ArrayList<JSONObject> Orders = new ArrayList<JSONObject>();
	
	Person person;
	private JSONObject json;
	private double coinstotrade;
	private int loop;
	private double total;
	private boolean run = true;
	private boolean ordercanceled = false;

	private String alt;

	private String exchangestring;

	private Object exchange;

	private String buystring;

	private boolean buy;

	private String base;

	private double volume;
	
	public AverageTrading(JSONObject json) throws JSONException {
		this.json = json;
	}
	
	public void run() {
    	try {
    		DashboardController dash = new DashboardController();
    		person = dash.newOrder(json);
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
    	
        try {
			this.loop = java.lang.Integer.parseInt(json.getString("loop"));
			this.total = 0;
			this.coinstotrade = Double.parseDouble(json.getString("coinstotrade"));
			this.base = json.getString("base");
			this.alt = json.getString("alt");
			this.exchangestring = json.getString("Exchanges");
			this.volume = Double.parseDouble(json.getString("volumeperorder"));
			this.exchange = Exchanges.exchangemap.get(exchangestring);
			this.buystring= json.getString("buy");
			if (buystring.equals("Buy")) {
				this.buy=true;
			} else if (buystring.equals("Sell")) {
				this.buy=false;
			} else {
				System.out.println("error!");
			}
			person.addOrderData("Starting Average Trading"
					+ "\nParameters:\n"
					+ "Base: " + base
					+ "\nAlt: " + alt
					+ "\nLoop Time: " +  loop
					+ "\nExchange: " +  exchangestring
					+ "\nCoins to Trade: " + coinstotrade
					+ "\nVolume Per Order: " + volume
					+ "\nOrder Type: " + buystring + "\n--------------------------------------\n\n") ;
			
            while (run==true && ordercanceled!=true) {
            	person.addOrderData("\nSending price request\n");
        		if (coinstotrade>total+volume) {
        			priceRequest(json);
        			total = total+volume;
        			System.out.println("Total: " + total + "\nCoins2trade: " + coinstotrade + "\nOrdersize: " + volume);
        		} else {
        			double newordersize = coinstotrade-total;
        			JSONObject jsonnew = json.put("volumeperorder", String.valueOf(newordersize));
        			priceRequest(jsonnew);
        			volume = newordersize;
        			total = total+volume;
        			run=false;
        		}
            	System.out.println("Waiting for " + loop);
            	TimeUnit.SECONDS.sleep(loop);
            }
            if (run==false) {
            	person.addOrderData("\n\n--------------------------------------\nAverage Trading has automatically ended!\n Total coins traded has reaced max of " + coinstotrade);
            }
		} catch (JSONException | InterruptedException e) {
			e.printStackTrace();
		}
        
	}
	
    public void priceRequest(JSONObject JSONObject) throws JSONException {
    	JSONObject.put("millis", System.currentTimeMillis());
    	Orders.add(JSONObject);
    	SocketCommunication.out.print(JSONObject.toString());
    	SocketCommunication.out.flush();
    }
    
	public void recievedAverageTrade(JSONObject message) throws JSONException, NotAvailableFromExchangeException, NotYetImplementedForExchangeException, ExchangeException, IOException {
		System.out.println("Recieved AVERAGEE");
		System.out.println(Orders.toString());
		for (int i = 0; i < Orders.size(); i++) {
			JSONObject listitem = Orders.get(i);
			if (listitem.getLong("millis") == (message.getLong("millis"))) {
				Orders.remove(listitem);
            	person.addOrderData("Recieved price request\n");

				double price = Double.parseDouble(message.getString("price"));
				
				if (ordercanceled!=true) {
					CurrencyPair pair = new CurrencyPair(alt,base);
					if (buy==true) {
						LimitOrder BuyingOrder = new LimitOrder((OrderType.BID), new BigDecimal(volume).setScale(8, RoundingMode.HALF_DOWN), pair, null, null, new BigDecimal(price).setScale(8, RoundingMode.HALF_DOWN));
						System.out.println(BuyingOrder);
						//String limitOrderReturnValueBUY = exchange.getTradeService().placeLimitOrder(BuyingOrder);
		            	person.addOrderData("Placing buy order @ " + price + " with volume: " + volume + "\n");
					} else {
						LimitOrder SellingOrder = new LimitOrder((OrderType.ASK), new BigDecimal(volume).setScale(8, RoundingMode.HALF_DOWN), pair, null, null, new BigDecimal(price).setScale(8, RoundingMode.HALF_DOWN));
						System.out.println(SellingOrder);
						//String limitOrderReturnValueSELL = exchange.getTradeService().placeLimitOrder(SellingOrder);
						person.addOrderData("Placing sell order @ " + price + " with volume: " + volume + "\n");
					}
				}
			}
		}		
	}	
	
	public void stopOrder() {
		System.out.println("cancel Average order!!!");
		person.addOrderData("\nAverage order has been manually canceled from dashboard.\n-------------------------------------------\n Stopping Average Trading.");
		this.ordercanceled = true;
	}
}
