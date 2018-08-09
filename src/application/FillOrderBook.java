package application;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

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

public class FillOrderBook implements Runnable {
	
	private String buysell;
	private String exchangeS;
	private String endpriceS;
	private String noofordersS;
	private String balanceusedS;
	private String startpriceS;
	private String alt;
	private String base;
	public FillOrderBook(String base, String alt, String startpriceS, String endpriceS, String balanceusedS, String noofordersS, String buysell, String exchangeS) {
		this.base=base;
		this.alt=alt;
		this.startpriceS=startpriceS;
		this.endpriceS=endpriceS;
		this.balanceusedS=balanceusedS;
		this.noofordersS=noofordersS;
		this.buysell=buysell;
		this.exchangeS=exchangeS;
	}
	
	@Override
	public void run() {
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
			person.addOrderData("Running Quick Buy\n"
					+ String.format("%-10s:%10s\n","Base", base)
					+ String.format("%-10s:%10s\n","Alt", alt)
					+ String.format("%-10s:%10s\n","Start Price", startpriceS)
					+ String.format("%-10s:%10s\n","End Price", endpriceS)
					+ String.format("%-10s:%10s\n","Balance Used", balanceusedS)
					+ String.format("%-10s:%10s\n","Number of Orders", noofordersS)
					+ String.format("%-10s:%10s\n","Order Type", buysell)
					+ String.format("%-10s:%10s\n","Exchange", exchangeS)
					+ "--------------------------------------\n");			
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		while (currentprice<=endprice) {
			double altvolume = volumeperorder/currentprice;
			LimitOrder SellingOrder = new LimitOrder((OrderType.ASK), new BigDecimal(altvolume).setScale(8, RoundingMode.HALF_DOWN), pair, null, null, new BigDecimal(currentprice).setScale(8, RoundingMode.HALF_DOWN));
			person.addOrderData(SellingOrder.toString() + "\n");
			//String limitOrderReturnValueSELL = exchange.getTradeService().placeLimitOrder(SellingOrder);
			//System.out.println("Limit Order return value SELL: " + limitOrderReturnValueSELL);
			currentprice = currentprice + interval;
		}
	}
}
