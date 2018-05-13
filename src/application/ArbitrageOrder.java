package application;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;
import org.knowm.xchange.Exchange;
import org.knowm.xchange.currency.Currency;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.account.Wallet;
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
	private String minarb;
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
		this.minarb=minarb;
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
					+ String.format("%-10s:%10s\n","Exchange", this.json.get("Exchanges").toString())
					+ "--------------------------------------\n") ;
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
    	SocketCommunication.out.print(json);
    	SocketCommunication.out.flush();
	    BigDecimal[] baseBalances = new BigDecimal[exchangesize];
	    BigDecimal[] altBalances = new BigDecimal[exchangesize];
	    /*
    	while(ordercanceled!=true) {
    		 for (int i=0; i<exchangesize; i++){
	    	    final int x = i;
		    	Thread exchangeThread = new Thread() {
		    	    public void run() {
		    	    	 ArrayList<Thread> specThreads = new ArrayList<Thread>();
		    			 Exchange UExchange = exchanges.get(x);
		    			 final AccountService account = UExchange.getAccountService();
		    			 	try {
								Wallet wallet = account.getAccountInfo().getWallet();
								
						    	Thread basethread = new Thread() {
						    	    public void run() {
						    	    	BigDecimal basebalance = wallet.getBalance(base).getAvailable();
										baseBalances[x] = basebalance;
						    	    }
						    	};
						    	basethread.start();
						    	specThreads.add(basethread);		
						    	Thread altthread = new Thread() {
						    	    public void run() {
										BigDecimal altbalance = wallet.getBalance(alt).getAvailable();
										
										altBalances[x] = altbalance;
						    	    }
						    	};
						    	altthread.start();
						    	specThreads.add(altthread);
						    	
						    	for (int i = 0; i < specThreads.size(); i++) 
						        {
						    		specThreads.get(i).join(); 
						        } 
							} catch (NotAvailableFromExchangeException | NotYetImplementedForExchangeException
									| ExchangeException | IOException | InterruptedException e) {
								e.printStackTrace();
							}
		    	    	}
		    		};
		    		exchangeThread.start();
    		 	}
    		}
    		*/
		}
	
	public void cancelArbitrageOrder() {
		System.out.println("cancelPendingOrder!!");
		this.ordercanceled = true;
	}
	
	public void recievedArbitrageOrder(JSONObject message) {
		
	}
}
