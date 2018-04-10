package application;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;

import org.knowm.xchange.Exchange;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.Order.OrderType;
import org.knowm.xchange.dto.trade.LimitOrder;
import org.knowm.xchange.exceptions.ExchangeException;
import org.knowm.xchange.exceptions.NotAvailableFromExchangeException;
import org.knowm.xchange.exceptions.NotYetImplementedForExchangeException;

public class FillOrderBook {
	public static void fillOrderBook(String base, String alt, String startpriceS, String endpriceS, String balanceusedS, String noofordersS, String buysell, String exchangeS) throws NotAvailableFromExchangeException, NotYetImplementedForExchangeException, ExchangeException, IOException {
		double startprice = Double.parseDouble(startpriceS);
		double endprice = Double.parseDouble(endpriceS);
		double balanceused = Double.parseDouble(balanceusedS);
		double nooforders = Double.parseDouble(noofordersS);
		Exchange exchange = Exchanges.exchangemap.get(exchangeS);
		
		
		double volumeperorder = balanceused/nooforders;
		double currentprice = startprice;
		CurrencyPair pair = new CurrencyPair(alt,base);
		double interval = (Math.abs(startprice-endprice))/nooforders;
		while (currentprice<=endprice) {
			//Place order @ currentprice with balanceperorder
			LimitOrder SellingOrder = new LimitOrder((OrderType.ASK), new BigDecimal(volumeperorder).setScale(8, RoundingMode.HALF_DOWN), pair, null, null, new BigDecimal(currentprice).setScale(8, RoundingMode.HALF_DOWN));
			System.out.println(SellingOrder);
			//String limitOrderReturnValueSELL = exchange.getTradeService().placeLimitOrder(SellingOrder);
			//System.out.println("Limit Order return value SELL: " + limitOrderReturnValueSELL);
			currentprice = currentprice + interval;
		}
	}
}
