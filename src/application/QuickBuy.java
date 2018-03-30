package application;
import java.math.BigDecimal;
import java.math.RoundingMode;
import org.json.JSONException;
import org.json.JSONObject;
import org.knowm.xchange.Exchange;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.Order.OrderType;
import org.knowm.xchange.dto.trade.LimitOrder;
import java.lang.Long;
import java.util.ArrayList;
import java.util.List;

import javax.json.Json;
import javax.json.JsonObject;

public class QuickBuy {

	private static List<JsonObject> myList = new ArrayList<>();
	/*
	public static void main(String[] args) throws NotAvailableFromExchangeException, NotYetImplementedForExchangeException, ExchangeException, IOException, JSONException, InterruptedException {
		ExchangeSpecification PoloniexEx = new PoloniexExchange().getDefaultExchangeSpecification();
		PoloniexEx.setUserName("Batu");
	    PoloniexEx.setApiKey("CBU5SD1G-G3K6U3VP-KIK9H73R-0AB52TC4");
	    PoloniexEx.setSecretKey("fe7885337c05f746c53a020dc7591aafa1ab7b337ee2fc0aeefe9d29d26974a10d28815ef789b170f40949808fd9b04c15fb0585841e9ab010b5a7c664d99bef");
	    Exchange exchange = ExchangeFactory.INSTANCE.createExchange(PoloniexEx);
		Ticker ticker;
		Currency currency = new Currency(coin);
		CurrencyPair pair = new CurrencyPair(coin,base);
		ticker = exchange.getMarketDataService().getTicker(pair);
		JSONObject jsonobj = new JSONObject();
	    jsonobj = new JSONObject(ticker);
		double bid;
		double ask;
		bid = jsonobj.getDouble("bid");
		ask = jsonobj.getDouble("ask");
		altvolume = new BigDecimal(btcvolume/ask);
		buyprice = new BigDecimal(ask*buypercent);
		LimitOrder BuyingOrder = new LimitOrder((OrderType.BID), altvolume, pair, null, null, buyprice);
		System.out.println(BuyingOrder.toString());
		
		String limitOrderReturnValueBUY = exchange.getTradeService().placeLimitOrder(BuyingOrder);
		System.out.println("Limit Order return value BUY: " + limitOrderReturnValueBUY);
		if (sell==true) {
			sellprice = new BigDecimal(ask*sellpercent);
			LimitOrder SellingOrder = new LimitOrder((OrderType.ASK), altvolume, pair, null, null, sellprice);
			System.out.println(SellingOrder.toString());
			String limitOrderReturnValueSELL = exchange.getTradeService().placeLimitOrder(SellingOrder);
			System.out.println("Limit Order return value SELL: " + limitOrderReturnValueSELL);
			TimeUnit.SECONDS.sleep(2);
			final Map<Currency, Balance> ALLBalances;
			ALLBalances = exchange.getAccountService().getAccountInfo().getWallet().getBalances();
			BigDecimal	AvailableBalance = ALLBalances.get(currency).getAvailable();
			LimitOrder SellingOrderRepeat = new LimitOrder((OrderType.ASK), AvailableBalance, pair, null, null, sellprice);
			System.out.println(SellingOrderRepeat.toString());
			String limitOrderReturnValueSELLRepeat = exchange.getTradeService().placeLimitOrder(SellingOrderRepeat);
			System.out.println("Limit Order return value SELL REPEAT: " + limitOrderReturnValueSELLRepeat);
		}
	}
	*/
	
	public void recievedQuickBuyMessage(JSONObject message) throws JSONException {
		System.out.println("Recieved quickbuy");
		System.out.println(myList.toString());
		for (int i = 0; i < myList.size(); i++) {
			JsonObject listitem = myList.get(i);
			System.out.println("Looping");
			
			if ((listitem.getString("Basecoin").equals(message.getString("Basecoin"))) && (listitem.getString("Altcoin").equals(message.getString("Altcoin"))) && (listitem.getString("Exchanges").equals(message.getString("Exchanges"))) && listitem.get("millis").toString().equals(Long.toString(message.getLong("millis")))) {
				myList.remove(listitem);
				System.out.println(myList.toString());
				double btcvolume = Double.parseDouble(listitem.get("volume").toString());
				double buypercent =  Double.parseDouble(listitem.get("buypercent").toString());
				Exchange exchange = Exchanges.exchangemap.get(listitem.getString("Exchanges"));
				double ask = message.getDouble("Ask");
				String coin = listitem.getString("Basecoin");
				CurrencyPair pair = new CurrencyPair(listitem.getString("Altcoin"),listitem.getString("Basecoin"));
				System.out.println(ask);
				double buyprice = ask*((buypercent/100)+1);
				System.out.println(buyprice);
				double altvolume = btcvolume/buyprice;
				LimitOrder BuyingOrder = new LimitOrder((OrderType.BID), new BigDecimal(altvolume).setScale(8, RoundingMode.HALF_DOWN), pair, null, null, new BigDecimal(buyprice).setScale(8, RoundingMode.HALF_DOWN));
				System.out.println(BuyingOrder.toString());
				//String limitOrderReturnValueBUY = exchange.getTradeService().placeLimitOrder(BuyingOrder);
			} else {
				System.out.println("Values don't match!!");
			}
		}

	}
	
	public void addtolist(JsonObject quickpricelistadd) {
		myList.add(quickpricelistadd);
		System.out.println(myList.toString());
	}
	
    public void sendQuickPriceRequest(String basecoin, String altcoin, String exchange, double btcvolume, double buypercent) {
    	long millis = System.currentTimeMillis();
    	//String quickpricerequest = ("{\"Coin\":\"" + coin + "\",\"Exchanges\":\"" + exchange + "\",\"request\":\"QuickPrice\",\"licenceKey\":\"" + SocketCommunication.licencekey + "\",\"millis\":\"" + millis + "\"}");
    	String quickpricerequest = Json.createObjectBuilder()
    			.add("Basecoin", basecoin)
    			.add("Altcoin", altcoin)
                .add("Exchanges", exchange)
                .add("request", "QuickPrice")
                .add("licenceKey", SocketCommunication.licencekey)
                .add("millis", millis)
				.build()
				.toString();
    	JsonObject quickpricelistadd = Json.createObjectBuilder()
    			.add("Basecoin", basecoin)
                .add("Altcoin", altcoin)
                .add("Exchanges", exchange)
                .add("volume", btcvolume)
                .add("buypercent", buypercent)
                .add("millis", millis)
				.build();
    	addtolist(quickpricelistadd);
    	SocketCommunication.out.print(quickpricerequest);
    	SocketCommunication.out.flush();
    }
}
