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
	private boolean gotbalance;
	private boolean gotBasebalance;
	private boolean gotAltbalance;
	
	public static HashMap<java.lang.Long, BigDecimal> BaseBalance = new HashMap<java.lang.Long, BigDecimal>();
	public static HashMap<java.lang.Long, BigDecimal> AltBalance = new HashMap<java.lang.Long, BigDecimal>();
	public MarketMaking(JSONObject json) throws JSONException {
		this.json = json;
	}
	
   public void gotBaseBalance() {
	     this.gotBasebalance=true;
	   }
	   
   public void gotAltBalance() {
	     this.gotAltbalance=true;
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
					+ String.format("%-10s:%10s\n","Base", base)
					+ String.format("%-10s:%10s\n","Alt", alt)
					+ String.format("%-10s:%10s\n","Min Spread", spread)
					+ String.format("%-10s:%10s\n","Exchange", this.json.getString("Exchanges"))
					+ String.format("%-10s:%10s\n","Max Balance", MaxBal)
					+ String.format("%-10s:%10s\n","Min Balance", MinBal) + "--------------------------------------\n") ;
			
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (NotAvailableFromExchangeException e) {
			e.printStackTrace();
		} catch (NotYetImplementedForExchangeException e) {
			e.printStackTrace();
		} catch (ExchangeException e) {
			e.printStackTrace();
		}
        try {
    		person.addOrderData("Sending price request\n");
        	System.out.println("Json: "  + json);
        	SocketCommunication.out.print(json);
        	SocketCommunication.out.flush();
			Thread baseBalance = new Thread() {
	    	    public void run() {
	    	    	try {
						balancesU.baseBalance = accountExchange.getAccountInfo().getWallet().getBalance(basecurrency).getTotal();
						gotBaseBalance();
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
	    	    		TimeUnit.MILLISECONDS.sleep(5);
						balancesU.altBalance = accountExchange.getAccountInfo().getWallet().getBalance(altcurrency).getTotal();
						gotAltBalance();
					} catch (NotAvailableFromExchangeException | NotYetImplementedForExchangeException
							| ExchangeException | IOException e) {
						e.printStackTrace();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
	    	    }
			};
			altBalance.start();

		} catch (NotAvailableFromExchangeException e) {
			e.printStackTrace();
		} catch (NotYetImplementedForExchangeException e) {
			e.printStackTrace();
		} catch (ExchangeException e) {
			e.printStackTrace();
		}
	}
	
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
				
				while(gotBasebalance!=true && gotAltbalance!=true) {
					TimeUnit.MILLISECONDS.sleep(10);
				}
				gotBasebalance=false;
				gotAltbalance=false;
				
				Thread baseBalance = new Thread() {
		    	    public void run() {
		    	    	try {
		    	    		TimeUnit.SECONDS.sleep(4);
							balancesU.baseBalance = accountExchange.getAccountInfo().getWallet().getBalance(basecurrency).getTotal();
							gotBaseBalance();
						} catch (NotAvailableFromExchangeException | NotYetImplementedForExchangeException
								| ExchangeException | IOException e) {
							e.printStackTrace();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
		    	    }
				};
				baseBalance.start();
				
				Thread altBalance = new Thread() {
		    	    public void run() {
		    	    	try {
		    	    		TimeUnit.MILLISECONDS.sleep(4005);
							balancesU.altBalance = accountExchange.getAccountInfo().getWallet().getBalance(altcurrency).getTotal();
							gotAltBalance();
						} catch (NotAvailableFromExchangeException | NotYetImplementedForExchangeException
								| ExchangeException | IOException e) {
							e.printStackTrace();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
		    	    }
				};
				altBalance.start();
				
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
							//prevbidorder = tradeExchange.placeLimitOrder(prevbidorderlimit);
						} else {
							person.addOrderData("\nOrder size for buy too low");
						}
						if (SellVolume.doubleValue()>0.0001) {
							person.addOrderData("\nAsk1: " + ask1 + " Ask2: " + ask2 + "\nPlacing sell order @ " + SellPrice  + "with volume: " + SellVolume);
							prevaskorderlimit  = new LimitOrder((OrderType.ASK), SellVolume, this.pair, null, null, SellPrice);
							//prevaskorder = tradeExchange.placeLimitOrder(prevaskorderlimit);
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
								//prevbidorder = tradeExchange.placeLimitOrder(prevbidorderlimit);
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
								//prevbidorder = tradeExchange.placeLimitOrder(prevbidorderlimit);
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
								//prevaskorder = tradeExchange.placeLimitOrder(prevaskorderlimit);
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
								//prevaskorder = tradeExchange.placeLimitOrder(prevaskorderlimit);
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
    private static class balancesU {
    	public java.lang.Long time;
        public static BigDecimal baseBalance;
        public static BigDecimal altBalance;
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