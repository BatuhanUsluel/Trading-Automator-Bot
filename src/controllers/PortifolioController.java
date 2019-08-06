package controllers;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import org.json.JSONException;
import org.json.JSONObject;
import org.knowm.xchange.Exchange;
import org.knowm.xchange.currency.Currency;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.account.Balance;
import org.knowm.xchange.dto.account.Wallet;
import org.knowm.xchange.dto.marketdata.Ticker;

import com.dslplatform.json.DslJson;
import com.dslplatform.json.JsonWriter;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import application.Exchanges;
import application.Main;
import controllers.PortifolioController.Person;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.paint.Color;

public class PortifolioController {
	
	@FXML private TableView<controllers.PortifolioController.Person> tablebalance;
    @FXML private Label balance;
    @FXML private Label usdbalance;
    @FXML private Label btcusdchange;
    @FXML private Label marketcap;
    @FXML private Label Volume;
    @FXML private Label Percent;
    @FXML private Label HourChange;
    @FXML private Label DayChange;
    @FXML private Label WeekChange;
    @FXML private Label ETHUSD;
    @FXML private Label ETHUSDChange;
    @FXML private Label Sbtcusd;

    private static class BTCValues {
        public static double Dbtcusdprice;
        public static double DHourChange;
        public static double DDayChange;
        public static double DWeekChange;
    }
    private static class ETHValues {
        public static double price;
        public static double daychange;
    }
    private static class GlobalData {
        public static double percentofmcap;
        public static double Dmarketcap;
        public static double volume;
    }
    private static class ranthread {
    	public static boolean ranthread;
    }
    private JsonObject jsonObject;
	@SuppressWarnings("unchecked")
	@FXML
	public void initialize() throws IOException, JSONException, InterruptedException{
		ObservableList<Person> data =  FXCollections.observableArrayList();
		ArrayList<Thread> threads = new ArrayList<Thread>();	
		//BTC TICKER
	    Thread btcticker = new Thread(new Runnable() {
	        public void run()
	        {
	        	try {
	        		System.out.println("TICKER");
		        	URL url = new URL("https://api.coingecko.com/api/v3/coins/bitcoin?localization=false&tickers=false&market_data=true&community_data=false&developer_data=false&sparkline=false");
		    		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		    		con.getResponseCode();
		    		BufferedReader in;
					in = new BufferedReader(
					new InputStreamReader(con.getInputStream()));
		    		String inputLine;
		    		StringBuffer content = new StringBuffer();
		    		while ((inputLine = in.readLine()) != null) {
		    		    content.append(inputLine);
		    		}
		    		in.close();
		    		con.disconnect();
		    		JsonParser parser = new JsonParser();
		    		Object obj = parser.parse(content.toString());
		    		JSONObject objJsonObject = new JSONObject(obj.toString());
		    		JSONObject marketdata = objJsonObject.getJSONObject("market_data");
		    		BTCValues.Dbtcusdprice = marketdata.getJSONObject("current_price").getDouble("usd");;
		    		BTCValues.DHourChange = marketdata.getDouble("price_change_percentage_14d");
		    		BTCValues.DDayChange = marketdata.getDouble("price_change_percentage_24h");
		    		BTCValues.DWeekChange = marketdata.getDouble("price_change_percentage_7d");
		    		GlobalData.Dmarketcap = marketdata.getJSONObject("market_cap").getDouble("usd");
		    		GlobalData.volume = marketdata.getJSONObject("total_volume").getDouble("usd");
		    		ObservableList<Person> list = tablebalance.getItems();
		    		int i = 0;
		    		for (Person p : list) {
		    			p.setUSDWorth(String.valueOf((Double.valueOf(p.getBTCWorth()) * BTCValues.Dbtcusdprice)));
		    			p.setDollarPrice(String.valueOf(Double.valueOf(p.getPrice())*BTCValues.Dbtcusdprice));
		    			System.out.println("Dollar price: " + String.valueOf(Double.valueOf(p.getPrice())*BTCValues.Dbtcusdprice));
		    			list.set(i, p);
		    			i++;
		    		}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	        }
	    });  
	    btcticker.start();
	    threads.add(btcticker);
	  	    Thread ethticker = new Thread(new Runnable() {
	  	        public void run()
	  	        {
	  	        	try {
	  		        	URL url = new URL("https://api.coingecko.com/api/v3/global");
	  		    		HttpURLConnection con = (HttpURLConnection) url.openConnection();
	  		    		con.getResponseCode();
	  		    		BufferedReader in;
	  					in = new BufferedReader(
	  					new InputStreamReader(con.getInputStream()));
	  		    		String inputLine;
	  		    		StringBuffer content = new StringBuffer();
	  		    		while ((inputLine = in.readLine()) != null) {
	  		    		    content.append(inputLine);
	  		    		}
	  		    		in.close();
	  		    		con.disconnect();
	  		    		JsonParser parser = new JsonParser();
			    		Object obj = parser.parse(content.toString());
			    		JSONObject objJsonObject = new JSONObject(obj.toString());
	  		    		GlobalData.percentofmcap = objJsonObject.getJSONObject("data").getJSONObject("market_cap_percentage").getDouble("btc");
	  				} catch (IOException e) {
	  					// TODO Auto-generated catch block
	  					e.printStackTrace();
	  				} catch (JSONException e) {
	  					// TODO Auto-generated catch block
	  					e.printStackTrace();
	  				}
	  	        }
	  	    });  
	  	  ethticker.start();
	  	  threads.add(ethticker);
	       new Thread() {
	            public void run() {
	            	DecimalFormat df = new DecimalFormat("0", DecimalFormatSymbols.getInstance(Locale.ENGLISH));
	        		DecimalFormat percentdecimal = new DecimalFormat("##.#", DecimalFormatSymbols.getInstance(Locale.ENGLISH));
	        		DecimalFormat pricedecimal = new DecimalFormat("$#########.#", DecimalFormatSymbols.getInstance(Locale.ENGLISH));
	        		
	        		df.setMaximumFractionDigits(340); // 340 = DecimalFormat.DOUBLE_FRACTION_DIGITS
	                for (Thread thread : threads) {
	        	        try {
							thread.join();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
	        	    }
	                Platform.runLater(new Runnable() {
	                    public void run() {
	                    	Volume.setText("$" + addCommasToNumericString(df.format(GlobalData.volume)));
	                		marketcap.setText("$" + addCommasToNumericString(df.format(GlobalData.Dmarketcap)));
	                		Percent.setText(percentdecimal.format(GlobalData.percentofmcap) + "%");
	                		Color CustomRed = Color.valueOf("#d05b5b");
	                		if (BTCValues.DHourChange>0) {
	                			HourChange.setTextFill(Color.GREEN);
	                			HourChange.setText("+" + (df.format(BTCValues.DHourChange))+ "%");
	                		} else {
	                			HourChange.setTextFill(CustomRed);
	                			HourChange.setText(" " + (df.format(BTCValues.DHourChange))+ "%");
	                		}
	                		if (BTCValues.DDayChange>0) {
	                			DayChange.setTextFill(Color.GREEN);
	                			DayChange.setText("+" + (df.format(BTCValues.DDayChange))+ "%");
	                			btcusdchange.setTextFill(Color.GREEN);
	                			btcusdchange.setText("+" + df.format(BTCValues.DDayChange) + "%");
	                		} else {
	                			DayChange.setTextFill(CustomRed);
	                			DayChange.setText(" " + (df.format(BTCValues.DDayChange))+ "%");
	                			btcusdchange.setTextFill(CustomRed);
	                			btcusdchange.setText(df.format(BTCValues.DDayChange)+ "%");
	                		}
	                		if (BTCValues.DWeekChange>0) {
	                			WeekChange.setTextFill(Color.GREEN);
	                			WeekChange.setText("+" + (df.format(BTCValues.DWeekChange))+ "%");
	                		} else {
	                			WeekChange.setTextFill(CustomRed);
	                			WeekChange.setText(" " + (df.format(BTCValues.DWeekChange))+ "%");
	                		}
	                		
	                		Sbtcusd.setText((pricedecimal.format(BTCValues.Dbtcusdprice)));
	                    }
	                });
	            }
	       }.start();

		
		ArrayList<Thread> exchangethreads = new ArrayList<Thread>();
		ArrayList<Thread> changethreads = new ArrayList<Thread>();
		//Get balances from each exchanges for all currencies and add them to hashmap, with currency being the key and the btc worth being the value(double)
		HashMap<Currency, Double[]> balancepercurrency = new HashMap<Currency, Double[]>();
		for (Entry<String, Exchange> entry : Exchanges.exchangemap.entrySet()) {
		    String ExchangeString = entry.getKey();
		    System.out.println("Looping for exchange: " + ExchangeString);
		    Exchange Exchange = entry.getValue();
		    Thread exchangethread = new Thread(new Runnable() {
		    public void run() {
			    Map<Currency, Balance> balancemap;
			    Map<String, Wallet> walletmap;
				try {
					walletmap = Exchange.getAccountService().getAccountInfo().getWallets();
					for (Entry<String, Wallet> entry3 : walletmap.entrySet()) {
						Wallet wallet = entry3.getValue();
						balancemap = wallet.getBalances();
					
				    for (Entry<Currency, Balance> entry2 : balancemap.entrySet()) {
				    	Currency currency = entry2.getKey();
				    	Balance balance = entry2.getValue();				    	
		            	BigDecimal last = null;
		            	if (balance.getTotal().doubleValue()>0) {
					    	BigDecimal btcbalance = null;
					    	if (currency.toString()!="BTC") {
					    		
								try {
									Ticker ticker = Exchange.getMarketDataService().getTicker(new CurrencyPair(currency.toString(), "BTC"));
									last = ticker.getLast();									
									btcbalance = last.multiply(balance.getTotal());
								} catch (IOException e) {
									e.printStackTrace();
								}
								
					    	} else {
					    		btcbalance = balance.getTotal();
					    		last=new BigDecimal("1");
					    	}
							if (balancepercurrency.get(currency) != null) {
								Double[] array = balancepercurrency.get(currency);
								Double[] newarray = {(double) array[0] + btcbalance.doubleValue(), array[1], (double) array[2] + balance.getTotal().doubleValue()};
								balancepercurrency.put(currency, newarray);
							} else {
								Double[] array = {btcbalance.doubleValue(),last.doubleValue(),balance.getTotal().doubleValue()};
								balancepercurrency.put(currency, array);
							}
				    	}
				    }
				}
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
		    }});
		    exchangethread.start();
		    exchangethreads.add(exchangethread);
		}

		TableColumn<Person, String> CurrencyCol = new TableColumn<Person, String>("Currency");
		CurrencyCol.setCellValueFactory(new PropertyValueFactory<>("Currency"));
		
		TableColumn<Person, String> AmmountCol = new TableColumn<Person, String>("Ammount");
		AmmountCol.setCellValueFactory(new PropertyValueFactory<>("Ammount"));
        
        TableColumn<Person, String> BTCWorthCol = new TableColumn<Person, String>("BTC Worth");
        BTCWorthCol.setCellValueFactory(new PropertyValueFactory<>("BTCWorth"));

        TableColumn<Person, String> USDWorthCol = new TableColumn<Person, String>("USD Worth");
        USDWorthCol.setCellValueFactory(new PropertyValueFactory<>("USDWorth"));
        
        TableColumn<Person, String> PercentCol = new TableColumn<Person, String>("% Of Balance");
        PercentCol.setCellValueFactory(new PropertyValueFactory<>("Percent"));
        
        TableColumn<Person, String> PriceCol = new TableColumn<Person, String>("Price (BTC)");
        PriceCol.setCellValueFactory(new PropertyValueFactory<>("Price"));
        
        TableColumn<Person, String> DollarPriceCol = new TableColumn<Person, String>("Price ($)");
        DollarPriceCol.setCellValueFactory(new PropertyValueFactory<>("DollarPrice"));
        
        String css = this.getClass().getResource("/assets/tableview.css").toExternalForm();
        tablebalance.getStylesheets().setAll(css);
        tablebalance.getColumns().addAll(CurrencyCol,AmmountCol,BTCWorthCol, USDWorthCol, PercentCol,PriceCol, DollarPriceCol);
        DecimalFormat btcbalancedf = new DecimalFormat("###########.########", DecimalFormatSymbols.getInstance(Locale.ENGLISH));
    	DecimalFormat pricedecimalchart = new DecimalFormat("#########.#", DecimalFormatSymbols.getInstance(Locale.ENGLISH));
    	DecimalFormat pricedecimal = new DecimalFormat("$#########.#", DecimalFormatSymbols.getInstance(Locale.ENGLISH));
    	DecimalFormat percentdecimal = new DecimalFormat("##.#", DecimalFormatSymbols.getInstance(Locale.ENGLISH));
        new Thread() {
            public void run() {
            	
            	for (Thread thread : exchangethreads) {
    		        try {
    					thread.join();
    				} catch (InterruptedException e) {
    					e.printStackTrace();
    				}
    		    }
            	ranthread.ranthread=false;
                Platform.runLater(new Runnable() {
                    public void run() {
            			double total=0;		
            			for (Double[] value : balancepercurrency.values()) {
            				 total += value[0];
            			}
            			for (Entry<Currency, Double[]> entry : balancepercurrency.entrySet()) {
            				Currency Currency = entry.getKey();
            				Double[] value = entry.getValue();
            				System.out.println("Current btc: " + BTCValues.Dbtcusdprice);
            				System.out.println("BTC Price: " + value[1]);
            				data.add(new Person(Currency.toString(), value[2].toString(), btcbalancedf.format(value[0]), pricedecimalchart.format(value[0]*BTCValues.Dbtcusdprice), percentdecimal.format((value[0]/total)*100),value[1].toString(), String.valueOf((value[1]*BTCValues.Dbtcusdprice))));
            			}
                    	balance.setText(btcbalancedf.format(total) + " BTC");
            			usdbalance.setText("USD: " + pricedecimal.format(BTCValues.Dbtcusdprice*total));
            	    	tablebalance.setItems(data);
            	        BTCWorthCol.setSortType(TableColumn.SortType.DESCENDING);
            	        tablebalance.getSortOrder().setAll(BTCWorthCol);
            	        ranthread.ranthread=true;
                    }
                });
            	for (Thread thread : changethreads) {
    		        try {
    					thread.join();
    				} catch (InterruptedException e) {
    					e.printStackTrace();
    				}
    		    }
                while(ranthread.ranthread==false) {
                	try {
						TimeUnit.MILLISECONDS.sleep(10);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
                }
                Platform.runLater(new Runnable() {
                    public void run() {
                    	tablebalance.refresh();
                    }
                });
            }
        }.start();
	}

	private String addCommasToNumericString (String digits)
	{
	    String result = "";
	    int len = digits.length();
	    int nDigits = 0;

	    for (int i = len - 1; i >= 0; i--)                      
	    {
	        result = digits.charAt(i) + result;                 
	        nDigits++;                                          
	        if (((nDigits % 3) == 0) && (i > 0))                
	        {
	            result = "," + result;
	        }
	    }
	    return (result);
	}
	
	public static class Person {
		private final String Currency;
		private final String Ammount;
    	private final String BTCWorth;
    	private String USDWorth;
    	private final String Percent;
    	private final String Price;
    	private String DollarPrice;
    	private Person(String Currency, String Ammount, String BTCWorth,String USDWorth, String Percent, String Price, String DollarPrice) {
    		this.Currency = Currency;
    		this.Ammount=Ammount;
			this.BTCWorth  = BTCWorth;
			this.USDWorth = USDWorth;
			this.Percent = Percent;
			this.Price=Price;
			this.DollarPrice=DollarPrice;
    	}
    	
    	 public String getAmmount() {
    		 return Ammount;
    	 }
    	 public String getCurrency() {
             return Currency;
         }
    	 public String getBTCWorth() {
             return BTCWorth;
         }
    	 public String getUSDWorth() {
             return USDWorth;
         }
    	 public String getPercent() {
             return Percent;
         }
    	 public String getPrice() {
    		 return Price;
    	 }

    	 public String getDollarPrice() {
    		 return DollarPrice;
    	 }
    	 
    	 public void setUSDWorth(String USDWorth) {
    		 this.USDWorth=USDWorth;
    	 }
    	 
    	 public void setDollarPrice(String DollarPrice) {
    		 this.DollarPrice=DollarPrice;
    	 }
	}
}