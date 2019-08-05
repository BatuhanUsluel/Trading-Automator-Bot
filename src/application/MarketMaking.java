package application;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.json.JSONException;
import org.json.JSONObject;
import org.knowm.xchange.Exchange;
import org.knowm.xchange.binance.service.BinanceCancelOrderParams;
import org.knowm.xchange.currency.Currency;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.Order.OrderType;
import org.knowm.xchange.dto.marketdata.Ticker;
import org.knowm.xchange.dto.trade.LimitOrder;
import org.knowm.xchange.exceptions.ExchangeException;
import org.knowm.xchange.exceptions.NotAvailableFromExchangeException;
import org.knowm.xchange.exceptions.NotYetImplementedForExchangeException;
import org.knowm.xchange.service.account.AccountService;
import org.knowm.xchange.service.marketdata.MarketDataService;
import org.knowm.xchange.service.trade.TradeService;
import org.knowm.xchange.service.trade.params.CancelOrderParams;

import controllers.DashboardController;
import controllers.DashboardController.Person;
import si.mazi.rescu.HttpStatusIOException;

public class MarketMaking implements Runnable {

	private JSONObject json;
	private String base;
	private String alt;
	private CurrencyPair pair;
	private double spread;
	private BigDecimal maxaltbalance;
	private BigDecimal minaltbalance;
	private Exchange exchange;
	private boolean ordercanceled=false;
	private String prevbidorder;
	private String prevaskorder;
	private double distancefrombest = 0.000001;
	private Person person;

	private AccountService accountExchange;
	private MarketDataService marketExchange;
	private TradeService tradeExchange;

	private LimitOrder prevaskorderlimit;
	private LimitOrder prevbidorderlimit;
	private Currency basecurrency;


	private Currency altcurrency;


	private String spreadstring;
	private String MaxBalString;
    private BigDecimal baseBalance;
    private BigDecimal altBalance;

	private String MinBalString;
	private Ticker ticker = null;

	private String exchangeString;
	
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
			this.spread = Double.parseDouble(spreadstring);
			this.maxaltbalance = new BigDecimal(MaxBalString);
			this.minaltbalance = new BigDecimal(MinBalString);
			this.exchangeString = this.json.getString("Exchanges");
			this.exchange = Exchanges.exchangemap.get(exchangeString);
			System.out.println(exchange);
			System.out.println(exchange.getExchangeSymbols());
			this.accountExchange = exchange.getAccountService();
			this.marketExchange = exchange.getMarketDataService();
			this.tradeExchange = exchange.getTradeService();
			//this.loop = Integer.parseInt(this.json.getString("loop"));
			person.addOrderData("Starting Market Making\n"
					+ String.format("%-10s:%10s\n","Base", base)
					+ String.format("%-10s:%10s\n","Alt", alt)
					+ String.format("%-10s:%10s\n","Min Spread", spread)
					+ String.format("%-10s:%10s\n","Exchange", this.json.getString("Exchanges"))
					+ String.format("%-10s:%10s\n","Max Balance", maxaltbalance)
					+ String.format("%-10s:%10s\n","Min Balance", minaltbalance) + "--------------------------------------\n") ;
			BigDecimal new_v = new BigDecimal(0.002592);
			prevaskorderlimit = new LimitOrder((OrderType.BID), new BigDecimal(1), this.pair, null, null, new_v.setScale(6, RoundingMode.HALF_DOWN));
			placeorder(prevaskorderlimit, false);
			TimeUnit.SECONDS.sleep(2);
			System.out.println("Placed order, id: " + prevbidorder);
			TimeUnit.SECONDS.sleep(5);
			System.out.println("ID: " + prevbidorder);
			CancelOrder.cancelOrder(prevbidorder, pair, exchangeString, tradeExchange);
			System.out.println("canceled");
//			testtheMarket();
			while (ordercanceled!=true) {
//				trade();
				TimeUnit.SECONDS.sleep(10);
			}

		} catch (NotAvailableFromExchangeException e) {
			e.printStackTrace();
		} catch (NotYetImplementedForExchangeException e) {
			e.printStackTrace();
		} catch (ExchangeException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	double prevaskprice = 0;
	double prevaskvolume = 0;
	double prevbidprice = 0;
	double prevbidvolume = 0;
	public void trade() throws IOException, InterruptedException {
		CountDownLatch doneSignal = new CountDownLatch(3);
		List<Future<?>> tasks = new ArrayList<Future<?>>();
		ExecutorService executor = Executors.newFixedThreadPool(3); 
		Thread baseBalanceThread = new Thread() {
    	    public void run() {
    	    	try {
					baseBalance = accountExchange.getAccountInfo().getWallet().getBalance(basecurrency).getTotal();
					doneSignal.countDown();
				} catch (NotAvailableFromExchangeException | NotYetImplementedForExchangeException
						| ExchangeException | IOException e) {
					e.printStackTrace();
				}
    	    }
		};
		tasks.add(executor.submit(baseBalanceThread));
		
		Thread altBalanceThread = new Thread() {
    	    public void run() {
    	    	try {
    	    		TimeUnit.MILLISECONDS.sleep(2);
					altBalance = accountExchange.getAccountInfo().getWallet().getBalance(altcurrency).getTotal();
					doneSignal.countDown();
				} catch (NotAvailableFromExchangeException | NotYetImplementedForExchangeException
						| ExchangeException | IOException e) {
					e.printStackTrace();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
    	    }
		};
		tasks.add(executor.submit(altBalanceThread));
		
		Thread tickerThread = new Thread() {
    	    public void run() {
    	    	try {
    	    		TimeUnit.MILLISECONDS.sleep(4);
					ticker = marketExchange.getTicker(pair);
					doneSignal.countDown();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
    	    }
		};
		tasks.add(executor.submit(tickerThread));
		
		try {
			doneSignal.await(5, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (doneSignal.getCount() > 0) {
		    for (Future<?> fut : tasks) {
    		    if (!fut.isDone()) {
    		        person.addOrderData("Did not recieve values from exchange in under 5 seconds, quitting this loop");
    		        fut.cancel(true);
    		        return;
    		    }
		    }
		}
		System.out.println("ticker: " + ticker.toString());
		double askprice = ticker.getAsk().doubleValue();
		double bidprice = ticker.getBid().doubleValue();
		double askvolume;
		double bidvolume;
		if (ticker.getAskSize()==null || ticker.getBidSize()==null) {
			System.out.println("order sizes are null");
			askvolume = 0;
			bidvolume = 0;
		} else {
			askvolume = ticker.getAskSize().doubleValue();
			bidvolume = ticker.getBidSize().doubleValue();
		}


		double currentspread = askprice/bidprice;
		if(currentspread<spread) {
			person.addOrderData("\nSpread is: " + currentspread + " which is lower than minimum spread: " + spread);
			cancelorders(true,true);
			return;
		} else {
			person.addOrderData("\nSpread is: " + askprice/bidprice);
		}
		

		if (askprice==prevaskprice) {
			person.addOrderData("\nNot changing sell order");
		} else {
			//Different order, place new trade, cancel previous
			cancelorders(false,true);
			TimeUnit.SECONDS.sleep((long) 0.5);
			BigDecimal SellPrice = new BigDecimal(askprice-distancefrombest).setScale(6, RoundingMode.HALF_DOWN);
			BigDecimal SellVolume = altBalance.setScale(8, RoundingMode.HALF_DOWN).subtract(minaltbalance);
			if (SellVolume.doubleValue()>0.0001) {
				SellVolume = SellVolume.setScale(2, RoundingMode.FLOOR);
				prevaskorderlimit = new LimitOrder((OrderType.ASK), SellVolume, this.pair, null, null, SellPrice);
				prevaskprice = SellPrice.doubleValue();
				prevaskvolume = SellVolume.doubleValue();
				placeorder(prevaskorderlimit, true);
				person.addOrderData("\nPlaced sell order @ " + SellPrice + " for volume: " + SellVolume);
			} else {
				person.addOrderData("\nOrder size for sell too low");
			}
		}
		
		System.out.println("Bid Price: " + bidprice);
		System.out.println("My bid price: " + prevbidprice);		
		if (bidprice==prevbidprice) {
			person.addOrderData("\nNot changing buy order");
		} else {
			//Different order, place new trade, cancel previous
			cancelorders(true,false);
			TimeUnit.SECONDS.sleep((long) 0.5);
			BigDecimal BuyPrice = new BigDecimal(bidprice+distancefrombest).setScale(6, RoundingMode.HALF_UP);
			BigDecimal BuyVolume = maxaltbalance.subtract(altBalance);
			if (BuyVolume.doubleValue()>baseBalance.divide(BuyPrice,8,RoundingMode.HALF_DOWN).doubleValue()) {
				BuyVolume = baseBalance.divide(BuyPrice,8,RoundingMode.HALF_DOWN).multiply(new BigDecimal(0.99));
			}
			if (BuyVolume.doubleValue()>0.0001) {
				BuyVolume = BuyVolume.setScale(2, RoundingMode.FLOOR);
				prevbidorderlimit = new LimitOrder((OrderType.BID), BuyVolume, this.pair, null, null, BuyPrice);
				prevbidprice = BuyPrice.doubleValue();
				prevbidvolume = BuyVolume.doubleValue();
				placeorder(prevbidorderlimit, false);
				person.addOrderData("\nPlaced buy order @ " + BuyPrice + " for volume: " + BuyVolume);
			} else {
				person.addOrderData("\nOrder size for buy too low");
			}
		}
	}	
	
	private void placeorder(LimitOrder placeorder, boolean sell) {
		Thread placeOrderThread = new Thread() {
    	    public void run() {
				try {
					if (sell) {
						prevaskorder = tradeExchange.placeLimitOrder(placeorder);
					} else {
						TimeUnit.MILLISECONDS.sleep(2);
						prevbidorder = tradeExchange.placeLimitOrder(placeorder);
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
    	    }
		};
		placeOrderThread.start();
	}

	private void cancelorders(boolean bid, boolean ask) {
		if (bid) {
			prevbidprice = 0;
			Thread cancelThreadbid = new Thread() {
	    	    public void run() {
					try {
						if (prevbidorder!=null) {
							System.out.println("Canceling bid: " + prevbidorder);
							CancelOrderParams t = new BinanceCancelOrderParams(pair, prevbidorder);
							System.out.println(tradeExchange.cancelOrder(t));
						} else {
							System.out.println("No prev bid order to cancel");
						}
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (Exception e) {
						
					}
	    	    }
			};
			cancelThreadbid.start();
		}
		if (ask) {
			prevaskprice = 0;
			Thread cancelThreadask = new Thread() {
	    	    public void run() {
					try {
						if (prevaskorder!=null) {
							System.out.println("Canceling ask: " + prevaskorder);
							CancelOrderParams t = new BinanceCancelOrderParams(pair, prevaskorder);
							System.out.println(tradeExchange.cancelOrder(t));
						} else {
							System.out.println("No prev ask order to cancel");
						}
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (Exception e) {
						
					}
	    	    }
			};
			cancelThreadask.start();
		}		
	}

	public static void testtheMarket() throws NotAvailableFromExchangeException, NotYetImplementedForExchangeException, ExchangeException, IOException {
		List<CurrencyPair> currencylist = Exchanges.exchangemap.get("binance").getExchangeSymbols();
		for (CurrencyPair curr : currencylist) {
			try {
				TimeUnit.MILLISECONDS.sleep(100);
			Ticker ticker = Exchanges.exchangemap.get("binance").getMarketDataService().getTicker(curr);
			BigDecimal volume = ticker.getVolume().multiply(ticker.getLast());
			BigDecimal profit = ticker.getAsk().divide(ticker.getBid(),8,RoundingMode.HALF_UP);
			if (profit.doubleValue()>1.005 && volume.doubleValue()>10 && ticker.getCurrencyPair().counter.toString().equals("BTC")) {
				System.out.println(profit + " Volume: "+ volume + " Pair: " + ticker.getCurrencyPair().toString());
			}
			} catch (ExchangeException e) {
				
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (HttpStatusIOException e) {
				System.out.println("test");
			}
		}
	}
    
	public void stopOrder() {
		System.out.println("cancel Market Making order!!!");
		person.addOrderData("\nMarket Making has been manually canceled from dashboard.\n-------------------------------------------\n Stopping Market Making.");
		this.ordercanceled = true;
		cancelorders(true,true);
	}	
}