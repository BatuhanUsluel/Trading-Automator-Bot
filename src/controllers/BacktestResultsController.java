package controllers;

import org.jfree.chart.fx.ChartViewer;
import org.ta4j.core.TimeSeries;
import org.ta4j.core.TradingRecord;
import org.ta4j.core.analysis.criteria.AverageProfitableTradesCriterion;
import org.ta4j.core.analysis.criteria.BuyAndHoldCriterion;
import org.ta4j.core.analysis.criteria.MaximumDrawdownCriterion;
import org.ta4j.core.analysis.criteria.NumberOfTradesCriterion;
import org.ta4j.core.analysis.criteria.RewardRiskRatioCriterion;
import org.ta4j.core.analysis.criteria.TotalProfitCriterion;
import org.ta4j.core.analysis.criteria.VersusBuyAndHoldCriterion;

import com.jfoenix.controls.JFXTextArea;

import javafx.embed.swing.SwingNode;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;

public class BacktestResultsController {
	@FXML private AnchorPane MainAnchor;
    @FXML private AnchorPane Anchor;
    @FXML private AnchorPane Anchor1;
    @FXML private JFXTextArea textarea;
    @FXML
    public void initialize(){
    	ChartViewer viewer = new ChartViewer(BacktestController.chart);
    	viewer.setScaleShape(true);
    	
    	
    	AnchorPane.setTopAnchor(viewer, 0.0);
        AnchorPane.setRightAnchor(viewer, 0.0);
        AnchorPane.setLeftAnchor(viewer, 0.0);
        AnchorPane.setBottomAnchor(viewer, 20.0);
        //viewer.setPrefWidth(100);
        //viewer.setPrefHeight(500);
        // Total profit
        TimeSeries series = BacktestController.series;
        TradingRecord tradingRecord = BacktestController.tradingRecord;
        TotalProfitCriterion totalProfit = new TotalProfitCriterion();
        /*textarea.appendText("Total profit: " + totalProfit.calculate(series, tradingRecord) + "            " + 
        		"Number of trades: " + new NumberOfTradesCriterion().calculate(series, tradingRecord) + "            " + 
        		"Profitable trades ratio: " + new AverageProfitableTradesCriterion().calculate(series, tradingRecord) + "            " +
        		"Maximum drawdown: " + new MaximumDrawdownCriterion().calculate(series, tradingRecord) + "            " +
        		"Reward-risk ratio: " + new RewardRiskRatioCriterion().calculate(series, tradingRecord) + "            " +
        		"Buy-and-hold: " + new BuyAndHoldCriterion().calculate(series, tradingRecord) + "            " +
        		"Custom strategy profit vs buy-and-hold strategy profit: " + new VersusBuyAndHoldCriterion(totalProfit).calculate(series, tradingRecord));*/
        System.out.println("Total profit: " + totalProfit.calculate(series, tradingRecord));
        // Number of trades
        System.out.println("Number of trades: " + new NumberOfTradesCriterion().calculate(series, tradingRecord));
        // Profitable trades ratio
        System.out.println("Profitable trades ratio: " + new AverageProfitableTradesCriterion().calculate(series, tradingRecord));
        // Maximum drawdown
        System.out.println("Maximum drawdown: " + new MaximumDrawdownCriterion().calculate(series, tradingRecord));
        // Reward-risk ratio
        System.out.println("Reward-risk ratio: " + new RewardRiskRatioCriterion().calculate(series, tradingRecord));
        // Buy-and-hold
        System.out.println("Buy-and-hold: " + new BuyAndHoldCriterion().calculate(series, tradingRecord));
        // Total profit vs buy-and-hold
        System.out.println("Custom strategy profit vs buy-and-hold strategy profit: " + new VersusBuyAndHoldCriterion(totalProfit).calculate(series, tradingRecord));
        MainAnchor.getChildren().add(viewer);
    }
}
