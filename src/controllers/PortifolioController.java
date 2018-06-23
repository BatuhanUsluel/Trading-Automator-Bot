package controllers;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.*;

import org.knowm.xchange.Exchange;
import org.knowm.xchange.ExchangeFactory;
import org.knowm.xchange.ExchangeSpecification;
import org.knowm.xchange.coinmarketcap.CoinMarketCapExchange;
import org.knowm.xchange.coinmarketcap.dto.marketdata.CoinMarketCapTicker;
import org.knowm.xchange.coinmarketcap.service.CoinMarketCapMarketDataService;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.service.marketdata.MarketDataService;

import com.google.gson.JsonParser;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;

public class PortifolioController {
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


	@FXML
	public void initialize() throws IOException, JSONException{
		/*ExchangeSpecification exSpec = new ExchangeSpecification(CoinMarketCapExchange.class.getName());
		Exchange ex = ExchangeFactory.INSTANCE.createExchange(exSpec);
		CoinMarketCapMarketDataService market = (CoinMarketCapMarketDataService) ex.getMarketDataService();
		CoinMarketCapTicker ticker = market.getCoinMarketCapTicker(CurrencyPair.BTC_USD);
		*/
		URL url = new URL("https://api.coinmarketcap.com/v2/ticker/1");
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		con.setRequestMethod("GET");

con.setDoOutput(true);
		DataOutputStream out = new DataOutputStream(con.getOutputStream());
		out.flush();
		out.close();
		con.setConnectTimeout(5000);
		con.setReadTimeout(5000);
		int status = con.getResponseCode();
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
		JSONObject btcusd = objJsonObject.getJSONObject("data").getJSONObject("quotes").getJSONObject("USD");
		double Dbtcusdprice = btcusd.getDouble("price");
		double DHourChange = btcusd.getDouble("percent_change_1h");
		double DDayChange = btcusd.getDouble("percent_change_24h");
		double DWeekChange = btcusd.getDouble("percent_change_7d");
		if (DHourChange>0) {
			HourChange.setTextFill(Color.GREEN);
			HourChange.setText("+" + (Double.toString(DHourChange))+ "%");
		} else {
			HourChange.setTextFill(Color.RED);
			HourChange.setText(" " + (Double.toString(DHourChange))+ "%");
		}
		if (DDayChange>0) {
			DayChange.setTextFill(Color.GREEN);
			DayChange.setText("+" + (Double.toString(DDayChange))+ "%");
			btcusdchange.setTextFill(Color.GREEN);
			btcusdchange.setText("+" + Double.toString(DDayChange) + "%");
		} else {
			DayChange.setTextFill(Color.RED);
			DayChange.setText(" " + (Double.toString(DDayChange))+ "%");
			btcusdchange.setTextFill(Color.RED);
			btcusdchange.setText(Double.toString(DDayChange)+ "%");
		}
		if (DWeekChange>0) {
			WeekChange.setTextFill(Color.GREEN);
			WeekChange.setText("+" + (Double.toString(DWeekChange))+ "%");
		} else {
			WeekChange.setTextFill(Color.RED);
			WeekChange.setText(" " + (Double.toString(DWeekChange))+ "%");
		}
		
		Sbtcusd.setText("$" + (Double.toString(Dbtcusdprice)));
	}
}