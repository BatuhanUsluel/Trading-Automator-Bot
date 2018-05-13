package application;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.knowm.xchange.Exchange;
import org.knowm.xchange.currency.Currency;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.Order.OrderType;
import org.knowm.xchange.dto.account.Wallet;
import org.knowm.xchange.dto.trade.LimitOrder;
import org.knowm.xchange.exceptions.ExchangeException;
import org.knowm.xchange.exceptions.NotAvailableFromExchangeException;
import org.knowm.xchange.exceptions.NotYetImplementedForExchangeException;
import org.knowm.xchange.service.account.AccountService;
import  java.lang.Thread;
import controllers.DashboardController;
import controllers.DashboardController.Person;

public class ArbitrageOrder implements Runnable{
	private Currency base;
	private Currency alt;
	private double minarb;
	private List<Exchange> exchanges;
	private JSONObject json;
	private boolean ordercanceled;
	private CurrencyPair pair;
	private int exchangesize;
	private Person person;
	public ArbitrageOrder(String base, String alt, String minarb, List<Exchange> exchanges, JSONObject json) throws JSONException {
		this.base= new Currency(base);
		this.alt= new Currency(alt);
		this.pair= new CurrencyPair(alt,base);
		this.minarb=(1+(Double.parseDouble(minarb)/100));
		this.exchanges=exchanges;
		this.json=json;
		this.exchangesize = exchanges.size();
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
    	SocketCommunication.out.print(json);
    	SocketCommunication.out.flush();
	    BigDecimal[] baseBalances = new BigDecimal[exchangesize];
	    BigDecimal[] altBalances = new BigDecimal[exchangesize];
		}
	
	public void cancelArbitrageOrder() {
		System.out.println("cancelPendingOrder!!");
		this.ordercanceled = true;
	}
	
	public void recievedArbitrageOrder(JSONObject message) throws JSONException, IOException, InterruptedException {
		person.addOrderData("\nRecieved prices");
		JSONObject object = message.getJSONObject("Returned");
		double highestbid = 0,lowestask = 0,highestbidvol=0,lowestaskvol=0;
		Exchange highestbidex = null;
		Exchange lowestaskex = null;
		boolean firstrun=true;
		for (Exchange exchange : exchanges) {
			JSONObject object2 = object.getJSONObject(exchange.toString());
			JSONArray bidarray = object2.getJSONArray("bid");
			double bid = bidarray.getDouble(0);
			JSONArray askarray = object2.getJSONArray("bid");
			double ask = askarray.getDouble(0);
			if (firstrun==true) {
				highestbid=bid;
				highestbidvol=bidarray.getDouble(1);
				highestbidex = exchange;
				
				lowestask=bid;
				lowestaskvol=askarray.getDouble(1);
				lowestaskex = exchange;
			} else {
				if (bid>highestbid) {
					highestbid=bid;
					highestbidvol=bidarray.getDouble(1);
					highestbidex = exchange;
				}
				if (ask<lowestask) {
					lowestask=ask;
					lowestaskvol=askarray.getDouble(1);
					lowestaskex = exchange;
				}
			}
			firstrun=false;
		}
		person.addOrderData("\nHighest Bid: " + highestbid + " with volume " + highestbidvol + " on exchange " + highestbidex.toString());
		person.addOrderData("\nLowest Ask: " + lowestask + " with volume " + lowestaskvol + " on exchange " + lowestaskex.toString());
		if ((highestbid/lowestask)>minarb) {
			person.addOrderData("\nArbitrage is " + (highestbid/lowestask) + " which is higher than the minimum " + minarb + ". Executing Trade");
			ArrayList<Thread> balanceThreads = new ArrayList<Thread>();
			final Exchange newlowestaskex = lowestaskex;
			final Exchange newhighestbidex = highestbidex;
			//Get base balance from exchange with lowest ask
	    	Thread basethread = new Thread() {
	    	    public void run() {
	    			AccountService baseaccount = newlowestaskex.getAccountService();
					try {
						Wallet basewallet = baseaccount.getAccountInfo().getWallet();
						balancesClass.baseBalance = basewallet.getBalance(base).getAvailable();
						person.addOrderData("\nBase balance on exchange " + newlowestaskex.toString() + " is " + balancesClass.baseBalance);
					} catch (IOException e) {
						e.printStackTrace();
					}
	    			
	    	    }
	    	};
	    	basethread.start();
	    	balanceThreads.add(basethread);

			//Get alt balance from exchange with highest bid
	    	Thread altthread = new Thread() {
	    	    public void run() {
	    			AccountService altaccount = newhighestbidex.getAccountService();
	    			Wallet altwallet;
					try {
						altwallet = altaccount.getAccountInfo().getWallet();
						balancesClass.altBalance = altwallet.getBalance(base).getAvailable();
						person.addOrderData("\nAlt balance on exchange " + newhighestbidex.toString() + " is " + balancesClass.altBalance);
					} catch (IOException e) {
						e.printStackTrace();
					}
	    	    }
	    	};
	    	altthread.start();
	    	balanceThreads.add(altthread);
			
	    	for (int i = 0; i < balanceThreads.size(); i++) 
	        {
	    		balanceThreads.get(i).join(); 
	        }
	    	
	    	//Volume Calculation
    		double tradeSize = Math.min(highestbidvol,lowestaskvol);
    		
	        if (tradeSize>balancesClass.altBalance.doubleValue()) {
	        	person.addOrderData("\nNot enough alt balance, reducing alt volume to: " + tradeSize);
	        	tradeSize=balancesClass.altBalance.doubleValue();
	        }
	        
	        if (balancesClass.baseBalance.doubleValue()<(tradeSize*lowestask)){
	        	tradeSize=balancesClass.baseBalance.doubleValue()/lowestask;
	        	person.addOrderData("\nNot enough base balance, reducing alt volume to: " + tradeSize);
	        }

	    	//Execute Trades
	    	LimitOrder buyfromaskorder = new LimitOrder((OrderType.BID), new BigDecimal(tradeSize), this.pair, null, null, new BigDecimal(lowestask));
	    	person.addOrderData("\n" + buyfromaskorder.toString());
			//Object buyfromask = highestbidex.getTradeService().placeLimitOrder(buyfromaskorder);
			
	    	LimitOrder selltobidorder = new LimitOrder((OrderType.ASK), new BigDecimal(tradeSize), this.pair, null, null, new BigDecimal(highestbid));
	    	person.addOrderData("\n" + selltobidorder.toString());
			//Object selltobid = lowestaskex.getTradeService().placeLimitOrder(selltobidorder);
	    	
	    	person.addOrderData("Placed orders!");
			
		} else {
			person.addOrderData("Arbitrage is " + (highestbid/lowestask) + " which is lower than the required " + minarb);
		}
	}
	
    private static class balancesClass {
        public static BigDecimal baseBalance;
        public static BigDecimal altBalance;
    }
    
}
