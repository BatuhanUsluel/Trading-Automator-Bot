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
import org.knowm.xchange.service.account.AccountService;
import org.knowm.xchange.service.marketdata.MarketDataService;
import org.knowm.xchange.service.trade.TradeService;

import controllers.DashboardController;
import controllers.DashboardController.Person;

public class MarketMaking implements Runnable {

	private JSONObject json;
	private String base;
	private String alt;
	private CurrencyPair pair;
	private double spread;
	private double MaxBal;
	private double MinBal;
	private Exchange exchange;
	private boolean ordercanceled=false;
	private double prevbid;
	private double prevask;
	private String prevbidorder;
	private String prevaskorder;
	private boolean firstrun=true;
	private double distancefrombest = 0.00000001;
	private Person person;
	private int loop;

	private AccountService accountExchange;
	private MarketDataService marketExchange;
	private TradeService tradeExchange;

	private LimitOrder prevbidorderlimit;
	private LimitOrder prevaskorderlimit;
	private Currency basecurrency;


	private Currency altcurrency;


	private String spreadstring;
	private BigDecimal prevbuyprice=new BigDecimal(0);
	private BigDecimal prevsellprice=new BigDecimal(0);
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
			/*
	    	System.out.println("testCancelOrder!");
	    	ExchangeSpecification exSpec = new ExchangeSpecification("org.knowm.xchange.poloniex.PoloniexExchange");
			exSpec.setApiKey("WNT88N8E-PMI4NUMM-XKCZA114-FSMFW9II");
			exSpec.setSecretKey("023874096f6ab514289f9cec6c739de63271403afe9b19565c5ab69119bdda49ab56dab29c07e289b3eacdd039c1a841bf5fe6758e2fe914456b88c477fc4102");
		    Exchange ex = ExchangeFactory.INSTANCE.createExchange(exSpec);
		   
		    
	    	TimeUnit.SECONDS.sleep(10);
	    	System.out.println("buy orderr!");
	    	LimitOrder buyingorder = new LimitOrder((OrderType.BID), new BigDecimal(0.1), new CurrencyPair("ETH","BTC"), null, null, new BigDecimal(0.06));
	    	System.out.println("buy orderrRRRRR!");
	    	String prevbidorder2 = ex.getTradeService().placeLimitOrder(buyingorder);
	    	System.out.println("Placed order!");
	    	System.out.println("Status: " + buyingorder.getStatus());
	    	TimeUnit.SECONDS.sleep(10);
	    	System.out.println("Status: " + buyingorder.getStatus());
	    	TimeUnit.SECONDS.sleep(20);
	    	System.out.println("Canceling order!");
	    	System.out.println("Status: " + buyingorder.getStatus());
	    	//System.out.println(ex.getTradeService().getOrder(prevbidorder2));
	    	ex.getTradeService().cancelOrder(prevbidorder2);
	    	System.out.println("Status: " + buyingorder.getStatus());
	    	TimeUnit.SECONDS.sleep(10);
	    	System.out.println("Status: " + buyingorder.getStatus());
	    	*/
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
		} catch (NotAvailableFromExchangeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NotYetImplementedForExchangeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExchangeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        try {
    		person.addOrderData("Sending price request\n");
        	System.out.println("Json: "  + json);
        	SocketCommunication.out.print(json);
        	SocketCommunication.out.flush();
		} catch (NotAvailableFromExchangeException e) {
			e.printStackTrace();
		} catch (NotYetImplementedForExchangeException e) {
			e.printStackTrace();
		} catch (ExchangeException e) {
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
			&& millisstart == (message.getLong("millisstart"))
			&& ordercanceled==false) {
				double bid1 = Double.parseDouble((message.getJSONObject("Returned").getString("Bid1")));
				double ask1 = Double.parseDouble((message.getJSONObject("Returned").getString("Ask1")));
				double ask2 = Double.parseDouble((message.getJSONObject("Returned").getString("Ask2")));
				double bid2 = Double.parseDouble((message.getJSONObject("Returned").getString("Bid2")));
				final balancesU balancesU = new balancesU();
				ArrayList<Thread> arrThreads = new ArrayList<Thread>();
				//CHANGE BALANCE:
				//GET BALANCE 4 SECONDS AFTER THE PRICE COMES AND SAVE IT IN THE BALANCE OBJECT WITH THE LONG TIME IN MILIS. THEN GET THE BALANCE FROM THERE WHILE TRADING SO THAT YOU HAVE NEW PRICE WITHOUT HAVING TO WAIT FOR IT ONCE AGAIN.
				Thread baseBalance = new Thread() {
    	    	    public void run() {
    	    	    	try {
    	    	    		TimeUnit.MILLISECONDS.sleep(50);
							balancesU.baseBalance = accountExchange.getAccountInfo().getWallet().getBalance(basecurrency).getTotal();
						} catch (NotAvailableFromExchangeException | NotYetImplementedForExchangeException
								| ExchangeException | IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (InterruptedException e) {
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
		        person.addOrderData("\nFirst run== " + firstrun);
				if (firstrun==true) {
					if((ask1-bid1)>spread) {
						BigDecimal BuyPrice = new BigDecimal(bid1+distancefrombest).setScale(8, RoundingMode.HALF_UP);
						BigDecimal SellPrice = new BigDecimal(ask1-distancefrombest).setScale(8, RoundingMode.HALF_DOWN);
						BigDecimal BuyVolume = (balancesU.baseBalance.divide(BuyPrice,8,RoundingMode.HALF_DOWN));
						BigDecimal SellVolume = balancesU.altBalance.setScale(8, RoundingMode.HALF_DOWN);
						person.addOrderData("\nFirst run");
						prevbuyprice = BuyPrice;
						prevsellprice = SellPrice;
						if (BuyVolume.doubleValue()>0.0001) {
							person.addOrderData("\nBid1: " + bid1 + " Bid2: " + bid2 + "\nPlacing buy order @ " + BuyPrice + "with volume: " + BuyVolume);
							prevbidorderlimit = new LimitOrder((OrderType.BID), BuyVolume, this.pair, null, null, BuyPrice);
							prevbidorder = tradeExchange.placeLimitOrder(prevbidorderlimit);
						} else {
							person.addOrderData("\nOrder size for buy too low");
						}
						if (SellVolume.doubleValue()>0.0001) {
							person.addOrderData("\nAsk1: " + ask1 + " Ask2: " + ask2 + "\nPlacing sell order @ " + SellPrice  + "with volume: " + SellVolume);
							prevaskorderlimit  = new LimitOrder((OrderType.ASK), SellVolume, this.pair, null, null, SellPrice);
							prevaskorder = tradeExchange.placeLimitOrder(prevaskorderlimit);
						} else {
							person.addOrderData("\nOrder size for sell too low");
						}
						firstrun=false;
						person.addOrderData("\nFirst run= " + firstrun);
					} else {
						//System.out.println("Minspread: " + spread + " is HIGHER than Current spread: " + (ask1-bid1));
						//System.out.println((ask1-bid1)<spread);
						person.addOrderData("\nSpread is: " + (ask1-bid1) + " which is lower than minimum spread: " + spread);
					}
				} else {
					if((ask1-bid1)<spread) {
						person.addOrderData("\nSpread is: " + (ask1-bid1) + " which is lower than minimum spread: " + spread);
						tradeExchange.cancelOrder(prevbidorder);
						tradeExchange.cancelOrder(prevaskorder);
						firstrun=true;
					} else {
						boolean wait = false;
						if (bid2+distancefrombest<(prevbuyprice.doubleValue()) || (bid2+distancefrombest>(prevbuyprice.doubleValue()))) {
							try {
								tradeExchange.cancelOrder(prevbidorder);
							} catch (Exception e) {
								System.out.println("Exception!");
							}
							wait=true;
						}
						if((ask2-distancefrombest>(prevsellprice.doubleValue())) || (ask2-distancefrombest<(prevsellprice.doubleValue()))) {
							try {
								tradeExchange.cancelOrder(prevaskorder);
							} catch (Exception e) {
								System.out.println("Exception!");
							}
							wait=true;
						}
						
						if (wait==true) {
							person.addOrderData("\nWaiting to make sure orders are canceled");
							TimeUnit.MILLISECONDS.sleep(2000);
						}
						
						if (bid2+distancefrombest<(prevbuyprice.doubleValue())) {
							person.addOrderData("\nBuy order price is too high, canceling and placing new order");
							BigDecimal BuyPrice = new BigDecimal(bid2+distancefrombest).setScale(8, RoundingMode.HALF_UP);
							BigDecimal BuyVolume = (balancesU.baseBalance.divide(BuyPrice,8,RoundingMode.HALF_DOWN)).multiply(new BigDecimal(0.99));
							prevbuyprice = BuyPrice;
							if (BuyVolume.doubleValue()>0.0001) {
								prevbidorderlimit = new LimitOrder((OrderType.BID), BuyVolume, this.pair, null, null, BuyPrice);
								person.addOrderData("Placed buy order @ " + BuyPrice + "for volume: " + BuyVolume);
								prevbidorder = tradeExchange.placeLimitOrder(prevbidorderlimit);
							} else {
								person.addOrderData("Order size for buy too low");
							}
						} else if (bid2+distancefrombest>(prevbuyprice.doubleValue())) {
							person.addOrderData("\nBuy order price is too low, canceling and placing new order");
							BigDecimal BuyPrice = new BigDecimal(bid1+distancefrombest).setScale(8, RoundingMode.HALF_UP);
							BigDecimal BuyVolume = (balancesU.baseBalance.divide(BuyPrice,8,RoundingMode.HALF_DOWN)).multiply(new BigDecimal(0.99));
							prevbuyprice = BuyPrice;
							if (BuyVolume.doubleValue()>0.0001) {
								prevbidorderlimit = new LimitOrder((OrderType.BID), BuyVolume, this.pair, null, null, new BigDecimal(bid1+distancefrombest));
								person.addOrderData("\nPlaced buy order @ " + BuyPrice + "for volume: " + BuyVolume);
								prevbidorder = tradeExchange.placeLimitOrder(prevbidorderlimit);
							} else {
								person.addOrderData("Order size for buy too low");
							}
						} else {
							person.addOrderData("\nNot changing buy order");
						}
						
						if (ask2-distancefrombest>(prevsellprice.doubleValue())) {
							person.addOrderData("\nSell order price is too low, canceling and placing new order");
							BigDecimal SellPrice = new BigDecimal(ask2-distancefrombest).setScale(8, RoundingMode.HALF_DOWN);
							BigDecimal SellVolume = balancesU.altBalance.multiply(new BigDecimal(0.99));
							prevsellprice = SellPrice;
							if (SellVolume.doubleValue()>0.0001) {
								prevaskorderlimit = new LimitOrder((OrderType.ASK), SellVolume, this.pair, null, null, SellPrice);
								person.addOrderData("\nPlaced sell order @ " + SellPrice + "for volume: " + SellVolume);
								prevaskorder = tradeExchange.placeLimitOrder(prevaskorderlimit);
							} else {
								person.addOrderData("Order size for sell too low");
							}
						} else if (ask2-distancefrombest<(prevsellprice.doubleValue())) {
							person.addOrderData("\nSell order price is too high, canceling and placing new order");
							BigDecimal SellPrice = new BigDecimal(ask1-distancefrombest).setScale(8, RoundingMode.HALF_DOWN);
							BigDecimal SellVolume = balancesU.altBalance.multiply(new BigDecimal(0.99));
							prevsellprice = SellPrice;
							if (SellVolume.doubleValue()>0.0001) {
								prevaskorderlimit = new LimitOrder((OrderType.ASK), SellVolume, this.pair, null, null, SellPrice);
								person.addOrderData("\nPlaced sell order @ " + SellPrice + "for volume: " + SellVolume);
								prevaskorder = tradeExchange.placeLimitOrder(prevaskorderlimit);
							} else {
								person.addOrderData("Order size for sell too low");
							}
						} else {
							person.addOrderData("\nNot changing sell order");
						}
					}
				}
			}
		}
	
	
	public static void testtheMarket() throws NotAvailableFromExchangeException, NotYetImplementedForExchangeException, ExchangeException, IOException {
		List<CurrencyPair> currencylist = Exchanges.exchangemap.get("poloniex").getExchangeSymbols();
		for (CurrencyPair curr : currencylist) {
			try {
			Ticker ticker = Exchanges.exchangemap.get("poloniex").getMarketDataService().getTicker(curr);
			BigDecimal volume = ticker.getVolume().multiply(ticker.getLast());
			BigDecimal profit = ticker.getAsk().divide(ticker.getBid(),8,RoundingMode.HALF_UP);
			if (profit.doubleValue()>1.007 && volume.doubleValue()>20 && ticker.getCurrencyPair().counter.toString().equals("BTC")) {
				System.out.println(profit + " Volume: "+ volume + " Pair: " + ticker.getCurrencyPair().toString());
			}
			} catch (ExchangeException e) {
				
			}
		}
	}
    private class balancesU {
    	public java.lang.Long time;
        public BigDecimal baseBalance;
        public BigDecimal altBalance;
    }
    
    public static void testCancelOrder() throws NotAvailableFromExchangeException, NotYetImplementedForExchangeException, ExchangeException, IOException, InterruptedException {
    	System.out.println("testCancelOrder!");
    	TradeService exchange = Exchanges.exchangemap.get("poloniex").getTradeService();
    	TimeUnit.SECONDS.sleep(10);
    	LimitOrder buyingorder = new LimitOrder((OrderType.BID), new BigDecimal(0.1), new CurrencyPair("ETH","BTC"), null, null, new BigDecimal(0.06));
    	String prevbidorder2 = exchange.placeLimitOrder(buyingorder);
    	System.out.println("Placed order!");
    	System.out.println("Status: " + buyingorder.getStatus());
    	TimeUnit.SECONDS.sleep(10);
    	System.out.println("Status: " + buyingorder.getStatus());
    	TimeUnit.SECONDS.sleep(20);
    	System.out.println("Canceling order!");
    	System.out.println("Status: " + buyingorder.getStatus());
    	exchange.cancelOrder(prevbidorder2);
    	System.out.println("Status: " + buyingorder.getStatus());
    }
    
	public void stopOrder() {
		System.out.println("cancel Market Making order!!!");
		person.addOrderData("\nMarket Making has been manually canceled from dashboard.\n-------------------------------------------\n Stopping Market Making.");
		this.ordercanceled = true;
		try {
			tradeExchange.cancelOrder(prevbidorder);
			tradeExchange.cancelOrder(prevaskorder);
		} catch (NotAvailableFromExchangeException | NotYetImplementedForExchangeException | ExchangeException
				| IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		JSONObject jsoncancel = this.json;
		try {
			jsoncancel.put("cancel","True");
		} catch (JSONException e) {
			e.printStackTrace();
		}
    	SocketCommunication.out.print(jsoncancel);
    	SocketCommunication.out.flush();
	}
	
}
