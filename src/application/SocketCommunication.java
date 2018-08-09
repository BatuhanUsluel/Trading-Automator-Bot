package application;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import org.json.JSONException;
import org.json.JSONObject;
import org.knowm.xchange.exceptions.ExchangeException;
import org.knowm.xchange.exceptions.NotAvailableFromExchangeException;
import org.knowm.xchange.exceptions.NotYetImplementedForExchangeException;

import controllers.ArbitrageController;
import controllers.AveragetradingController;
import controllers.BacktestController;
import controllers.LiveController;
import controllers.MarketController;
import controllers.PendingController;
import controllers.TrailingController;


public class SocketCommunication {
    static Thread receive;
    static long startTime = System.nanoTime();
	public static PrintWriter out;
	public static String licencekey = ("5718570290571");
	private static BufferedReader stdIn;
	public static Socket socket;
	static String server="localhost";
	static int port=8888;
	public static void setup(){
		Thread t = new Thread(new java.lang.Runnable() {
			@Override
			public void run() {
				while(connect(server,port)==false) {
					try {
						TimeUnit.SECONDS.sleep(2);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
		        listen();
			 }	
			});
	    t.setDaemon(true);
	    t.start();
    }

    public static void listen() {
            	boolean x = true;
            	while (x == true){
        			String message;
					try {
						message = stdIn.readLine();
						System.out.println(message);
				    	Thread t = new Thread(new java.lang.Runnable() {
				            public void run() {
				            	try {
				            		
									JSONObject jsonmessage = new JSONObject(message);
									String request = jsonmessage.getString("request");
									switch (request) {
									case "arbitrageOrder":
										Main.logger.log(Level.INFO, "Recieved arbitrage prices");
										HashMap<JSONObject, ArbitrageOrder> arbitrageMap = ArbitrageController.ArbitrageOrderMap;
										for (Entry<JSONObject, ArbitrageOrder> entry : arbitrageMap.entrySet()) {
											JSONObject key = entry.getKey();
												if ((key.getString("base").equals(jsonmessage.getString("base")))
													&& (key.getString("alt").equals(jsonmessage.getString("alt")))
													&& (key.getString("MinArbitrage").equals(jsonmessage.getString("MinArbitrage")))
													&& (key.getString("request").equals(jsonmessage.getString("request")))
													&& (key.getString("licenceKey").equals(jsonmessage.getString("licenceKey")))
													&& key.getLong("millisstart") == (jsonmessage.getLong("millisstart"))
													&& key.getLong("orderid") == (jsonmessage.getLong("orderid"))) {
														ArbitrageOrder value = entry.getValue();
														value.recievedArbitrageOrder(jsonmessage);
												}
										}
										break;
									case "Historic":
										Main.logger.log(Level.INFO, "Recieved historic prices");
										BacktestController.recievedBackTest(jsonmessage);
										System.out.println(message);
										break;
									case "averageTrading":
										Main.logger.log(Level.INFO, "Recieved prices for average trading");								
										HashMap<JSONObject, AverageTrading> hashmapaverage = AveragetradingController.AverageTradingMap;
										for (Entry<JSONObject, AverageTrading> entry : hashmapaverage.entrySet()) {
										    JSONObject key = entry.getKey();
												if ((key.getString("base").equals(jsonmessage.getString("base")))
													&& (key.getString("alt").equals(jsonmessage.getString("alt")))
													&& (key.getString("Exchanges").equals(jsonmessage.getString("Exchanges")))
													&& (key.getString("request").equals(jsonmessage.getString("request")))
													&& (key.getString("coinstotrade").equals(jsonmessage.getString("coinstotrade")))
													&& (key.getString("volumeperorder").equals(jsonmessage.getString("volumeperorder")))
													&& (key.getString("licenceKey").equals(jsonmessage.getString("licenceKey")))
													&& (key.getString("atbid").equals(jsonmessage.getString("atbid")))
													&& (key.getString("buy").equals(jsonmessage.getString("buy")))
													&& (key.getString("loop").equals(jsonmessage.getString("loop")))
													&& key.getLong("millisstart") == (jsonmessage.getLong("millisstart"))) {
														AverageTrading value = entry.getValue();
														value.recievedAverageTrade(jsonmessage);
												}
										}
										break;
									case "trailingStop":
										Main.logger.log(Level.INFO, "Recieved prices for trailing stop");
										HashMap<JSONObject, TrailingStop> hashmaptrailing = TrailingController.TrailingStopMap;
										for (Entry<JSONObject, TrailingStop> entry : hashmaptrailing.entrySet()) {
										    JSONObject key = entry.getKey();
												if ((key.getString("base").equals(jsonmessage.getString("base")))
													&& (key.getString("alt").equals(jsonmessage.getString("alt")))
													&& (key.getString("request").equals(jsonmessage.getString("request")))
													&& (key.getString("volume").equals(jsonmessage.getString("volume")))
													&& (key.getString("trail").equals(jsonmessage.getString("trail")))
													&& (key.getString("buysell").equals(jsonmessage.getString("buysell")))
													&& (key.getString("Exchanges").equals(jsonmessage.getString("Exchanges")))
													&& (key.getString("licenceKey").equals(jsonmessage.getString("licenceKey")))
													&& key.getLong("millisstart") == (jsonmessage.getLong("millisstart"))) {
														TrailingStop value = entry.getValue();
														value.recievedTrailingStop(jsonmessage);
												}
										}
										break;
									case "pendingOrder":
										Main.logger.log(Level.INFO, "Recieved prices for pending order");
										HashMap<JSONObject, PendingOrder> hashmappending = PendingController.PendingOrderMap;
										for (Entry<JSONObject, PendingOrder> entry : hashmappending.entrySet()) {
											JSONObject key = entry.getKey();
												if ((key.getString("base").equals(jsonmessage.getString("base")))
													&& (key.getString("alt").equals(jsonmessage.getString("alt")))
													&& (key.getString("request").equals(jsonmessage.getString("request")))
													&& (key.getString("priceorder").equals(jsonmessage.getString("priceorder")))
													&& (key.getString("volume").equals(jsonmessage.getString("volume")))
													&& (key.getString("percent").equals(jsonmessage.getString("percent")))
													&& (key.getString("Exchanges").equals(jsonmessage.getString("Exchanges")))
													&& (key.getString("licenceKey").equals(jsonmessage.getString("licenceKey")))
													&& key.getLong("millisstart") == (jsonmessage.getLong("millisstart"))) {
														PendingOrder value = entry.getValue();
														value.recievedPendingOrder(jsonmessage);
												}
										}
										break;
									case "marketMaking":
										Main.logger.log(Level.INFO, "Recieved prices for market making");
										HashMap<JSONObject, MarketMaking> hashmapmarket = MarketController.marketMakingMap;
										for (Entry<JSONObject, MarketMaking> entry : hashmapmarket.entrySet()) {
											JSONObject key = entry.getKey();
												if ((key.getString("base").equals(jsonmessage.getString("base")))
													&& (key.getString("alt").equals(jsonmessage.getString("alt")))
													&& (key.getString("spread").equals(jsonmessage.getString("spread")))
													&& (key.getString("MaxBal").equals(jsonmessage.getString("MaxBal")))
													&& (key.getString("MinBal").equals(jsonmessage.getString("MinBal")))
													&& (key.getString("Exchanges").equals(jsonmessage.getString("Exchanges")))
													&& (key.getString("request").equals(jsonmessage.getString("request")))
													&& (key.getString("licenceKey").equals(jsonmessage.getString("licenceKey")))
													&& key.getLong("millisstart") == (jsonmessage.getLong("millisstart"))) {
														MarketMaking value = entry.getValue();
														value.recievedMarketOrder(jsonmessage);
												}
										}
										break;
									case "prevlive":
										Main.logger.log(Level.INFO, "Recieved prices live trading(historical)");
										HashMap<Integer, LiveTrading> livemap = LiveController.LiveTradingMap;
										for (Entry<Integer, LiveTrading> entry : livemap.entrySet()) {
											int key = entry.getKey();
											if (key==jsonmessage.getInt("orderid")) {
												LiveTrading value = entry.getValue();
												value.recievedPreviousPrices(jsonmessage);
											}
										}
										break;
									case "LiveTrading":
										Main.logger.log(Level.INFO, "Recieved prices for live trading");
										HashMap<Integer, LiveTrading> livemap2 = LiveController.LiveTradingMap;
										for (Entry<Integer, LiveTrading> entry : livemap2.entrySet()) {
											int key = entry.getKey();
											if (key==jsonmessage.getInt("orderid")) {
												LiveTrading value = entry.getValue();
												value.recievedLiveTrading(jsonmessage);
											}
										}
										break;
										
									default:
										Main.logger.log(Level.WARNING, "Recieved invalid message from server: " + request);
										System.out.println("Invalid");
									}	
									System.out.println("M:   " + message);
						            } catch (NotAvailableFromExchangeException | NotYetImplementedForExchangeException
											| ExchangeException | IOException e) {
										e.printStackTrace();
						           } catch (JSONException e) {
										e.printStackTrace();
									} catch (InterruptedException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
						          }
						    	});
				    	t.setDaemon(true);
				    	t.start();
					} catch (SocketException e) {
						Main.logger.log(Level.SEVERE, "Lost connection with server. Retrying");
						connect(server, port);
						try {
							TimeUnit.SECONDS.sleep(2);
						} catch (InterruptedException e1) {
							e1.printStackTrace();
						}
						e.printStackTrace();
					} catch (IOException e3) {
						e3.printStackTrace();
					}
	            }
    }
    
    private static boolean connect(String server, int port){
        try {
            socket = new Socket(server, port);
            stdIn =new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            Main.logger.log(Level.INFO, "Connected to server");
            return true;
        } catch (UnknownHostException e) {
        	Main.logger.log(Level.SEVERE, "Error connecting to server. UnknownHost | " + e.getMessage());
        	return false;
        } catch (IOException e) {
        	Main.logger.log(Level.SEVERE, "Error connecting to server. IOException | " + e.getMessage());
        	return false;
        }
    }
}