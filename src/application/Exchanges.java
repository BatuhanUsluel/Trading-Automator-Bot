package application;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.json.JSONException;
import org.json.JSONObject;
import org.knowm.xchange.Exchange;
import org.knowm.xchange.ExchangeFactory;
import org.knowm.xchange.ExchangeSpecification;

public class Exchanges {
	public HashMap<String, String> exchangecodemap = new HashMap<String, String>();
	public static HashMap<String, Exchange> exchangemap = new HashMap<String, Exchange>();
	public static ArrayList<String> list = new ArrayList<String>();
	public void createExchanges() throws JSONException, IOException, InterruptedException {
		/*Create hashmap with exchanges short name, and long version. Then loop for everythign in the json text file, and do hashmap.get(exchangeshort).
		 * Then create a exchange object with this using:
		 * 	ExchangeSpecification exSpec = new ExchangeSpecification("class org.knowm.xchange.bittrex.BittrexExchange");
		 * 	Then place this into a NEW hashmap, with the short names and the created exchanges
		 *  Then from other classes, you can do new Exhange().hashmap2.get("bittrex") to get the exchange
		 *  Then you can just get the marketdataservice, etc. from that object
		 */
		
		exchangecodemap.put("Binance", "org.knowm.xchange.binance.BinanceExchange");
		exchangecodemap.put("Bitbay", "org.knowm.xchange.bitbay.BitbayExchange");
		
		exchangecodemap.put("Bitfinexv1", "org.knowm.xchange.bitfinex.v1.BitfinexExchange");
		exchangecodemap.put("Bitmarket", "org.knowm.xchange.bitmarket.BitMarketExchange");
		exchangecodemap.put("Bitstamp", "org.knowm.xchange.bitstamp.BitstampExchange");
		exchangecodemap.put("Bittrex", "org.knowm.xchange.bittrex.BittrexExchange");
		exchangecodemap.put("BleuTrade", "org.knowm.xchange.bleutrade.BleutradeExchange");
		exchangecodemap.put("Btcmarkets", "org.knowm.xchange.btcmarkets.BTCMarketsExchange");
		exchangecodemap.put("ccex", "org.knowm.xchange.ccex.CCEXExchange");
		exchangecodemap.put("cexio", "org.knowm.xchange.cexio.CexIOExchange");
		exchangecodemap.put("coinbase", "org.knowm.xchange.coinbase.CoinbaseExchange");
		exchangecodemap.put("cryptopia", "org.knowm.xchange.cryptopia.CryptopiaExchange");
		exchangecodemap.put("dsx", "org.knowm.xchange.dsx.DSXExchange");
		exchangecodemap.put("gatecoin", "org.knowm.xchange.gatecoin.GatecoinExchange");
		exchangecodemap.put("gdax", "org.knowm.xchange.gdax.GDAXExchange");
		exchangecodemap.put("geminiv1", "org.knowm.xchange.gemini.v1.GeminiExchange");
		exchangecodemap.put("hitbtc", "org.knowm.xchange.hitbtc.HitbtcExchange");
		exchangecodemap.put("huobi", "org.knowm.xchange.huobi.HuobiExchange");
		exchangecodemap.put("kraken", "org.knowm.xchange.kraken.KrakenExchange");	
		exchangecodemap.put("liqui", "org.knowm.xchange.liqui.LiquiExchange");
		exchangecodemap.put("livecoin", "org.knowm.xchange.livecoin.LivecoinExchange");
		exchangecodemap.put("poloniex", "org.knowm.xchange.poloniex.PoloniexExchange");		
		exchangecodemap.put("quoine", "org.knowm.xchange.quoine.QuoineExchange");
		exchangecodemap.put("yobit", "org.knowm.xchange.yobit.YoBitExchange");
		
		String everything;
		try(BufferedReader br = new BufferedReader(new FileReader(new File(getClass().getClassLoader().getResource("exchanges").getFile())))) {
		    StringBuilder sb = new StringBuilder();
		    String line = br.readLine();

		    while (line != null) {
		        sb.append(line);
		        sb.append(System.lineSeparator());
		        line = br.readLine();
		    }
		    everything = sb.toString();
		}
		JSONObject jsonmessage = new JSONObject(everything);
		Iterator<?> keys = jsonmessage.keys();
		while( keys.hasNext() ) {
			String key = (String)keys.next();
			//System.out.println(jsonmessage.get(key));
			System.out.println(key);
			
			JSONObject specific = (JSONObject) jsonmessage.get(key);
			String apikey = specific.getString("apikey");
			String apisecret = specific.getString("apisecret");
			//System.out.println("Exchange:" + exchange + " Apikey: " + apikey + " Apisecret: " + apisecret);
			String longex = exchangecodemap.get(key);
			System.out.println("LongEx: " + longex);
			ExchangeSpecification exSpec = new ExchangeSpecification(longex);
			exSpec.setApiKey(apikey);
			exSpec.setSecretKey(apisecret);
			try {
		    //Exchange ex = ExchangeFactory.INSTANCE.createExchange(exSpec);
			
		    //exchangemap.put(key, ex);
		    System.out.println(key);
		    list.add(key);
		    //System.out.println(ex.getMarketDataService().getTicker(new CurrencyPair("ETH", "BTC")));
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
		    
		}
	}
}
