package application;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
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
import java.util.logging.*;
public class Exchanges {
	public HashMap<String, String> exchangecodemap = new HashMap<String, String>();
	public static HashMap<String, Exchange> exchangemap = new HashMap<String, Exchange>();
	public static ArrayList<String> list = new ArrayList<String>();
	public void createExchanges() {
		Thread t = new Thread(new java.lang.Runnable() {
            public void run() {
		exchangecodemap.put("binance", "org.knowm.xchange.binance.BinanceExchange");
		exchangecodemap.put("bitbay", "org.knowm.xchange.bitbay.BitbayExchange");
		exchangecodemap.put("bitfinexv1", "org.knowm.xchange.bitfinex.v1.BitfinexExchange");
		exchangecodemap.put("bitmarket", "org.knowm.xchange.bitmarket.BitMarketExchange");
		exchangecodemap.put("bitstamp", "org.knowm.xchange.bitstamp.BitstampExchange");
		exchangecodemap.put("bittrex", "org.knowm.xchange.bittrex.BittrexExchange");
		exchangecodemap.put("bleuTrade", "org.knowm.xchange.bleutrade.BleutradeExchange");
		exchangecodemap.put("btcmarkets", "org.knowm.xchange.btcmarkets.BTCMarketsExchange");
		exchangecodemap.put("ccex", "org.knowm.xchange.ccex.CCEXExchange");
		exchangecodemap.put("cexio", "org.knowm.xchange.cexio.CexIOExchange");
		exchangecodemap.put("coinbase", "org.knowm.xchange.coinbase.CoinbaseExchange");
		exchangecodemap.put("cryptopia", "org.knowm.xchange.cryptopia.CryptopiaExchange");
		exchangecodemap.put("dsx", "org.knowm.xchange.dsx.DSXExchange");
		exchangecodemap.put("gatecoin", "org.knowm.xchange.gatecoin.GatecoinExchange");
		exchangecodemap.put("gdax", "org.knowm.xchange.gdax.GDAXExchange");
		exchangecodemap.put("geminiv1", "org.knowm.xchange.gemini.v1.GeminiExchange");
		exchangecodemap.put("hitbtc", "org.knowm.xchange.hitbtc.v2.HitbtcExchange");
		exchangecodemap.put("huobi", "org.knowm.xchange.huobi.HuobiExchange");
		exchangecodemap.put("kraken", "org.knowm.xchange.kraken.KrakenExchange");	
		exchangecodemap.put("liqui", "org.knowm.xchange.liqui.LiquiExchange");
		exchangecodemap.put("livecoin", "org.knowm.xchange.livecoin.LivecoinExchange");
		exchangecodemap.put("poloniex", "org.knowm.xchange.poloniex.PoloniexExchange");		
		exchangecodemap.put("quoine", "org.knowm.xchange.quoine.QuoineExchange");
		exchangecodemap.put("yobit", "org.knowm.xchange.yobit.YoBitExchange");
		
		String everything = null;
		BufferedReader br = null;
		try {
				br = new BufferedReader(new FileReader("exchanges.txt"));
			    StringBuilder sb = new StringBuilder();
			    String line = br.readLine();
	
			    while (line != null) {
			        sb.append(line);
			        sb.append(System.lineSeparator());
			        line = br.readLine();
			    }
			    everything = sb.toString();
		} catch (FileNotFoundException e) {
			Main.logger.log(Level.SEVERE, "exchanges.txt file not found. Error setting up exchanges");
		} catch (IOException e) {
			Main.logger.log(Level.SEVERE, "Error reading exchanges.txt file");
		} finally {
			try {br.close();} catch (IOException e) {e.printStackTrace();}
		}
		if (!everything.isEmpty()) {
			JSONObject jsonmessage = new JSONObject(everything);
			Iterator<?> keys = jsonmessage.keys();
			while(keys.hasNext()) {
				String key = (String)keys.next();
				key = key.toLowerCase();
				System.out.println(key);
				
				JSONObject specific = (JSONObject) jsonmessage.get(key);
				String apikey = specific.getString("apikey");
				String apisecret = specific.getString("apisecret");
				System.out.println("logged");
				Main.logger.log(Level.INFO, "Creating exchange for " + key);
				String longex = exchangecodemap.get(key);
				System.out.println("LongEx: " + longex);
				ExchangeSpecification exSpec = new ExchangeSpecification(longex);
				exSpec.setApiKey(apikey);
				exSpec.setSecretKey(apisecret);
				try {
				    Exchange ex = ExchangeFactory.INSTANCE.createExchange(exSpec);
				    exchangemap.put(key, ex);
				    list.add(key);
				    Main.logger.log(Level.INFO, "Succesfully created exchange for " + key);	
		        } catch (Exception e) {
		        	Main.logger.log(Level.SEVERE, "Error creating exchange for " + key + " | " + e.getMessage());	
		        }  
			}
		} else {
			Main.logger.log(Level.SEVERE, "exchanges.txt file empty. Error setting up exchanges");
		}
    }});
	t.start();
	}
}
