package controllers;

import java.text.DecimalFormat;

import org.jfree.chart.fx.ChartViewer;
import org.ta4j.core.AnalysisCriterion;
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
import com.jfoenix.controls.JFXTextField;

import javafx.embed.swing.SwingNode;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;

public class BacktestResultsController {

    @FXML
    private BorderPane borderpane;
    @FXML
    private AnchorPane anchorpane;
    @FXML
    private Label tp;
    @FXML
    private Label nt;
    @FXML
    private Label ptr;
    @FXML
    private Label md;
    @FXML
    private Label rrr;
    @FXML
    private Label bh;
    @FXML
    private Label svbhp;
    @FXML
    private Label tpl;

    @FXML
    private Label ntl;
    @FXML
    private Label ptrl;
    @FXML
    private Label mdl;
    @FXML
    private Label rrrl;
    @FXML
    private Label bhl;
    @FXML
    private Label svbhpl;
    
    @FXML
    public void initialize(){
    	ChartViewer viewer = new ChartViewer(BacktestController.chart);
    	viewer.setScaleShape(true);
    	AnchorPane.setTopAnchor(viewer, 0.0);
        AnchorPane.setRightAnchor(viewer, 0.0);
        AnchorPane.setLeftAnchor(viewer, 0.0);
        AnchorPane.setBottomAnchor(viewer, 0.0);
        // Total profit
        DecimalFormat df2 = new DecimalFormat(".###");
        TimeSeries series = BacktestController.series;
        TradingRecord tradingRecord = BacktestController.tradingRecord;
        TotalProfitCriterion totalProfit = new TotalProfitCriterion();
        tp.setText(df2.format(totalProfit.calculate(series, tradingRecord)));
        AnchorPane.setRightAnchor(tp,725.0);
        AnchorPane.setRightAnchor(tpl,896.0);
        nt.setText(df2.format(new NumberOfTradesCriterion().calculate(series, tradingRecord)));
        AnchorPane.setRightAnchor(nt,540.0);
        AnchorPane.setRightAnchor(ntl,590.0);
        ptr.setText(df2.format(new AverageProfitableTradesCriterion().calculate(series, tradingRecord)));
        AnchorPane.setRightAnchor(ptr,240.0);
        AnchorPane.setRightAnchor(ptrl,310.0);
        md.setText(df2.format(new MaximumDrawdownCriterion().calculate(series, tradingRecord)));
        AnchorPane.setRightAnchor(md,10.0);
        AnchorPane.setRightAnchor(mdl,80.0);
        rrr.setText(df2.format(new RewardRiskRatioCriterion().calculate(series, tradingRecord)));
        AnchorPane.setRightAnchor(rrr,665.0);
        AnchorPane.setRightAnchor(rrr,730.0);
        bh.setText(df2.format(new BuyAndHoldCriterion().calculate(series, tradingRecord)));
        AnchorPane.setRightAnchor(bh,456.0);
        AnchorPane.setRightAnchor(bhl,510.0);
        svbhp.setText(df2.format(new VersusBuyAndHoldCriterion(totalProfit).calculate(series, tradingRecord)));
        AnchorPane.setRightAnchor(svbhp,10.0);
        AnchorPane.setRightAnchor(svbhp,80.0);
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
        anchorpane.getChildren().add(viewer);
    }
}
