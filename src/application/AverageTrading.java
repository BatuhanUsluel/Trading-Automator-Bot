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
import org.knowm.xchange.dto.Order.OrderType;
import org.knowm.xchange.dto.marketdata.Ticker;
import org.knowm.xchange.dto.trade.LimitOrder;
import org.knowm.xchange.exceptions.ExchangeException;
import org.knowm.xchange.exceptions.NotAvailableFromExchangeException;
import org.knowm.xchange.exceptions.NotYetImplementedForExchangeException;

import controllers.DashboardController;
import controllers.DashboardController.Person;
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
	private Exchange exchange;
	private String buystring;
	private boolean buy;
	private String base;
	private double volume;
	private String atbidstring;
	private boolean bid;

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
			this.atbidstring = json.getString("atbid");
			if (atbidstring.equals("At Bid")) {
				this.bid = true;
			} else {
				this.bid = false;
			}
			if (buystring.equals("Buy")) {
				this.buy=true;
			} else if (buystring.equals("Sell")) {
				this.buy=false;
			} else {
				System.out.println("error!");
			}
			person.addOrderData("Starting Average Trading\n"
					+ String.format("%-10s:%10s\n","Base", base)
					+ String.format("%-10s:%10s\n","Alt", alt)
					+ String.format("%-10s:%10s\n","Loop Time", loop)
					+ String.format("%-10s:%10s\n","Exchange", exchangestring)
					+ String.format("%-10s:%10s\n","Coins to Trade", coinstotrade)
					+ String.format("%-10s:%10s\n","Volume Per Order", volume)
					+ String.format("%-10s:%10s\n","Order Type", buystring)
					+ "--------------------------------------\n") ;
			
            while (run==true && ordercanceled!=true) {
            	person.addOrderData("\nSending price request\n");
        		if (coinstotrade>total+volume) {
        			getPrice();
        			total = total+volume;
        			System.out.println("Total: " + total + "\nCoins2trade: " + coinstotrade + "\nOrdersize: " + volume);
        		} else {
        			double newordersize = coinstotrade-total;
        			volume = newordersize;
        			total = total+volume;
        			getPrice();
        			run=false;
        			person.setRunning("False");
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
    
	public void getPrice() {
		CurrencyPair pair = new CurrencyPair(alt,base);
		Ticker ticker;
		try {
			ticker = exchange.getMarketDataService().getTicker(pair);
			double price;
			if (bid) {
				price = ticker.getBid().doubleValue();
			} else {
				price = ticker.getAsk().doubleValue();
			}
			
			if (ordercanceled!=true) {
				if (buy) {
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
		} catch (IOException e) {
			e.printStackTrace();
		}
	}	
	
	public void stopOrder() {
		person.addOrderData("\nAverage order has been manually stopped from dashboard.\n-------------------------------------------\n Stopping Average Trading.");
		Main.logger.log(Level.INFO, "Average order has been stopped");
		this.ordercanceled = true;
	}
}
