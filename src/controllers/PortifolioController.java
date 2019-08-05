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
import org.knowm.xchange.dto.marketdata.Ticker;

import com.dslplatform.json.DslJson;
import com.dslplatform.json.JsonWriter;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import application.Exchanges;
import application.Main;
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
		String everything = null;
		try {
		BufferedReader br = new BufferedReader(new FileReader("cmc.txt"));
			try {
			    StringBuilder sb = new StringBuilder();
			    String line = br.readLine();
	
			    while (line != null) {
			        sb.append(line);
			        sb.append(System.lineSeparator());
			        line = br.readLine();
			    }
			    everything = sb.toString();
			} finally {
			    br.close();
			}
		} catch (FileNotFoundException e) {
			Main.logger.log(Level.SEVERE, "cmc file not found");
		}

		if (everything!=null && !(everything.isEmpty())) {
			jsonObject = new JsonParser().parse(everything).getAsJsonObject();
			JsonObject metadata = jsonObject.get("metadata").getAsJsonObject();
			long time = metadata.get("usertime").getAsLong();
			long currenttime = System.currentTimeMillis();
			if (time+604800000L<currenttime) {
				jsonObject = getCmcListings();
			}
		} else {
			jsonObject = getCmcListings();
		}
		
		//BTC TICKER
	    Thread btcticker = new Thread(new Runnable() {
	        public void run()
	        {
	        	try {
		        	URL url = new URL("https://api.coinmarketcap.com/v2/ticker/1");
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
		    		System.out.println(content);
		    		in.close();
		    		con.disconnect();
		    		JsonParser parser = new JsonParser();
		    		Object obj = parser.parse(content.toString());
		    		JSONObject objJsonObject = new JSONObject(obj.toString());
		    		JSONObject btcusd = objJsonObject.getJSONObject("data").getJSONObject("quotes").getJSONObject("USD");
		    		BTCValues.Dbtcusdprice = btcusd.getDouble("price");
		    		BTCValues.DHourChange = btcusd.getDouble("percent_change_1h");
		    		BTCValues.DDayChange = btcusd.getDouble("percent_change_24h");
		    		BTCValues.DWeekChange = btcusd.getDouble("percent_change_7d");
		    		
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
	    //ETH TICKER
	  	    Thread ethticker = new Thread(new Runnable() {
	  	        public void run()
	  	        {
	  	        	try {
	  		        	URL url = new URL("https://api.coinmarketcap.com/v2/ticker/1027");
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
	  		    		JsonObject jsonObject = new JsonParser().parse(content.toString()).getAsJsonObject();
	  		    		JsonObject ethusd = jsonObject.get("data").getAsJsonObject().get("quotes").getAsJsonObject().get("USD").getAsJsonObject();
	  		    		ETHValues.price = ethusd.get("price").getAsDouble();
	  		    		ETHValues.daychange = ethusd.get("percent_change_24h").getAsDouble();
	  		    		
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
		//GLOBAL DATA
	    Thread globaldata = new Thread(new Runnable() {
	        public void run()
	        {
	        	try {
					URL url = new URL("https://api.coinmarketcap.com/v2/global/");
					HttpURLConnection con = (HttpURLConnection) url.openConnection();
					con.setDoOutput(true);
					int responseCode = con.getResponseCode();
					BufferedReader in = new BufferedReader(
					new InputStreamReader(con.getInputStream()));
					String inputLine;
					StringBuffer content = new StringBuffer();
					while ((inputLine = in.readLine()) != null) {
					    content.append(inputLine);
					}
					System.out.println(content);
					in.close();
					con.disconnect();
					JsonParser parser = new JsonParser();
					Object obj = parser.parse(content.toString());
					JSONObject objJsonObject = new JSONObject(obj.toString());
					GlobalData.percentofmcap = objJsonObject.getJSONObject("data").getDouble("bitcoin_percentage_of_market_cap");
					GlobalData.Dmarketcap = objJsonObject.getJSONObject("data").getJSONObject("quotes").getJSONObject("USD").getDouble("total_market_cap");
					GlobalData.volume = objJsonObject.getJSONObject("data").getJSONObject("quotes").getJSONObject("USD").getDouble("total_volume_24h");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	        }
	    });
	    globaldata.start();
	    threads.add(globaldata);
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
	                		ETHUSD.setText(pricedecimal.format(ETHValues.price));
	                		Color CustomRed = Color.valueOf("#d05b5b");
	                		 if (ETHValues.daychange>0) {
	                			 	ETHUSDChange.setTextFill(Color.GREEN);
	                			 	ETHUSDChange.setText("+" + (df.format((ETHValues.daychange))+ "%"));
	                			} else {
	                				ETHUSDChange.setTextFill(CustomRed);
	                				ETHUSDChange.setText(" " + (df.format((ETHValues.daychange))+ "%"));
	                			}
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
		HashMap<Currency, Double> balancepercurrency = new HashMap<Currency, Double>();
		HashMap<Currency, Double> changepercurrency = new HashMap<Currency, Double>();
		for (Entry<String, Exchange> entry : Exchanges.exchangemap.entrySet()) {
		    String ExchangeString = entry.getKey();
		    System.out.println("Looping for exchange: " + ExchangeString);
		    Exchange Exchange = entry.getValue();
		    Thread exchangethread = new Thread(new Runnable() {
		    public void run() {
			    Map<Currency, Balance> balancemap;
				try {
					balancemap = Exchange.getAccountService().getAccountInfo().getWallet().getBalances();
				    for (Entry<Currency, Balance> entry2 : balancemap.entrySet()) {
				    	Currency currency = entry2.getKey();
				    	Balance balance = entry2.getValue();
				    	if (changepercurrency.get(currency)==null && balance.getTotal().doubleValue()>0) {
				    		changepercurrency.put(currency, Double.valueOf(0));
				    		 Thread changethread = new Thread(new Runnable() {
				                public void run() {        	
										try {
										int id = 0;
										for (JsonElement y : jsonObject.get("data").getAsJsonArray()) {
											JsonObject yjson = y.getAsJsonObject();
											String symbol = yjson.get("symbol").getAsString();
											if (symbol.equals(currency.toString())) {
												id = yjson.get("id").getAsInt();
												break;
											}
										}
										if (id!=0) {
										URL url = new URL("https://api.coinmarketcap.com/v2/ticker/" + id);
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
					  		    		JsonObject jsonObject = new JsonParser().parse(content.toString()).getAsJsonObject();
					  		    		JsonObject prices = jsonObject.get("data").getAsJsonObject().get("quotes").getAsJsonObject().get("USD").getAsJsonObject();
					  		    		double change = prices.get("percent_change_24h").getAsDouble();
					  		    		changepercurrency.put(currency, change);
										} else {
											
										}
										} catch (IOException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										}
				    			    }
				    		 });
				    		 changethreads.add(changethread);
				    		 changethread.start();
				    		 
				    	}
				    	
				    	if (balance.getTotal().doubleValue()>0) {
					    	BigDecimal btcbalance = null;
					    	if (currency.toString()!="BTC") {
					    		BigDecimal last;
								try {
									Ticker ticker = Exchange.getMarketDataService().getTicker(new CurrencyPair(currency.toString(), "BTC"));
									last = ticker.getLast();									
									btcbalance = last.multiply(balance.getTotal());
								} catch (IOException e) {
									e.printStackTrace();
								}
								
					    	} else {
					    		btcbalance = balance.getTotal();
					    	}
							if (balancepercurrency.get(currency) != null) {
								balancepercurrency.put(currency, (balancepercurrency.get(currency)+btcbalance.doubleValue()));
							} else {
								balancepercurrency.put(currency, btcbalance.doubleValue());
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

		DecimalFormat btcbalancedf = new DecimalFormat("###########.########", DecimalFormatSymbols.getInstance(Locale.ENGLISH));

		TableColumn<Person, String> CurrencyCol = new TableColumn<Person, String>("Currency");
		CurrencyCol.setCellValueFactory(new PropertyValueFactory<>("Currency"));
        
        TableColumn<Person, String> BTCWorthCol = new TableColumn<Person, String>("BTC Worth");
        BTCWorthCol.setCellValueFactory(new PropertyValueFactory<>("BTCWorth"));

        TableColumn<Person, String> USDWorthCol = new TableColumn<Person, String>("USD Worth");
        USDWorthCol.setCellValueFactory(new PropertyValueFactory<>("USDWorth"));
        
        TableColumn<Person, String> PercentCol = new TableColumn<Person, String>("% Of Balance");
        PercentCol.setCellValueFactory(new PropertyValueFactory<>("Percent"));
        
        TableColumn<Person, String> ChangeCol = new TableColumn<Person, String>("24H Change");
        ChangeCol.setCellValueFactory(new PropertyValueFactory<>("Change"));
        String css = this.getClass().getResource("/assets/tableview.css").toExternalForm();
        tablebalance.getStylesheets().setAll(css);
        tablebalance.getColumns().addAll(CurrencyCol,BTCWorthCol, USDWorthCol, PercentCol,ChangeCol);
        new Thread() {
            public void run() {
            	DecimalFormat pricedecimalchart = new DecimalFormat("#########.#", DecimalFormatSymbols.getInstance(Locale.ENGLISH));
            	DecimalFormat pricedecimal = new DecimalFormat("$#########.#", DecimalFormatSymbols.getInstance(Locale.ENGLISH));
            	DecimalFormat percentdecimal = new DecimalFormat("##.#", DecimalFormatSymbols.getInstance(Locale.ENGLISH));
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
            			for (double value : balancepercurrency.values()) {
            				 total+=value;
            			}
            			for (Entry<Currency, Double> entry : balancepercurrency.entrySet()) {
            				Currency Currency = entry.getKey();
            				Double value = entry.getValue();
            				data.add(new Person(Currency.toString(), btcbalancedf.format(value), pricedecimalchart.format(value*BTCValues.Dbtcusdprice), percentdecimal.format((value/total)*100),"N/A"));
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
                for (Person person : data) {
                	Currency currency = new Currency(person.getCurrency());
                	double change = changepercurrency.get(currency);
                	person.setChange(Double.toString(change));
                }
                Platform.runLater(new Runnable() {
                    public void run() {
                    	tablebalance.refresh();
                    }
                });
            }
        }.start();
	}
	private JsonObject getCmcListings() throws IOException {
		Main.logger.log(Level.INFO, "Getting listings from cmc");
		URL url = new URL("https://api.coinmarketcap.com/v2/listings/");
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		con.setDoOutput(true);
		int responseCode = con.getResponseCode();
		BufferedReader in = new BufferedReader(
		new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer content = new StringBuffer();
		while ((inputLine = in.readLine()) != null) {
		    content.append(inputLine);
		}
		String contentstring = content.toString();
		in.close();
		con.disconnect();
		JsonObject jsonObject = new JsonParser().parse(contentstring).getAsJsonObject();
		jsonObject.get("metadata").getAsJsonObject().addProperty("usertime", System.currentTimeMillis());
		try (Writer writer = new BufferedWriter(new OutputStreamWriter(
	              new FileOutputStream("cmc.txt"), "utf-8"))) {
			writer.write(jsonObject.toString());
		}
		return jsonObject;
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
    	private final String BTCWorth;
    	private final String USDWorth;
    	private final String Percent;
    	private String Change;
    	private Person(String Currency,String BTCWorth,String USDWorth, String Percent, String Change) {
    		this.Currency = Currency;
			this.BTCWorth  = BTCWorth;
			this.USDWorth = USDWorth;
			this.Percent = Percent;
			this.Change = Change;
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
    	 public String getChange() {
             return Change;
         }
    	 public void setChange(String Change) {
            this.Change=Change;
         }
	}
}