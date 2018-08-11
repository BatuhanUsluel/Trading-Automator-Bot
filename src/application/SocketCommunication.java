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
									case "Historic":
										Main.logger.log(Level.INFO, "Recieved historic prices");
										BacktestController.recievedBackTest(jsonmessage);
										System.out.println(message);
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