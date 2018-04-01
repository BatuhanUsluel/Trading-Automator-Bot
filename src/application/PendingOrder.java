package application;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import org.json.JSONException;
import org.json.JSONObject;
import org.knowm.xchange.Exchange;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.Order.OrderType;
import org.knowm.xchange.dto.trade.LimitOrder;

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
			this.exchange = Exchanges.exchangemap.get(this.json.getString("base"));
			boolean buy = true;
				if (this.json.getString("base").equals("Buy")) {
					buy=true;
				} else if (this.json.getString("base").equals("Sell")) {
					buy=false;
				} else {
					System.out.println("error!");
				}
			this.buy = buy;
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		
        try {
			//int loop = Integer.parseInt(message.getString("loop"));
            while (orderplaced!=true && ordercanceled!=true) {
            		System.out.println("Sending Order Request");
            		JSONObject jsonrun = this.json;
					jsonrun.put("millis", System.currentTimeMillis());
            		OrdersPending.add(jsonrun);
                	System.out.println(jsonrun);
                	SocketCommunication.out.print(jsonrun.toString());
                	SocketCommunication.out.flush();
            	TimeUnit.SECONDS.sleep(60);
            }
		} catch (InterruptedException | JSONException e) {
			e.printStackTrace();
		}
	}
	
	public void recievedPendingOrder(JSONObject message) throws JSONException  {
		System.out.println("Recieved PendingOrder Message!");
		for (int i = 0; i < OrdersPending.size(); i++) {
			JSONObject listitem = OrdersPending.get(i);
			if ((listitem.getString("base").equals(message.getString("base")))
			&& (listitem.getString("alt").equals(message.getString("alt")))
			&& (listitem.getString("request").equals(message.getString("request")))
			&& (listitem.getString("volume").equals(message.getString("volume")))
			&& (listitem.getString("priceorder").equals(message.getString("trail")))
			&& (listitem.getString("percent").equals(message.getString("buysell")))
			&& (listitem.getString("exchange").equals(message.getString("exchange")))
			&& (listitem.getString("licenceKey").equals(message.getString("licenceKey")))
			&& (listitem.getString("buysell").equals(message.getString("buysell")))
			&& listitem.getLong("millisstart") == (message.getLong("millisstart"))
			&& listitem.getLong("millis") == (message.getLong("millis"))) {
				OrdersPending.remove(listitem);
				double price = Double.parseDouble(message.getString("price"));
				
				if (this.buy==true) { //Buying order
					if((this.priceorder*(1+this.percent))>=price) { //Buying order activated
						LimitOrder BuyingOrder = new LimitOrder((OrderType.BID), new BigDecimal(this.volume), this.pair, null, null, new BigDecimal(this.priceorder));
						System.out.println(BuyingOrder.toString());
						this.orderplaced = true;
					} else {
						System.out.println("Trigger Price for BUY is: "+ (this.priceorder*(1+this.percent)) + " which is lower than current price: " + price);
					}
				} else { //Selling order
					if((this.priceorder*(1-this.percent))<=price) {  //Selling order activated
						LimitOrder SellingOrder = new LimitOrder((OrderType.ASK), new BigDecimal(this.volume), this.pair, null, null, new BigDecimal(this.priceorder));
						System.out.println(SellingOrder.toString());
						this.orderplaced = true;
					} else {
						System.out.println("Trigger Price for SELL is: "+ (this.priceorder*(1-this.percent)) + " which is higher than current price: " + price);
					}
				}
			}
		}
	}
	
	public void cancelPendingOrder() {
		System.out.println("cancelPendingOrder!!");
		this.ordercanceled = true;
	}
}