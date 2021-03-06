package application;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import org.json.JSONException;
import org.json.JSONObject;
import org.knowm.xchange.Exchange;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.Order.OrderType;
import org.knowm.xchange.dto.trade.LimitOrder;

import controllers.DashboardController;
import controllers.DashboardController.Person;
public class PendingOrder implements Runnable {
	ArrayList<JSONObject> OrdersPending = new ArrayList<JSONObject>();
	
	private JSONObject json;
	private String base;
	private String alt;
	private double priceorder;
	private double volume;
	private double percent;
	private Exchange exchange;
	private boolean buy;
	private boolean orderplaced;
	private CurrencyPair pair;
	private boolean ordercanceled = false;
	private String buystring;
	Person person;
	public PendingOrder(JSONObject json) throws JSONException {
		this.json = json;
	}
	   
	@Override
	public void run() {
		try {
			this.base = this.json.getString("base");
			this.alt = this.json.getString("alt");
			this.pair = new CurrencyPair(this.base,this.alt);
			this.priceorder = Double.parseDouble(this.json.getString("priceorder"));
			this.volume = Double.parseDouble(this.json.getString("volume"));
			this.percent = Double.parseDouble(this.json.getString("percent"));
			this.exchange = Exchanges.exchangemap.get(this.json.getString("Exchanges"));
			DashboardController dash = new DashboardController();

			boolean buy = true;
				
				if (this.json.getString("buysell").equals("Buy")) {
					buy=true;
					buystring="Buy";
				} else if (this.json.getString("buysell").equals("Sell")) {
					buy=false;
					buystring="Sell";
				} else {
					System.out.println("error!");
				}
			this.buy = buy;
	    	try {
				person = dash.newOrder(json);
				person.addOrderData("Starting Pending Stop\n"
						+ String.format("%-10s:%10s\n","Base", base)
						+ String.format("%-10s:%10s\n","Alt", alt)
						+ String.format("%-10s:%10s\n","Volume", volume)
						+ String.format("%-10s:%10s\n","Exchange", exchange)
						+ String.format("%-10s:%10s\n","Price of Order", priceorder)
						+ String.format("%-10s:%10s\n","Percent", percent)
						+ String.format("%-10s:%10s\n","Order Type", buystring) + "--------------------------------------\n") ;
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		
        try {
            while (orderplaced!=true && ordercanceled!=true) {
            	person.addOrderData("\nRequesting price\n");
            	CurrencyPair pair = new CurrencyPair(alt,base);
            	double price = exchange.getMarketDataService().getTicker(pair).getBid().doubleValue();
				person.addOrderData("Recieved price: " + price);
				if (ordercanceled!=true) {
				if (this.buy==true) { //Buying order
					if((this.priceorder*(1+this.percent))>=price) { //Buying order activated
						LimitOrder BuyingOrder = new LimitOrder((OrderType.BID), new BigDecimal(this.volume), this.pair, null, null, new BigDecimal(this.priceorder));
						System.out.println(BuyingOrder.toString());
						this.orderplaced = true;
						person.addOrderData("Trigger Price has been hit for buy order, order has been placed!\n-------------------------------------------\n Stopping Pending Order.");
						person.setRunning("False");
					} else {
						System.out.println("Trigger Price for BUY is: "+ (this.priceorder*(1+(0.01 * this.percent))) + " which is lower than current price: " + price + "\n");
						person.addOrderData("Trigger Price is: "+ (this.priceorder*(1+(0.01*this.percent))) + ", lower than current price: " + price);
					}
				} else { //Selling order
					if((this.priceorder*(1-this.percent))<=price) {  //Selling order activated
						LimitOrder SellingOrder = new LimitOrder((OrderType.ASK), new BigDecimal(this.volume), this.pair, null, null, new BigDecimal(this.priceorder));
						System.out.println(SellingOrder.toString());
						this.orderplaced = true;
						person.addOrderData("Trigger Price has been hit for sell order, order has been placed!\n-------------------------------------------\n Stopping Pending Order.");
						person.setRunning("False");
					} else {
						System.out.println("Trigger Price for SELL is: "+ (this.priceorder*(1-this.percent)) + " which is higher than current price: " + price);
						person.addOrderData("Trigger Price is: "+ (this.priceorder*(1-this.percent)) + ", higher than current price: " + price + "\n");
					}
				  }
				}
            	TimeUnit.SECONDS.sleep(60);
            }
		} catch (InterruptedException | JSONException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
	public void stopOrder() {
		System.out.println("cancelPendingOrder!!");
		person.addOrderData("\nPending order has been manually canceled from dashboard.\n-------------------------------------------\n Stopping Pending Order.");
		this.ordercanceled = true;
	}	
}