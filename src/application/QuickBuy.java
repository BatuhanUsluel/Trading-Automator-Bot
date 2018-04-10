package application;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import javax.json.Json;
import javax.json.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;
import org.knowm.xchange.Exchange;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.Order.OrderType;
import org.knowm.xchange.dto.trade.LimitOrder;

import controllers.DashboardController;

import  java.lang.Long;
public class QuickBuy {

	private static List<JsonObject> myList = new ArrayList<>();
	public void recievedQuickBuyMessage(JSONObject message) throws JSONException {
		System.out.println("Recieved quickbuy");
		System.out.println(myList.toString());
		for (int i = 0; i < myList.size(); i++) {
			JsonObject listitem = myList.get(i);
			System.out.println("Looping");
			
			if ((listitem.getString("base").equals(message.getString("base"))) && (listitem.getString("alt").equals(message.getString("alt"))) && (listitem.getString("Exchanges").equals(message.getString("Exchanges"))) && listitem.get("millisstart").toString().equals(Long.toString(message.getLong("millisstart")))) {
				myList.remove(listitem);
				System.out.println(myList.toString());
				double btcvolume = Double.parseDouble(listitem.get("volume").toString());
				double buypercent =  Double.parseDouble(listitem.get("buypercent").toString());
				Exchange exchange = Exchanges.exchangemap.get(listitem.getString("Exchanges"));
				double ask = message.getDouble("Ask");
				CurrencyPair pair = new CurrencyPair(listitem.getString("alt"),listitem.getString("base"));
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
    	Random rand = new Random(); 
    	int value = rand.nextInt(1000000000); 
    	Date date = new Date(millis);
    	SimpleDateFormat format = new SimpleDateFormat("dd/MM hh:mm:ss", Locale.US);
    	String text = format.format(date);
    	String quickpricerequest = Json.createObjectBuilder()
    			.add("base", basecoin)
    			.add("alt", altcoin)
                .add("Exchanges", exchange)
                .add("request", "quickBuy")
                .add("licenceKey", SocketCommunication.licencekey)
                .add("millisstart", millis)
                .add("orderid", value)
                .add("endtime",text)
                .add("running","False")
				.build()
				.toString();
    	JsonObject quickpricelistadd = Json.createObjectBuilder()
    			.add("base", basecoin)
                .add("alt", altcoin)
                .add("Exchanges", exchange)
                .add("volume", btcvolume)
                .add("buypercent", buypercent)
                .add("request", "quickBuy")
                .add("millisstart", millis)
                .add("orderid", value)
                .add("endtime",text)
                .add("running","False")
				.build();
    	addtolist(quickpricelistadd);
		DashboardController dash = new DashboardController();
    	try {
			dash.newOrder(new JSONObject(quickpricelistadd.toString()));
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
    	SocketCommunication.out.print(quickpricerequest);
    	SocketCommunication.out.flush();
    }
}
