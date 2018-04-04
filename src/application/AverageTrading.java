package application;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.lang.Long;
import javax.json.Json;
import javax.json.JsonObject;
import java.lang.Runnable;
import java.lang.Integer;
import org.json.JSONException;
import org.json.JSONObject;
import org.knowm.xchange.Exchange;
import org.knowm.xchange.ExchangeFactory;
import org.knowm.xchange.ExchangeSpecification;
import org.knowm.xchange.currency.Currency;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.Order.OrderType;
import org.knowm.xchange.dto.account.Balance;
import org.knowm.xchange.dto.marketdata.Ticker;
import org.knowm.xchange.dto.trade.LimitOrder;
import org.knowm.xchange.exceptions.ExchangeException;
import org.knowm.xchange.exceptions.NotAvailableFromExchangeException;
import org.knowm.xchange.exceptions.NotYetImplementedForExchangeException;
import org.knowm.xchange.poloniex.PoloniexExchange;

public class AverageTrading {
	public static ArrayList<JSONObject> Orders = new ArrayList<JSONObject>();
	public static ArrayList<JSONObject> Orders1 = new ArrayList<JSONObject>();
	
	double volume = 5;
	static int seconds = 8;
	static double minmaxbalance = 5.0;
	static boolean sell = true;
	static boolean tobid=true;
	static String coin = "DCR";
	static String base = "BTC";
	static boolean stoprunning=false;
	/*
	public void main(String[] args) throws InterruptedException, NotAvailableFromExchangeException, NotYetImplementedForExchangeException, ExchangeException, IOException, JSONException {
		ExchangeSpecification PoloniexEx = new PoloniexExchange().getDefaultExchangeSpecification();
	    PoloniexEx.setUserName("Batu");
	    PoloniexEx.setApiKey("CBU5SD1G-G3K6U3VP-KIK9H73R-0AB52TC4");
	    PoloniexEx.setSecretKey("fe7885337c05f746c53a020dc7591aafa1ab7b337ee2fc0aeefe9d29d26974a10d28815ef789b170f40949808fd9b04c15fb0585841e9ab010b5a7c664d99bef");
	    Exchange exchange = ExchangeFactory.INSTANCE.createExchange(PoloniexEx);
		CurrencyPair pair = new CurrencyPair(coin,base);
    	Currency currency = new Currency(coin);
		while(stoprunning==false) {
			final Map<Currency, Balance> ALLBalances;
			ALLBalances = exchange.getAccountService().getAccountInfo().getWallet().getBalances();
			BigDecimal	AvailableBalance = ALLBalances.get(currency).getAvailable();
	    	Ticker ticker;
	    	ticker = exchange.getMarketDataService().getTicker(pair);

			JSONObject jsonobj = new JSONObject();
    		jsonobj = new JSONObject(ticker);
    		double bid;
    		double ask;
			bid = jsonobj.getDouble("bid");
			ask = jsonobj.getDouble("ask");
			
			if (sell==true) {
				if(AvailableBalance.doubleValue()>minmaxbalance) {
					if (tobid==true) {
						LimitOrder SellingOrder = new LimitOrder((OrderType.BID), new BigDecimal(volume), pair, null, null, new BigDecimal(bid));
						String limitOrderReturnValueSELL = exchange.getTradeService().placeLimitOrder(SellingOrder);
						System.out.println("Limit Order return value SELL: " + limitOrderReturnValueSELL);
						System.out.println("Placed sell order to BID!");
					} else {
						LimitOrder SellingOrder = new LimitOrder((OrderType.BID), new BigDecimal(volume), pair, null, null, new BigDecimal(ask));
						String limitOrderReturnValueSELL = exchange.getTradeService().placeLimitOrder(SellingOrder);
						System.out.println("Limit Order return value SELL: " + limitOrderReturnValueSELL);
						System.out.println("Placed buy order to ASK!");
					}
				} else {
					//FINNISH
					System.out.println("STOPPING-------------------------------------------");
					System.out.println("Available Balance is " + AvailableBalance + " which is less than min balance of " + minmaxbalance);
					stoprunning=true;
				}
			} else {
				if(AvailableBalance.doubleValue()<minmaxbalance) {
					if (tobid==true) {
						LimitOrder BuyingOrder = new LimitOrder((OrderType.BID), new BigDecimal(volume), pair, null, null, new BigDecimal(bid));
						String limitOrderReturnValueBUY = exchange.getTradeService().placeLimitOrder(BuyingOrder);
						System.out.println("Limit Order return value BUY: " + limitOrderReturnValueBUY);
						System.out.println("Placed buy order to BID!");
					} else {
						LimitOrder BuyingOrder = new LimitOrder((OrderType.BID), new BigDecimal(volume), pair, null, null, new BigDecimal(ask));
						String limitOrderReturnValueBUY = exchange.getTradeService().placeLimitOrder(BuyingOrder);
						System.out.println("Limit Order return value BUY: " + limitOrderReturnValueBUY);
						System.out.println("Placed buy order to ASK!");
					}
				} else {
					//FINNISH
					System.out.println("STOPPING-------------------------------------------");
					System.out.println("Available Balance is " + AvailableBalance + " which is more than max balance of " + minmaxbalance);
					stoprunning=true;
				}
			}
			TimeUnit.SECONDS.sleep(seconds);
		}
	}
	*/
	
	/*
	Currently Used Idea:
	Going to create AverageTrading class once. Add the message to an array.
	Then call a new thread for that Specific order, which loops every x seconds, checks if the order is still in an Array, and if so gets the price and buys.
	If it isn't in array, it closes the thread.
	Also keeps a counter for the total bought, and if it crosses the max it closes the thread. 
	When closing thread, it sets the status to closed, so it can be shown on the dashboard.
	
	New Idea:
	In the controller thread, when a new AverageTrading order is submitted, create a new AverageTrading class,
	add it to a dictionary with the key being the parameters(and the time), and the value being the class
	Then call the class constructor and set all the parameters(this.coin=(coin from method input)
	The constructor then should create a new thread, thread should access the variables by this.
	Then you can cancel it by calling the cancel method for that specific class(found from .get in dictionary)
	Which sets the cancel variable to 1, and the thread stops running because it checks for it (if run=!{cancel})
	*/
	
	public static void runOrder(JSONObject message) {
		class OneShotTask implements Runnable {
			JSONObject message;
			OneShotTask(JSONObject message2) {message= message2; }
	        public void run() {
	            boolean run=true;
	            
	            try {
	            	Orders1.add(message);
					int loop = Integer.parseInt(message.getString("loop"));
					double total = 0;
					double coinstotrade = Double.parseDouble(message.getString("coinstotrade"));
					double ordersize = Double.parseDouble(message.getString("volumeperorder"));
		            while (run==true) {
		            	if (Orders1.contains(message)) {
		            		System.out.println("Orders contains");
		            		if (coinstotrade>total+ordersize) {
		            			priceRequest(message);
		            			total = total+ordersize;
		            		} else {
		            			double newordersize = coinstotrade-total;
		            			message.put("volumeperorder", String.valueOf(newordersize));
		            			priceRequest(message);
		            			total = total+ordersize;
		            			run=false;
		            		}
		            	} else {
		            		System.out.println("Removing Order");
		            		run=false;
		            	}
		            	TimeUnit.SECONDS.sleep(loop);
		            }
				} catch (JSONException | InterruptedException e) {
					e.printStackTrace();
				}
	        }
		}
		
	    Thread t = new Thread(new OneShotTask(message));
	    t.start();
	}
	
    public static void priceRequest(JSONObject JSONObject) throws JSONException {
    	Orders.add(JSONObject);
    	System.out.println(JSONObject);
    	SocketCommunication.out.print(JSONObject.toString());
    	SocketCommunication.out.flush();
    }
    
	public static void recievedAverageTrade(JSONObject message) throws JSONException, NotAvailableFromExchangeException, NotYetImplementedForExchangeException, ExchangeException, IOException {
		System.out.println("Recieved AVERAGEE");
		System.out.println(Orders.toString());
		for (int i = 0; i < Orders.size(); i++) {
			JSONObject listitem = Orders.get(i);
			System.out.println("Looping");
			
			if ((listitem.getString("Basecoin").equals(message.getString("Basecoin")))
			&& (listitem.getString("Altcoin").equals(message.getString("Altcoin")))
			&& (listitem.getString("Exchanges").equals(message.getString("Exchanges")))
			&& (listitem.getString("request").equals(message.getString("request")))
			&& (listitem.getString("coinstotrade").equals(message.getString("coinstotrade")))
			&& (listitem.getString("volumeperorder").equals(message.getString("volumeperorder")))
			&& (listitem.getString("licenceKey").equals(message.getString("licenceKey")))
			&& (listitem.getString("atbid").equals(message.getString("atbid")))
			&& (listitem.getString("buy").equals(message.getString("buy")))
			&& (listitem.getString("loop").equals(message.getString("loop")))
			&& listitem.getLong("millis") == (message.getLong("millis"))) {
				Orders.remove(listitem);
				String basecoin = listitem.getString("Basecoin");
				String altcoin = listitem.getString("Altcoin");
				String exchangeString = listitem.getString("Exchanges");
				Exchange exchange = Exchanges.exchangemap.get(exchangeString);
				String buystring = listitem.getString("buy");
				boolean buy = true;
				if (buystring.equals("Buy")) {
					buy=true;
				} else if (buystring.equals("Sell")) {
					buy=false;
				} else {
					System.out.println("error!");
				}
				System.out.println("Buystring:" + buystring);
				double price = Double.parseDouble(message.getString("price"));
				double volume = Double.parseDouble(listitem.getString("volumeperorder"));
				CurrencyPair pair = new CurrencyPair(altcoin,basecoin);
				if (buy==true) {
					LimitOrder BuyingOrder = new LimitOrder((OrderType.BID), new BigDecimal(volume).setScale(8, RoundingMode.HALF_DOWN), pair, null, null, new BigDecimal(price).setScale(8, RoundingMode.HALF_DOWN));
					System.out.println(BuyingOrder);
					//String limitOrderReturnValueBUY = exchange.getTradeService().placeLimitOrder(BuyingOrder);
				} else {
					LimitOrder SellingOrder = new LimitOrder((OrderType.ASK), new BigDecimal(volume).setScale(8, RoundingMode.HALF_DOWN), pair, null, null, new BigDecimal(price).setScale(8, RoundingMode.HALF_DOWN));
					System.out.println(SellingOrder);
					//String limitOrderReturnValueSELL = exchange.getTradeService().placeLimitOrder(SellingOrder);
				}
			}
		}		
	}
	 
	public static void removeOrder(JSONObject message) {
		Orders1.remove(message);
	}
	
}
