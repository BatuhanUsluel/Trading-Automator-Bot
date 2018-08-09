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
import java.util.logging.Level;

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
	
    public void runOrder(String base, String alt, String ExchangeString, double btcvolume, double buypercent) {
    	long millis = System.currentTimeMillis();
    	Random rand = new Random(); 
    	int value = rand.nextInt(1000000000); 
    	Date date = new Date(millis);
    	SimpleDateFormat format = new SimpleDateFormat("dd/MM hh:mm:ss", Locale.US);
    	String endtime = format.format(date);
    	Exchange exchange = Exchanges.exchangemap.get(ExchangeString);
    	JsonObject quickpricelistadd = Json.createObjectBuilder()
    			.add("base", base)
                .add("alt", alt)
                .add("Exchanges", ExchangeString)
                .add("volume", btcvolume)
                .add("buypercent", buypercent)
                .add("request", "quickBuy")
                .add("millisstart", millis)
                .add("orderid", value)
                .add("endtime",endtime)
                .add("running","False")
				.build();
		DashboardController dash = new DashboardController();
    	try {
			Person person = dash.newOrder(new JSONObject(quickpricelistadd.toString()));
			person.addOrderData("Running Quick Buy\n"
					+ String.format("%-10s:%10s\n","Base",base)
					+ String.format("%-10s:%10s\n","Alt", alt)
					+ String.format("%-10s:%10s\n","Volume", btcvolume)
					+ String.format("%-10s:%10s\n","Buy Percent", buypercent)
					+ String.format("%-10s:%10s\n","Exchange", ExchangeString) + "--------------------------------------\n");			

    	CurrencyPair pair = new CurrencyPair(alt,base);
    	double ask = exchange.getMarketDataService().getTicker(pair).getAsk().doubleValue();
    	double buyprice = ask*((buypercent/100)+1);
		System.out.println(buyprice);
		double altvolume = btcvolume/buyprice;
		LimitOrder BuyingOrder = new LimitOrder((OrderType.BID), new BigDecimal(altvolume).setScale(8, RoundingMode.HALF_DOWN), pair, null, null, new BigDecimal(buyprice).setScale(8, RoundingMode.HALF_DOWN));
		if (btcvolume>0.0001) {
			person.addOrderData("Placing buy order for " + btcvolume + base + "(" + new BigDecimal(altvolume).setScale(8, RoundingMode.HALF_DOWN) + alt + ") @ price: " + new BigDecimal(buyprice).setScale(8, RoundingMode.HALF_DOWN) + "\n");
			FxDialogs.showInformation(null, "Order Placed");
			Main.logger.log(Level.INFO, "Placed quick buy order");
			String limitOrderReturnValueBUY = exchange.getTradeService().placeLimitOrder(BuyingOrder);
		} else {
			FxDialogs.showInformation(null, "BTC Volume too low(below 0.0001). Unable to place order");
			Main.logger.log(Level.WARNING, "QuickBuy: BTC Volume too low(below 0.0001). Unable to place order");		}
		
	} catch (JSONException e1) {
		e1.printStackTrace();
	} catch (IOException e) {
		e.printStackTrace();
	}
    }
}
