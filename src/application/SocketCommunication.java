package application;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;
import java.lang.Runnable;
import javax.json.Json;
import javax.json.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;


public class SocketCommunication {
    static Thread receive;
    static long startTime = System.nanoTime();
	public static PrintWriter out;
	static String licencekey = ("5718570290571");
	private static BufferedReader stdIn;
	public static void setup() throws UnknownHostException, IOException{
    	Socket socket = new Socket("localhost",8888);
        BufferedReader stdIn =new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        SocketCommunication.out = out;
        SocketCommunication.stdIn = stdIn;
        listen();
        System.out.println("Done");    
            	/*Request Types:
            	 * 
            	 * QuickPrice(returns ask, only 1 exchange, single time)
            	 * 		Example: ("{\"Coin\":\"ETH\",\"Exchanges\":\"bittrex\",\"request\":\"QuickPrice\",\"cancel\":\"False\",\"licenceKey\":\"752175198Afs25\"}");
            	 * 
            	 * Historic(Retruns historic OHLCV, 1 exchange, single time)
            	 * 		Example: ("{\"Coin\":\"ETH\",\"Exchanges\":\"bittrex\",\"request\":\"Historic\",\"cancel\":\"False\",\"licenceKey\":\"752175198Afs25\", \"Historic\":{\"StartTime\": \"2018-01-01 00:00:00\", \"Timeframe\":\"1h\", \"Index\":4}}");
            	 * 
            	 * BidAsk(Returns bid, ask & sizes of each, multiple exchanges, repeating every 5 seconds(subsription system))
            	 * 		Example: String decredrequest= ("{\"Coin\":\"DCR\",\"Exchanges\":[\"bittrex\",\"poloniex\"],\"request\":\"BidAsk\",\"cancel\":\"False\",\"licenceKey\":\"752175198Afs25\"}");
            	 * 
            	 * Canceling a Request(Send same exact request, but with cancel being true)
            	 * 		Example: String decredcancel = ("{\"Coin\":\"DCR\",\"Exchanges\":[\"bittrex\", \"poloniex\"],\"request\":\"BidAsk\",\"cancel\":\"True\",\"licenceKey\":\"752175198Afs25\"}");
            	 */
    		    
                	/* Historical:
                	 * 	0: UTC timestamp in milliseconds, integer
                	 *  1: (O)pen price, float
                	 *  2: (H)ighest price, float
                	 *  3: (L)owest price, float
                	 *  4: (C)losing price, float
                	 *  5: (V)olume (in terms of the base currency), float
                	 *  Example: ("{\"Coin\":\"ETH\",\"Exchanges\":\"bittrex\",\"request\":\"Historic\",\"cancel\":\"False\",\"licenceKey\":\"752175198Afs25\", \"Historic\":{\"StartTime\": \"2018-01-01 00:00:00\", \"Timeframe\":\"1h\", \"Index\":4}}");
                	 */
    }
    
	//Not done
    public static void sendHistoricalRequest(String coin, String exchange, String starttime, String timeframe, int index) {
    	String historicalrequest = ("{\"Coin\":\"" + coin + "\",\"Exchanges\":\"" + exchange + "\",\"request\":\"Historic\",\"licenceKey\":\"" + licencekey + "\", \"Historic\":{\"StartTime\": \"" + starttime + "\", \"Timeframe\":\"" + timeframe + "\", \"Index\":" + index + "}}");
    	out.print(historicalrequest);
        out.flush();
    }
    
    //Not done
    public static void sendBidAskRequest(String coin, String[] exchanges, boolean cancel) {
    	String bidaskrequest = ("{\"Coin\":\"" + coin + "\",\"Exchanges\":\"" + exchanges + "\",\"request\":\"BidAsk\",\"cancel\":\"False\",\"licenceKey\":\"" + licencekey + "\"}");
    	out.print(bidaskrequest);
        out.flush();
    }
    
    public static void listen() throws IOException {
    	Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
            	boolean x = true;
            	while (x == true){
        			String message;
					try {
						message = stdIn.readLine();
						JSONObject jsonmessage = new JSONObject(message);
						String request = jsonmessage.getString("request");
						switch (request) {
						case "QuickPrice":
							System.out.println("QuikcPrice");
							QuickBuy quickbuy = new QuickBuy();
							quickbuy.recievedQuickBuyMessage(jsonmessage);
							break;
						case "BidAsk":
							
							break;
						case "Historic":
							
							break;
						case "averageTrading":
							System.out.println("Recieved AVERAGE");
							AverageTrading.recievedAverageTrade(jsonmessage);
							break;
						case "trailingStop":
							HashMap<JSONObject, TrailingStop> hashmap = TrailingController.TrailingStopMap;
							for (Entry<JSONObject, TrailingStop> entry : hashmap.entrySet()) {
							    JSONObject key = entry.getKey();
								if ((key.getString("base").equals(jsonmessage.getString("base")))
									&& (key.getString("alt").equals(jsonmessage.getString("alt")))
									&& (key.getString("request").equals(jsonmessage.getString("request")))
									&& (key.getString("volume").equals(jsonmessage.getString("volume")))
									&& (key.getString("trail").equals(jsonmessage.getString("trail")))
									&& (key.getString("buysell").equals(jsonmessage.getString("buysell")))
									&& (key.getString("exchange").equals(jsonmessage.getString("exchange")))
									&& (key.getString("licenceKey").equals(jsonmessage.getString("licenceKey")))
									&& key.getLong("millisstart") == (jsonmessage.getLong("millisstart"))) {
										TrailingStop value = entry.getValue();
										value.recievedTrailingStop(jsonmessage);
								}
							}
							break;
						case "pendingOrder":
							HashMap<JSONObject, PendingOrder> hashmappending = PendingController.PendingOrderMap;
							for (Entry<JSONObject, PendingOrder> entry : hashmappending.entrySet()) {
								JSONObject key = entry.getKey();
								if ((key.getString("base").equals(jsonmessage.getString("base")))
									&& (key.getString("alt").equals(jsonmessage.getString("alt")))
									&& (key.getString("request").equals(jsonmessage.getString("request")))
									&& (key.getString("priceorder").equals(jsonmessage.getString("priceorder")))
									&& (key.getString("volume").equals(jsonmessage.getString("volume")))
									&& (key.getString("percent").equals(jsonmessage.getString("percent")))
									&& (key.getString("exchange").equals(jsonmessage.getString("exchange")))
									&& (key.getString("licenceKey").equals(jsonmessage.getString("licenceKey")))
									&& key.getLong("millisstart") == (jsonmessage.getLong("millisstart"))) {
										PendingOrder value = entry.getValue();
										value.recievedPendingOrder(jsonmessage);
								}
							}
							break;
						case "marketMaking":
							HashMap<JSONObject, MarketMaking> hashmapmarket = MarketController.marketMakingMap;
							for (Entry<JSONObject, MarketMaking> entry : hashmapmarket.entrySet()) {
								JSONObject key = entry.getKey();
								if ((key.getString("base").equals(jsonmessage.getString("base")))
									&& (key.getString("alt").equals(jsonmessage.getString("alt")))
									&& (key.getString("spread").equals(jsonmessage.getString("spread")))
									&& (key.getString("MaxBal").equals(jsonmessage.getString("MaxBal")))
									&& (key.getString("MinBal").equals(jsonmessage.getString("MinBal")))
									&& (key.getString("exchange").equals(jsonmessage.getString("exchange")))
									&& (key.getString("request").equals(jsonmessage.getString("request")))
									&& (key.getString("licenceKey").equals(jsonmessage.getString("licenceKey")))
									&& key.getLong("millisstart") == (jsonmessage.getLong("millisstart"))) {
										MarketMaking value = entry.getValue();
										value.recievedMarketOrder(jsonmessage);
								}
							}
							break;
						default:
							System.out.println("Invalid");
						}	
						System.out.println("M:   " + message);
					} catch (SocketException e) {
						System.out.println("Socket Exception");
						e.printStackTrace();
						x = false;
					} catch (JSONException e) {
						System.out.println("Json Exception");
						e.printStackTrace();
					} catch (IOException e) {
						System.out.println("IO Exception");
						e.printStackTrace();
					}
	            }
            }
	   });
    t.start();
    }
    
}