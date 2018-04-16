package application;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.Date;
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
import org.knowm.xchange.exceptions.ExchangeException;
import org.knowm.xchange.exceptions.NotAvailableFromExchangeException;
import org.knowm.xchange.exceptions.NotYetImplementedForExchangeException;

import controllers.DashboardController;
import controllers.DashboardController.Person;

public class FillOrderBook {
	public static void fillOrderBook(String base, String alt, String startpriceS, String endpriceS, String balanceusedS, String noofordersS, String buysell, String exchangeS) throws NotAvailableFromExchangeException, NotYetImplementedForExchangeException, ExchangeException, IOException, JSONException {
		double startprice = Double.parseDouble(startpriceS);
		double endprice = Double.parseDouble(endpriceS);
		double balanceused = Double.parseDouble(balanceusedS);
		double nooforders = Double.parseDouble(noofordersS);
		Exchange exchange = Exchanges.exchangemap.get(exchangeS);
		
		
		double volumeperorder = balanceused/nooforders;
		double currentprice = startprice;
		CurrencyPair pair = new CurrencyPair(alt,base);
		double interval = (Math.abs(startprice-endprice))/nooforders;
		Random rand = new Random(); 
    	int value = rand.nextInt(1000000000); 
    	java.lang.Long millis = System.currentTimeMillis();
    	Date date = new Date(millis);
    	SimpleDateFormat format = new SimpleDateFormat("dd/MM hh:mm:ss", Locale.US);
    	String text = format.format(date);
    	JSONObject fillorderJSON = new JSONObject();
    	fillorderJSON.put("base", base);
    	fillorderJSON.put("alt", alt);
    	fillorderJSON.put("Exchanges", exchangeS);
    	fillorderJSON.put("request", "fillOrderBook");
    	fillorderJSON.put("millisstart", System.currentTimeMillis());
    	fillorderJSON.put("orderid", value);
    	fillorderJSON.put("endtime",text);
    	fillorderJSON.put("running","False");
		DashboardController dash = new DashboardController();
		Person person = null;
    	try {
			person = dash.newOrder(fillorderJSON);
			person.addOrderData("Running Quick Buy"
					+ "\nParameters:"
					+ "\nBase: " + base
					+ "\nAlt: " + alt
					+ "\nStart Price: " + startpriceS
					+ "\nEnd Price: " + endpriceS
					+ "\nBalance Used: " + balanceusedS
					+ "\nNumber of Orders: " + noofordersS
					+ "\nOrder Type: " + buysell
					+ "\nExchange: " + exchangeS
					+ "\n--------------------------------------\n\n");			
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		while (currentprice<=endprice) {
			//FIX ORDER SIZE WITH BASE/ALT!!
			//FIX ORDER SIZE WITH BASE/ALT!!
			//FIX ORDER SIZE WITH BASE/ALT!!
			//FIX ORDER SIZE WITH BASE/ALT!!
			//FIX ORDER SIZE WITH BASE/ALT!!
			//FIX ORDER SIZE WITH BASE/ALT!!
			//Place order @ currentprice with balanceperorder
			LimitOrder SellingOrder = new LimitOrder((OrderType.ASK), new BigDecimal(volumeperorder).setScale(8, RoundingMode.HALF_DOWN), pair, null, null, new BigDecimal(currentprice).setScale(8, RoundingMode.HALF_DOWN));
			person.addOrderData(SellingOrder.toString() + "\n");
			//String limitOrderReturnValueSELL = exchange.getTradeService().placeLimitOrder(SellingOrder);
			//System.out.println("Limit Order return value SELL: " + limitOrderReturnValueSELL);
			currentprice = currentprice + interval;
		}
	}
}
