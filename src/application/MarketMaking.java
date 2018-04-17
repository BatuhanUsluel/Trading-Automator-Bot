package application;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.json.JSONException;
import org.json.JSONObject;
import org.knowm.xchange.Exchange;
import org.knowm.xchange.currency.Currency;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.Order.OrderType;
import org.knowm.xchange.dto.account.Balance;
import org.knowm.xchange.dto.marketdata.Ticker;
import org.knowm.xchange.dto.trade.LimitOrder;
import org.knowm.xchange.exceptions.ExchangeException;
import org.knowm.xchange.exceptions.NotAvailableFromExchangeException;
import org.knowm.xchange.exceptions.NotYetImplementedForExchangeException;
import org.knowm.xchange.service.account.AccountService;
import org.knowm.xchange.service.marketdata.MarketDataService;
import org.knowm.xchange.service.trade.TradeService;

import controllers.DashboardController;
import controllers.DashboardController.Person;

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
	private Person person;
	private int loop;

	private AccountService accountExchange;
	private MarketDataService marketExchange;
	private TradeService tradeExchange;


	private Currency basecurrency;


	private Currency altcurrency;


	private String spreadstring;


	private String MaxBalString;


	private String MinBalString;


	private String exchangeString;


	private long millisstart;
	
	public static HashMap<java.lang.Long, BigDecimal> BaseBalance = new HashMap<java.lang.Long, BigDecimal>();
	public static HashMap<java.lang.Long, BigDecimal> AltBalance = new HashMap<java.lang.Long, BigDecimal>();
	public MarketMaking(JSONObject json) throws JSONException {
		this.json = json;
	}
	
	@Override
	public void run() {
		try {
	    	DashboardController dash = new DashboardController();
	    	person = dash.newOrder(json);
			this.base = this.json.getString("base");
			this.alt = this.json.getString("alt");
			this.basecurrency = new Currency(base);
			this.altcurrency = new Currency(alt);
			this.pair = new CurrencyPair(this.alt,this.base);
			this.spreadstring = this.json.getString("spread");
			this.MaxBalString = this.json.getString("MaxBal");
			this.MinBalString = this.json.getString("MinBal");
			this.millisstart = this.json.getLong("millisstart");
			this.spread = Double.parseDouble(spreadstring);
			this.MaxBal = Double.parseDouble(MaxBalString);
			this.MinBal = Double.parseDouble(MinBalString);
			this.exchangeString = this.json.getString("Exchanges");
			this.exchange = Exchanges.exchangemap.get(exchangeString);
			System.out.println(exchange);
			System.out.println(exchange.getExchangeSymbols());
			this.accountExchange = exchange.getAccountService();
			this.marketExchange = exchange.getMarketDataService();
			this.tradeExchange = exchange.getTradeService();
			//this.loop = Integer.parseInt(this.json.getString("loop"));
			person.addOrderData("Starting Market Making\n"
					+ "\nParameters:\n"
					+ "Base: " + base
					+ "\nAlt: " + alt
					+ "\nMin Spread " +  spread
					+ "\nExchange: " +  this.json.getString("Exchanges")
					+ "\nMax Balance " + MaxBal
					+ "\nMin Balance: " + MinBal + "\n--------------------------------------\n\n") ;
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
        try {
			//while (ordercanceled!=true) {
					java.lang.Long millis = System.currentTimeMillis();
            		person.addOrderData("Sending price request\n");
            		JSONObject jsonrun = this.json;
					jsonrun.put("millis", millis);
					OrdersMarketMaking.add(jsonrun);
                	System.out.println("Original Json: "  + jsonrun);
                	SocketCommunication.out.print(jsonrun);
                	SocketCommunication.out.flush();
        	    	Thread baseBalance = new Thread() {
        	    	    public void run() {
        	    	    	try {
        	    	    		BaseBalance.put(millis, accountExchange.getAccountInfo().getWallet().getBalance(basecurrency).getTotal());
        	    	    		
							} catch (NotAvailableFromExchangeException | NotYetImplementedForExchangeException
									| ExchangeException | IOException e) {
								e.printStackTrace();
							}
        	    	    }
        	    	};
        	    	baseBalance.start();      
        	    	
        	    	Thread altBalance = new Thread() {
        	    	    public void run() {
        	    	    	try {
        	    	    		AltBalance.put(millis, accountExchange.getAccountInfo().getWallet().getBalance(altcurrency).getTotal());
							} catch (NotAvailableFromExchangeException | NotYetImplementedForExchangeException
									| ExchangeException | IOException e) {
								e.printStackTrace();
							}
        	    	    }
        	    	};
        	    	altBalance.start();     
                	
            	TimeUnit.SECONDS.sleep(20);
            //}
		} catch (InterruptedException | JSONException e) {
			e.printStackTrace();
		
		}
	}
	
	/*It should get the balances at the same time as the price request is sent to my server,
	and then save it in a hashmap with the keys being the current time,
	and the answer being the balance(it should do this in a new thread).
	Then when the price request is recieved, it should check to see if the balance is null,
	and if it is block the code untill it runs, with a timeout of a few seconds.
	
	Also, you can make the ordersizes max of balance for now, and only run the bot while the market is moving sideways.
	Later on after the bot is fully finished and selling, add additonal volume features that adjust itself according to how far it is from the average of that days price
	
	TRADING STRATEGY:
	Works for low priced coins such as DOGE & BCN. Do normal market making between the lowest interval(60 and 61).
	BTW the engine probably matches orders on a first in first out basis.
	When the wall order size on one side(ask||bid) gets close to the size of total balance, dump into the wall, so the price moves, and place market making orders in new price
	Example:
	Ask: 0.60, Bid: 0.61. Ask volume gets low over time, so we buy with max volume, new ask moves to 61, we place our sell order @ 61, and move our bid with the market
	
	*/
	
	public void recievedMarketOrder(JSONObject message) throws JSONException, NotAvailableFromExchangeException, NotYetImplementedForExchangeException, ExchangeException, IOException, InterruptedException  {
		System.out.println("Recieved market order Message!");
			
			if ((base.equals(message.getString("base")))
			&& (alt.equals(message.getString("alt")))
			&& (spreadstring.toString().equals(message.getString("spread")))
			&& (MaxBalString.equals(message.getString("MaxBal")))
			&& (MinBalString.equals(message.getString("MinBal")))
			&& (exchangeString.equals(message.getString("Exchanges")))
			&& millisstart == (message.getLong("millisstart"))) {
				double bid = Double.parseDouble((message.getJSONObject("Returned").getString("Bid").split("\\,")[0]).substring(1));
				double ask = Double.parseDouble((message.getJSONObject("Returned").getString("Ask").split("\\,")[0]).substring(1));
				person.addOrderData("Bid: " + bid + "\nAsk: " + ask);
				int x=0;
				final balancesU balancesU = new balancesU();
				boolean dontrun=false;
				ArrayList<Thread> arrThreads = new ArrayList<Thread>();
				Thread baseBalance = new Thread() {
    	    	    public void run() {
    	    	    	try {
							balancesU.baseBalance = accountExchange.getAccountInfo().getWallet().getBalance(basecurrency).getTotal();
						} catch (NotAvailableFromExchangeException | NotYetImplementedForExchangeException
								| ExchangeException | IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
    	    	    }
				};
				baseBalance.start();
				arrThreads.add(baseBalance);
				Thread altBalance = new Thread() {
    	    	    public void run() {
    	    	    	try {
							balancesU.altBalance = accountExchange.getAccountInfo().getWallet().getBalance(altcurrency).getTotal();
						} catch (NotAvailableFromExchangeException | NotYetImplementedForExchangeException
								| ExchangeException | IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
    	    	    }
				};
				altBalance.start();
				arrThreads.add(altBalance);
				
		        for (int i = 0; i < arrThreads.size(); i++) 
		        {
		            arrThreads.get(i).join(); 
		        } 
		        
				BigDecimal BuyPrice = new BigDecimal(bid+distancefrombest).setScale(8, RoundingMode.HALF_UP);;
				BigDecimal SellPrice = new BigDecimal(ask-distancefrombest).setScale(8, RoundingMode.HALF_DOWN);;
				BigDecimal BuyVolume = (balancesU.baseBalance.divide(BuyPrice,8,RoundingMode.HALF_DOWN)).multiply(new BigDecimal(0.99));
				BigDecimal SellVolume = balancesU.altBalance.multiply(new BigDecimal(0.99));
				
				if (dontrun!=true) {
					if (firstrun==true) {
						person.addOrderData("\nPlacing buy order @ " + BuyPrice);
						LimitOrder buyingorder = new LimitOrder((OrderType.BID), BuyVolume, this.pair, null, null, BuyPrice);
						prevbidorder = tradeExchange.placeLimitOrder(buyingorder);
						person.addOrderData("\nPlacing sell order @ " + SellPrice);
						LimitOrder sellingorder  = new LimitOrder((OrderType.ASK), SellVolume, this.pair, null, null, SellPrice);
						prevaskorder = tradeExchange.placeLimitOrder(sellingorder);
						prevbid = bid;
						prevask = ask;
					} else {
						if (bid!=prevbid) {
							tradeExchange.cancelOrder(prevbidorder);
							person.addOrderData("Canceled previous buy order\n");
							LimitOrder buyingorder = new LimitOrder((OrderType.BID), BuyVolume, this.pair, null, null, BuyPrice);
							person.addOrderData("\nPlacing buy order @ " + BuyPrice);
							prevbidorder = tradeExchange.placeLimitOrder(buyingorder);
						} else {
							person.addOrderData("Bid is equal to previous bid, not changing buying order\n");
							System.out.println("Bid is equal to prevbid");
						}
						if (ask!=prevask) {
							tradeExchange.cancelOrder(prevaskorder);
							person.addOrderData("Canceled previous sell order\n");
							LimitOrder sellingorder  = new LimitOrder((OrderType.ASK), SellVolume, this.pair, null, null, SellPrice);
							person.addOrderData("\nPlacing sell order @ " + SellPrice);
							prevaskorder = tradeExchange.placeLimitOrder(sellingorder);
						} else {
							person.addOrderData("Ask is equal to previous ask, not changing selling order\n");
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
	
	public static void testtheMarket() throws NotAvailableFromExchangeException, NotYetImplementedForExchangeException, ExchangeException, IOException {
		List<CurrencyPair> currencylist = Exchanges.exchangemap.get("poloniex").getExchangeSymbols();
		for (CurrencyPair curr : currencylist) {
			try {
			Ticker ticker = Exchanges.exchangemap.get("poloniex").getMarketDataService().getTicker(curr);
			BigDecimal volume = ticker.getVolume().multiply(ticker.getLast());
			BigDecimal profit = ticker.getAsk().divide(ticker.getBid(),8,RoundingMode.HALF_UP);
			if (profit.doubleValue()>1.01 && volume.doubleValue()>20) {
			System.out.println(profit + " Volume: "+ volume + " Pair: " + ticker.getCurrencyPair().toString());
			}
			} catch (ExchangeException e) {
				
			}
		}
	}
    private class balancesU {
        public BigDecimal baseBalance;
        public BigDecimal altBalance;
    }
}
