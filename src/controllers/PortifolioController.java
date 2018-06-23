package controllers;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import org.json.*;

import org.knowm.xchange.Exchange;
import org.knowm.xchange.ExchangeFactory;
import org.knowm.xchange.ExchangeSpecification;
import org.knowm.xchange.coinmarketcap.CoinMarketCapExchange;
import org.knowm.xchange.coinmarketcap.dto.marketdata.CoinMarketCapTicker;
import org.knowm.xchange.coinmarketcap.service.CoinMarketCapMarketDataService;
import org.knowm.xchange.currency.Currency;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.account.Balance;
import org.knowm.xchange.service.marketdata.MarketDataService;

import com.google.gson.JsonParser;

import application.Exchanges;
import controllers.DashboardController.Person;
import javafx.*;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
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
	@SuppressWarnings("unchecked")
	@FXML
	public void initialize() throws IOException, JSONException, InterruptedException{
		ObservableList<Person> data =  FXCollections.observableArrayList();
		ArrayList<Thread> threads = new ArrayList<Thread>();
		//BTC TICKER
		int test = 1;
	    Thread btcticker = new Thread(new Runnable() {
	        public void run()
	        {
	        	try {
	        		int xd = test;
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
	  	        		int xd = test;
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
	  		    		System.out.println(content);
	  		    		in.close();
	  		    		con.disconnect();
	  		    		JsonParser parser = new JsonParser();
	  		    		Object obj = parser.parse(content.toString());
	  		    		JSONObject objJsonObject = new JSONObject(obj.toString());
	  		    		JSONObject ethusd = objJsonObject.getJSONObject("data").getJSONObject("quotes").getJSONObject("USD");
	  		    		ETHValues.price = ethusd.getDouble("price");
	  		    		ETHValues.daychange = ethusd.getDouble("percent_change_24h");
	  		    		
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
	    for (Thread thread : threads) {
	        thread.join();
	    }
		DecimalFormat df = new DecimalFormat("0", DecimalFormatSymbols.getInstance(Locale.ENGLISH));
		DecimalFormat percentdecimal = new DecimalFormat("##.#", DecimalFormatSymbols.getInstance(Locale.ENGLISH));
		DecimalFormat pricedecimal = new DecimalFormat("$#########.#", DecimalFormatSymbols.getInstance(Locale.ENGLISH));
		df.setMaximumFractionDigits(340); // 340 = DecimalFormat.DOUBLE_FRACTION_DIGITS
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
		
		//Get balances from each exchanges for all currencies and add them to hashmap, with currency being the key and the btc worth being the value(double)
		HashMap<Currency, Double> balanceperexchange = new HashMap<Currency, Double>();
		for (Entry<String, Exchange> entry : Exchanges.exchangemap.entrySet()) {
		    String ExchangeString = entry.getKey();
		    Exchange Exchange = entry.getValue();
		    Map<Currency, Balance> balancemap = Exchange.getAccountService().getAccountInfo().getWallet().getBalances();
		    for (Entry<Currency, Balance> entry2 : balancemap.entrySet()) {
		    	Balance balance = entry2.getValue();
		    	if (balance.getTotal().doubleValue()>0) {
			    	Currency currency = entry2.getKey();
			    	BigDecimal btcbalance;
			    	if (currency.toString()!="BTC") {
			    		BigDecimal last = Exchange.getMarketDataService().getTicker(new CurrencyPair(currency.toString(), "BTC")).getLast();
						btcbalance = last.multiply(balance.getTotal());
			    	} else {
			    		btcbalance = balance.getTotal();
			    	}
					System.out.println(currency + ": " + balance + "btcworth" + btcbalance);
					if (balanceperexchange.get(currency) != null) {
						balanceperexchange.put(currency, (balanceperexchange.get(currency)+btcbalance.doubleValue()));
					} else {
						balanceperexchange.put(currency, btcbalance.doubleValue());
					}
		    	}
		    }
		}
		double total=0;
		
		for (double value : balanceperexchange.values()) {
			 total+=value;
		}
		DecimalFormat btcbalancedf = new DecimalFormat("###########.########", DecimalFormatSymbols.getInstance(Locale.ENGLISH));
		balance.setText(btcbalancedf.format(total) + " BTC");
		usdbalance.setText("USD: " + pricedecimal.format(BTCValues.Dbtcusdprice*total));
		
		for (Entry<Currency, Double> entry : balanceperexchange.entrySet()) {
			Currency Currency = entry.getKey();
			Double value = entry.getValue();
			data.add(new Person(Currency.toString(), btcbalancedf.format(value), pricedecimal.format(value*BTCValues.Dbtcusdprice), percentdecimal.format((value/total)*100),"1"));
		}

		
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
        tablebalance.setItems(data);
        BTCWorthCol.setSortType(TableColumn.SortType.ASCENDING);
        tablebalance.getSortOrder().setAll(BTCWorthCol);
        tablebalance.getColumns().addAll(CurrencyCol,BTCWorthCol, USDWorthCol, PercentCol,ChangeCol);
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
		private final SimpleStringProperty Currency;
    	private final SimpleStringProperty BTCWorth;
    	private final SimpleStringProperty USDWorth;
    	private final SimpleStringProperty Percent;
    	private final SimpleStringProperty Change;
    	private Person(String Currency,String BTCWorth,String USDWorth, String Percent, String Change) {
    		this.Currency = new SimpleStringProperty(Currency);
			this.BTCWorth  = new SimpleStringProperty(BTCWorth);
			this.USDWorth = new SimpleStringProperty(USDWorth);
			this.Percent = new SimpleStringProperty(Percent);
			this.Change = new SimpleStringProperty(Change);
    	}
    	
    	 public String getCurrency() {
             return Currency.get();
         }
    	 public String getBTCWorth() {
             return BTCWorth.get();
         }
    	 public String getUSDWorth() {
             return USDWorth.get();
         }
    	 public String getPercent() {
             return Percent.get();
         }
    	 public String getChange() {
             return Change.get();
         }
	}
}