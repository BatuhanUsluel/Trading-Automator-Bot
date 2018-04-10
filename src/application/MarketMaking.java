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
import org.knowm.xchange.exceptions.ExchangeException;
import org.knowm.xchange.exceptions.NotAvailableFromExchangeException;
import org.knowm.xchange.exceptions.NotYetImplementedForExchangeException;

public class MarketMaking implements Runnable {
	ArrayList<JSONObject> OrdersMarketMaking = new ArrayList<JSONObject>();
	
	
	private JSONObject json;
	private String base;
	private String alt;
	private CurrencyPair pair;
	private double spread;
	private double MaxBal;
	private double MinBal;
	private Exchange exchange;
	private boolean ordercanceled;
	private double prevbid;
	private double prevask;
	private String prevbidorder;
	private String prevaskorder;
	private boolean firstrun=true;
	private double distancefrombest;
	
	public MarketMaking(JSONObject json) throws JSONException {
		this.json = json;
	}
	
	@Override
	public void run() {
		try {
			this.base = this.json.getString("base");
			this.alt = this.json.getString("alt");
			this.pair = new CurrencyPair(this.base,this.alt);
			this.spread = Double.parseDouble(this.json.getString("spread"));
			this.MaxBal = Double.parseDouble(this.json.getString("MaxBal"));
			this.MinBal = Double.parseDouble(this.json.getString("MinBal"));
			this.exchange = Exchanges.exchangemap.get(this.json.getString("exchange"));
		} catch (JSONException e) {
			e.printStackTrace();
		}
        try {
			//int loop = Integer.parseInt(message.getString("loop"));
           
			while (ordercanceled!=true) {
            		System.out.println("Sending Market Making Request");
            		JSONObject jsonrun = this.json;
					jsonrun.put("millis", System.currentTimeMillis());
					OrdersMarketMaking.add(jsonrun);
                	System.out.println(jsonrun);
                	SocketCommunication.out.print(jsonrun.toString());
                	SocketCommunication.out.flush();
            	TimeUnit.SECONDS.sleep(60);
            }
		} catch (InterruptedException | JSONException e) {
			e.printStackTrace();
		
		}
	}
	public void recievedMarketOrder(JSONObject message) throws JSONException, NotAvailableFromExchangeException, NotYetImplementedForExchangeException, ExchangeException, IOException  {
		System.out.println("Recieved PendingOrder Message!");
		for (int i = 0; i < OrdersMarketMaking.size(); i++) {
			JSONObject listitem = OrdersMarketMaking.get(i);
			
			if ((listitem.getString("base").equals(message.getString("base")))
			&& (listitem.getString("alt").equals(message.getString("alt")))
			&& (listitem.getString("spread").equals(message.getString("spread")))
			&& (listitem.getString("MaxBal").equals(message.getString("MaxBal")))
			&& (listitem.getString("MinBal").equals(message.getString("MinBal")))
			&& (listitem.getString("Exchanges").equals(message.getString("Exchanges")))
			&& (listitem.getString("licenceKey").equals(message.getString("licenceKey")))
			&& (listitem.getString("request").equals(message.getString("request")))
			&& listitem.getLong("millisstart") == (message.getLong("millisstart"))
			&& listitem.getLong("millis") == (message.getLong("millis"))) {
				OrdersMarketMaking.remove(listitem);
				double bid = Double.parseDouble(message.getString("bid"));
				double ask = Double.parseDouble(message.getString("ask"));
				if (firstrun==true) {
					LimitOrder buyingorder = new LimitOrder((OrderType.BID), new BigDecimal(5), this.pair, null, null, new BigDecimal(bid+distancefrombest));
					prevbidorder = exchange.getTradeService().placeLimitOrder(buyingorder);
					LimitOrder sellingorder  = new LimitOrder((OrderType.ASK), new BigDecimal(5), this.pair, null, null, new BigDecimal(ask-distancefrombest));
					prevaskorder = exchange.getTradeService().placeLimitOrder(sellingorder);
					prevbid = bid;
					prevask = ask;
				} else {
					if (bid!=prevbid) {
						exchange.getTradeService().cancelOrder(prevbidorder);
						LimitOrder buyingorder = new LimitOrder((OrderType.BID), new BigDecimal(5), this.pair, null, null, new BigDecimal(bid+distancefrombest));
						prevbidorder = exchange.getTradeService().placeLimitOrder(buyingorder);
					} else {
						System.out.println("Bid is equal to prevbid");
					}
					if (ask!=prevask) {
						exchange.getTradeService().cancelOrder(prevaskorder);
						LimitOrder sellingorder  = new LimitOrder((OrderType.ASK), new BigDecimal(5), this.pair, null, null, new BigDecimal(ask-distancefrombest));
						prevaskorder = exchange.getTradeService().placeLimitOrder(sellingorder);
					} else {
						System.out.println("Ask is equal to prevask");
					}
				}
			}
		}
	}
	
	public void cancelMarketOrder() {
		System.out.println("cancel Market Making Order!!");
		this.ordercanceled = true;
	}
}
