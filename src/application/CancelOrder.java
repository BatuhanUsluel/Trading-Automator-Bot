package application;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
//import org.knowm.xchange.binance.service.BinanceCancelOrderParams;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.service.trade.TradeService;
import org.knowm.xchange.service.trade.params.CancelOrderParams;

	//binance - Done
	//bitbay - Done
	//bitfinexv1 - Done
	//bitmarket - Done
	//bitstamp - Done
	//bittrex - Done
	//bleuTrade - Done
	//btcmarkets - Done
	//ccex - Done
	//cexio - Done
	//coinbase
	//cryptopia - Done
	//dsx - Done - Check
	//gatecoin
	//gdax
	//geminiv1
	//hitbtc
	//huobi
	//kraken
	//liqui
	//livecoin
	//poloniex
	//quoine
	//yobit
public class CancelOrder {
	public static void cancelOrder(String orderid, CurrencyPair pair, String exchange_string, TradeService tradeService) throws IOException {
		List<String> id = Arrays.asList(new String[]{"bitbay", "bitfinexv1", "bitmarket", "bitstamp", "bittrex" ,
				"bleuTrade", "btcmarkets", "ccex",
				"cexio", "cryptopia", "dsx",
				""});
		List<String> id_and_pair = Arrays.asList(new String[]{"binance"});
		if (id.contains(exchange_string)) {
			tradeService.cancelOrder(orderid);
		} else if (id_and_pair.contains(exchange_string)) {
			System.out.println("Canceling using id and pair");
			CancelOrderParams o = new CancelOrderIDAndPair(pair, orderid);
			tradeService.cancelOrder(o);
		}

//switch (exchange_string) {
//		case "binance":
//			CancelOrderParams o = new BinanceCancelOrderParams(pair, orderid);
//			tradeService.cancelOrder(o);
//			break;
//		case "bitbay":
//			tradeService.cancelOrder(o);
//			break;	
//		case "bitfinexv1":
//			
//			break;
//		case "bitmarket":
//			
//			break;	
//		case "bitstamp":
//			
//			break;
//		case "bittrex":
//			
//			break;	
//		case "bleuTrade":
//			
//			break;
//		case "btcmarkets":
//			
//			break;
//			
//		}
	}
	
	
	
	
	
	//binance
	//bitbay
	//bitfinexv1
	//bitmarket
	//bitstamp
	//bittrex
	//bleuTrade
	//btcmarkets
	//ccex
	//cexio
	//coinbase
	//cryptopia
	//dsx
	//gatecoin
	//gdax
	//geminiv1
	//hitbtc
	//huobi
	//kraken
	//liqui
	//livecoin
	//poloniex
	//quoine
	//yobit
}
