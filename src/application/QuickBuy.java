package application;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import javax.json.Json;
import javax.json.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;
import org.knowm.xchange.Exchange;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.Order.OrderType;
import org.knowm.xchange.dto.trade.LimitOrder;

public class QuickBuy {

	private static List<JsonObject> myList = new ArrayList<>();
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
	
	public static void addtolist(JsonObject quickpricelistadd) {
		myList.add(quickpricelistadd);
		System.out.println(myList.toString());
	}
	
    public static void sendQuickPriceRequest(String basecoin, String altcoin, String exchange, double btcvolume, double buypercent) {
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
