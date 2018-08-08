package controllers;

import java.text.DecimalFormat;

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

import com.jfoenix.controls.JFXButton;

import application.LiveTrading;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

public class LiveTradingShowMenu {
	  @FXML
	    private BorderPane borderpane;

	    @FXML
	    private AnchorPane anchorpane;

	    @FXML
	    private HBox hbox1;

	    @FXML
	    private Label tpl;

	    @FXML
	    private Label tp;

	    @FXML
	    private Label ntl;

	    @FXML
	    private Label nt;

	    @FXML
	    private Label ptrl;

	    @FXML
	    private Label ptr;

	    @FXML
	    private Label mdl;

	    @FXML
	    private Label md;

	    @FXML
	    private HBox hbox2;

	    @FXML
	    private Label rrrl;

	    @FXML
	    private Label rrr;

	    @FXML
	    private Label bhl;

	    @FXML
	    private Label bh;

	    @FXML
	    private Label svbhpl;

	    @FXML
	    private Label svbhp;

	    @FXML
	    private JFXButton stoporder;
	    
	    public void initialize(){
	    	ChartViewer viewer = new ChartViewer(LiveTrading.chart);
	    	viewer.setScaleShape(true);
	    	AnchorPane.setTopAnchor(viewer, 0.0);
	        AnchorPane.setRightAnchor(viewer, 0.0);
	        AnchorPane.setLeftAnchor(viewer, 0.0);
	        AnchorPane.setBottomAnchor(viewer, 0.0);
	        // Total profit
	        DecimalFormat df2 = new DecimalFormat(".###");
	        TimeSeries series = LiveTrading.series;
	        TradingRecord tradingRecord = LiveTrading.tradingRecord;
	        TotalProfitCriterion totalProfit = new TotalProfitCriterion();
	        tp.setText(df2.format(totalProfit.calculate(series, tradingRecord)));
	        //AnchorPane.setRightAnchor(tp,725.0);
	        //AnchorPane.setRightAnchor(tpl,896.0);
	        nt.setText(df2.format(new NumberOfTradesCriterion().calculate(series, tradingRecord)));
	        //AnchorPane.setRightAnchor(nt,540.0);
	        //AnchorPane.setRightAnchor(ntl,590.0);
	        ptr.setText(df2.format(new AverageProfitableTradesCriterion().calculate(series, tradingRecord)));
	        //AnchorPane.setRightAnchor(ptr,240.0);
	        //AnchorPane.setRightAnchor(ptrl,310.0);
	        md.setText(df2.format(new MaximumDrawdownCriterion().calculate(series, tradingRecord)));
	        //AnchorPane.setRightAnchor(md,10.0);
	        //AnchorPane.setRightAnchor(mdl,80.0);
	        rrr.setText(df2.format(new RewardRiskRatioCriterion().calculate(series, tradingRecord)));
	        //AnchorPane.setRightAnchor(rrr,665.0);
	        //AnchorPane.setRightAnchor(rrr,730.0);
	        bh.setText(df2.format(new BuyAndHoldCriterion().calculate(series, tradingRecord)));
	        //AnchorPane.setRightAnchor(bh,456.0);
	        //AnchorPane.setRightAnchor(bhl,510.0);
	        svbhp.setText(df2.format(new VersusBuyAndHoldCriterion(totalProfit).calculate(series, tradingRecord)));
	        //AnchorPane.setRightAnchor(svbhp,10.0);
	        //AnchorPane.setRightAnchor(svbhp,80.0);
	        hbox1.setSpacing(5.0);
	        hbox2.setSpacing(5.0);
	        
	        HBox.setHgrow(tp, Priority.ALWAYS);
	        HBox.setHgrow(nt, Priority.ALWAYS);
	        HBox.setHgrow(ptr, Priority.ALWAYS);
	        HBox.setHgrow(md, Priority.ALWAYS);
	        HBox.setHgrow(rrr, Priority.ALWAYS);
	        HBox.setHgrow(bh, Priority.ALWAYS);
	        HBox.setHgrow(svbhp, Priority.ALWAYS);
	        
	        tp.setMaxWidth(Double.MAX_VALUE);
	        nt.setMaxWidth(Double.MAX_VALUE);
	        ptr.setMaxWidth(Double.MAX_VALUE);
	        md.setMaxWidth(Double.MAX_VALUE);
	        rrr.setMaxWidth(Double.MAX_VALUE);
	        bh.setMaxWidth(Double.MAX_VALUE);
	        svbhp.setMaxWidth(Double.MAX_VALUE);
	        
	        tpl.setMaxWidth(Double.MAX_VALUE);
	        ntl.setMaxWidth(Double.MAX_VALUE);
	        ptrl.setMaxWidth(Double.MAX_VALUE);
	        mdl.setMaxWidth(Double.MAX_VALUE);
	        rrrl.setMaxWidth(Double.MAX_VALUE);
	        bhl.setMaxWidth(Double.MAX_VALUE);
	        svbhpl.setMaxWidth(Double.MAX_VALUE);
	        
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
