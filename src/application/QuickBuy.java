package application;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
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
import controllers.DashboardController.Person;
public class QuickBuy {
	static HashMap<java.lang.Integer, Person> hmap = new HashMap<java.lang.Integer, Person>();
	private static List<JsonObject> myList = new ArrayList<>();
	public static void recievedQuickBuyMessage(JSONObject message) throws JSONException, IOException {
		System.out.println("Recieved quickbuy");
		System.out.println(myList.toString());
		for (int i = 0; i < myList.size(); i++) {
			JsonObject listitem = myList.get(i);
			System.out.println("Looping");
			
			if ((listitem.getString("base").equals(message.getString("base"))) && (listitem.getString("alt").equals(message.getString("alt"))) && (listitem.getString("Exchanges").equals(message.getString("Exchanges"))) && listitem.get("millisstart").toString().equals(Long.toString(message.getLong("millisstart")))) {
				Person person = hmap.get(listitem.getInt("orderid"));
				
				myList.remove(listitem);
				System.out.println(myList.toString());
				double btcvolume = Double.parseDouble(listitem.get("volume").toString());
				double buypercent =  Double.parseDouble(listitem.get("buypercent").toString());
				Exchange exchange = Exchanges.exchangemap.get(listitem.getString("Exchanges"));
				double ask = message.getDouble("Ask");
				person.addOrderData("Price: " + ask + "\n");
				CurrencyPair pair = new CurrencyPair(listitem.getString("alt"),listitem.getString("base"));
				System.out.println(ask);
				double buyprice = ask*((buypercent/100)+1);
				System.out.println(buyprice);
				double altvolume = btcvolume/buyprice;
				LimitOrder BuyingOrder = new LimitOrder((OrderType.BID), new BigDecimal(altvolume).setScale(8, RoundingMode.HALF_DOWN), pair, null, null, new BigDecimal(buyprice).setScale(8, RoundingMode.HALF_DOWN));
				person.addOrderData("Placing buy order for " + btcvolume + (listitem.getString("base")) + "(" + new BigDecimal(altvolume).setScale(8, RoundingMode.HALF_DOWN) + (listitem.getString("alt")) + ") @ price: " + new BigDecimal(buyprice).setScale(8, RoundingMode.HALF_DOWN) + "\n");
				person.addOrderData(BuyingOrder.toString());
				if (btcvolume>0.0001) {
					String limitOrderReturnValueBUY = exchange.getTradeService().placeLimitOrder(BuyingOrder);
				}
			} else {
				System.out.println("Values don't match!!");
			}
		}

	}
	
	public static void addtolist(JsonObject quickpricelistadd, Person person) {
		myList.add(quickpricelistadd);
		hmap.put(quickpricelistadd.getInt("orderid"),person);
		System.out.println(myList.toString());
	}
	
    public static void sendQuickPriceRequest(String base, String alt, String Exchanges, double btcvolume, double buypercent) {
    	long millis = System.currentTimeMillis();
    	Random rand = new Random(); 
    	int value = rand.nextInt(1000000000); 
    	Date date = new Date(millis);
    	SimpleDateFormat format = new SimpleDateFormat("dd/MM hh:mm:ss", Locale.US);
    	String text = format.format(date);
    	String quickpricerequest = Json.createObjectBuilder()
    			.add("base", base)
    			.add("alt", alt)
                .add("Exchanges", Exchanges)
                .add("request", "quickBuy")
                .add("licenceKey", SocketCommunication.licencekey)
                .add("millisstart", millis)
                .add("orderid", value)
                .add("endtime",text)
                .add("running","False")
				.build()
				.toString();
    	JsonObject quickpricelistadd = Json.createObjectBuilder()
    			.add("base", base)
                .add("alt", alt)
                .add("Exchanges", Exchanges)
                .add("volume", btcvolume)
                .add("buypercent", buypercent)
                .add("request", "quickBuy")
                .add("millisstart", millis)
                .add("orderid", value)
                .add("endtime",text)
                .add("running","False")
				.build();
    	
		DashboardController dash = new DashboardController();
    	try {
			Person person = dash.newOrder(new JSONObject(quickpricelistadd.toString()));
			addtolist(quickpricelistadd,person);
			person.addOrderData("Running Quick Buy\n"
					+ String.format("%-10s:%10s\n","Base",base)
					+ String.format("%-10s:%10s\n","Alt", alt)
					+ String.format("%-10s:%10s\n","Volume", btcvolume)
					+ String.format("%-10s:%10s\n","Buy Percent", buypercent)
					+ String.format("%-10s:%10s\n","Exchange", Exchanges) + "--------------------------------------\n");			
			
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
    	SocketCommunication.out.print(quickpricerequest);
    	SocketCommunication.out.flush();
    }
}
