package application;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.knowm.xchange.Exchange;
import org.knowm.xchange.currency.Currency;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.Order.OrderType;
import org.knowm.xchange.dto.account.Wallet;
import org.knowm.xchange.dto.marketdata.Ticker;
import org.knowm.xchange.dto.trade.LimitOrder;
import org.knowm.xchange.dto.trade.MarketOrder;
import org.knowm.xchange.service.account.AccountService;

import controllers.DashboardController;
import controllers.DashboardController.Person;

public class ArbitrageOrder implements Runnable{
	private Currency base;
	private Currency alt;
	private double minarb;
	private List<Exchange> exchanges;
	private JSONObject json;
	private boolean ordercanceled=false;
	private CurrencyPair pair;
	private int exchangesize;
	private Person person;
	private String[] exchangestotal;
	public ArbitrageOrder(String base, String alt, String minarb, List<Exchange> exchanges, JSONObject json, String[] exchangestotal) throws JSONException {
		this.base= new Currency(base);
		this.alt= new Currency(alt);
		this.pair= new CurrencyPair(alt,base);
		this.minarb=(1+(Double.parseDouble(minarb)/100));
		this.exchanges=exchanges;
		this.json=json;
		this.exchangesize = exchanges.size();
		this.exchangestotal = exchangestotal;
	}
	   
	@Override
	public void run() {
		DashboardController dash = new DashboardController();
    	try {
			person = dash.newOrder(json);
	
	    	person.addOrderData("Starting Arbitrage\n"
					+ String.format("%-10s:%10s\n","Base", base)
					+ String.format("%-10s:%10s\n","Alt", alt)
					+ String.format("%-10s:%10s\n","Min Arbitrage", minarb)
					+ String.format("%-10s:%10s\n","Exchange", String.join(",",(CharSequence[]) this.json.get("Exchanges")))
					+ "--------------------------------------\n") ;
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
    	while (ordercanceled!=true) {

    		try {
    		LinkedList<bidClass> bidClasses= new LinkedList<bidClass>();
    		LinkedList<askClass> askClasses= new LinkedList<askClass>();
    		
    		CountDownLatch doneSignal = new CountDownLatch(exchanges.size()*3);
    		List<Future<?>> tasks = new ArrayList<Future<?>>();
    		ExecutorService executor = Executors.newFixedThreadPool(exchanges.size()*3);    		
    		
    		ArrayList<Thread> threads = new ArrayList<Thread>();
    		for (Exchange exchange : exchanges) {
    			bidClass bid = new bidClass();
    			askClass ask = new askClass();
    			Thread tickerthread = new Thread() {
    	    	    public void run() {
					Ticker ticker;
					try {
						ticker = exchange.getMarketDataService().getTicker(pair);
						
						bid.bid=ticker.getBid().doubleValue();
						bid.bidvolume=ticker.getBidSize().doubleValue();
						bid.exchange=exchange;
						
						
						ask.ask=ticker.getAsk().doubleValue();
						ask.askvolume=ticker.getAskSize().doubleValue();
						ask.exchange=exchange;
						
						bidClasses.add(bid);
						askClasses.add(ask);
						doneSignal.countDown();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
    	    	    }
    			};
    			tasks.add(executor.submit(tickerthread));
    			
    			Thread basebalances = new Thread() {
    	    	    public void run() {
    	    	    	AccountService baseaccount = exchange.getAccountService();
						try {
							Wallet basewallet = baseaccount.getAccountInfo().getWallet();
							double basebalance =  basewallet.getBalance(base).getAvailable().doubleValue();
							bid.basebalance = basebalance;
							ask.basebalance = basebalance;
							doneSignal.countDown();
						} catch (IOException e) {
							e.printStackTrace();
						}
    	    	    }
    			};
    			tasks.add(executor.submit(basebalances));
    			
    			Thread altbalances = new Thread() {
    	    	    public void run() {
    	    	    	AccountService baseaccount = exchange.getAccountService();
						try {
							Wallet basewallet = baseaccount.getAccountInfo().getWallet();
							double basebalance =  basewallet.getBalance(alt).getAvailable().doubleValue();
							bid.altbalance = basebalance;
							ask.altbalance = basebalance;
							doneSignal.countDown();
						} catch (IOException e) {
							e.printStackTrace();
						}
    	    	    }
    			};
    			tasks.add(executor.submit(altbalances));
    		}
    		
    		doneSignal.await(5, TimeUnit.SECONDS);
    		if (doneSignal.getCount() > 0) {
    		    for (Future<?> fut : tasks) {
	    		    if (!fut.isDone()) {
	    		        System.out.println("Task " + fut + " has not finshed!");
	    		        fut.cancel(true);
	    		    }
    		    }
    		}
	    	
    		for (bidClass bid : bidClasses) {
    			System.out.println(bid.exchange.toString() + ": " + bid.bid + " , " + bid.bidvolume);
    		}
    		System.out.println("ask");
    		for (askClass ask : askClasses) {
    			System.out.println(ask.exchange.toString() + ": " + ask.ask + " , " + ask.askvolume);
    		}
    		System.out.println("order");
    		Collections.sort(bidClasses);
    		Collections.sort(askClasses);
    		for (bidClass bid : bidClasses) {
    			System.out.println(bid.exchange.toString() + ": " + bid.bid + " , " + bid.bidvolume);
    		}
    		for (askClass ask : askClasses) {
    			System.out.println(ask.exchange.toString() + ": " + ask.ask + " , " + ask.askvolume);
    		}
    		LinkedList<exchangesAndOrders> exchangesandorders= new LinkedList<exchangesAndOrders>();
    		
    		while(bidClasses.getFirst().bid*minarb>askClasses.getFirst().ask) {
    			bidClass bidc = bidClasses.getFirst();
    			askClass askc = askClasses.getFirst();
    			double bidprice = bidc.bid;
    			double bidvolume = bidc.bidvolume;
    			double askprice = askc.ask;
    			double askvolume = askc.askvolume;
    			Exchange bidexchange = bidc.exchange;
    			Exchange askexchange = askc.exchange;
    			double bidexchangealtbal = bidc.altbalance;
    			double askexchangebasebal = askc.basebalance;
    	    	
    	    	//Volume Calculation
    			double tradeSize = Double.min(askvolume, bidvolume);
    			boolean reducedbid = false;
        		boolean reducedask = false;
    	        if (tradeSize>bidexchangealtbal) {
    	        	person.addOrderData("\nNot enough alt balance, reducing alt volume to: " + tradeSize);
    	        	tradeSize=bidexchangealtbal;
    	        	reducedbid=true;
    	        }
    	        
    	        if (askexchangebasebal<(tradeSize*askprice)){
    	        	tradeSize=askexchangebasebal/askprice;
    	        	person.addOrderData("\nNot enough base balance, reducing alt volume to: " + tradeSize);
    	        	reducedask=true;
    	        }
    	        
    	        MarketOrder buyfromaskorder = new MarketOrder((OrderType.BID), new BigDecimal(tradeSize), this.pair);
    	        MarketOrder selltobidorder = new MarketOrder((OrderType.ASK), new BigDecimal(tradeSize), this.pair);
    	        exchangesAndOrders BidOrder = new exchangesAndOrders(askexchange, buyfromaskorder);
    	        exchangesAndOrders AskOrder = new exchangesAndOrders(bidexchange, selltobidorder);
    	        exchangesandorders.add(BidOrder);
    	        exchangesandorders.add(AskOrder);
    	        bidc.bidvolume = bidc.bidvolume - tradeSize;
    	        askc.askvolume = askc.askvolume - tradeSize;
    	        if (bidc.bidvolume <= 0.00000001) {
    	        	bidClasses.removeFirst();
    	        } else if (reducedbid) {
    	        	bidClasses.removeFirst();
    	        }
    	        if (askc.askvolume <= 0.00000001) {
    	        	askClasses.removeFirst();
    	        } else if (reducedask) {
    	        	askClasses.removeFirst();
    	        }
    		}
			
			for (exchangesAndOrders order : exchangesandorders) {
				String returned = order.placeOrder();
				System.out.println(returned);
			}
    		
    		} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    		try {
				TimeUnit.SECONDS.sleep(10);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
	}
    
	public void stopOrder() {
		person.addOrderData("\nArbitrage Order has been manually canceled from dashboard.\n-------------------------------------------\n Stopping Arbitrage Bot.");
		this.ordercanceled = true;
	}
	
	public class bidClass implements Comparable<bidClass>{
		 public double altbalance;
		 public double basebalance;
		 public Exchange exchange;
		 public double bid;
		 public double bidvolume;
		@Override
		public int compareTo(bidClass bidclass) {
			return Double.compare(bidclass.bid,this.bid);
		}

	}
	public class askClass implements Comparable<askClass>{
		 public double basebalance;
		 public double altbalance;
		 public Exchange exchange;
		 public double ask;
		 public double askvolume;
			@Override
			public int compareTo(askClass askclass) {
				return Double.compare(this.ask,askclass.ask);
			}

	}
	
	public class exchangesAndOrders {
		public Exchange exchange;
		public MarketOrder marketorder;
		public exchangesAndOrders(Exchange exchange, MarketOrder marketorder) {
			this.exchange=exchange;
			this.marketorder=marketorder;
		}
		public String placeOrder() throws IOException {
			String orderreturn = exchange.getTradeService().placeMarketOrder(marketorder);
			return orderreturn;
		}
	}
	public static double round(double value, int places) {
	    if (places < 0) throw new IllegalArgumentException();

	    BigDecimal bd = new BigDecimal(value);
	    bd = bd.setScale(places, RoundingMode.HALF_DOWN);
	    return bd.doubleValue();
	}
}
