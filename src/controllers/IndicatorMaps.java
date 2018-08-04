package controllers;

import java.util.ArrayList;
import java.util.HashMap;

public class IndicatorMaps {
    public static HashMap<String, String[]> indicatorparameters = new HashMap<String, String[]>();
    public static HashMap<String, String> indicatorclasspaths = new HashMap<String, String>();
    public static HashMap<String, Integer> timeframes = new HashMap<String, Integer>();
    public static void addValues() {
    	timeframes.put("1m", 1);
		timeframes.put("5m", 5);
		timeframes.put("1h", 60);
		timeframes.put("4h", 240);
		timeframes.put("1d", 1440);
		timeframes.put("1w", 10080);
		
		indicatorparameters.put("DecimalValue",  new String[]{"Value"});
		indicatorparameters.put("BooleanValue",  new String[]{"True/False"});
		
		
		//Indicators
		indicatorparameters.put("AccelerationDecelerationIndicator",  new String[]{"series","timeFrameSma1" , "timeFrameSma2"});
		indicatorparameters.put("AroonDownIndicator",  new String[]{"series","timeFrame"});
		indicatorparameters.put("AroonOscillatorIndicator", new String[]{"series","timeFrame"});
		indicatorparameters.put("AroonUpIndicator", new String[]{"series","timeFrame"});
		indicatorparameters.put("ATRIndicator", new String[]{"series","timeFrame"});//
		indicatorparameters.put("AwesomeOscillatorIndicator", new String[]{"closeprice","timeFrameSma1", "timeFrameSma2"});
		indicatorparameters.put("CCIIndicator", new String[]{"series","timeFrame"});
		indicatorparameters.put("ChandelierExitLongIndicator", new String[]{"series","timeFrame","K multiplier"}); //K - Decimal
		indicatorparameters.put("ChandelierExitShortIndicator", new String[]{"series","timeFrame","K multiplier"}); //K - Decimal
		indicatorparameters.put("CMOIndicator", new String[]{"closeprice","timeFrame"});
		indicatorparameters.put("CoppockCurveIndicator", new String[]{"closeprice","longRoCTimeFrame", "shortRoCTimeFrame", "wmaTimeFrame"});
		indicatorparameters.put("DoubleEMAIndicator", new String[]{"closeprice","timeFrame"});
		indicatorparameters.put("DPOIndicator", new String[]{"closeprice","timeFrame"});
		indicatorparameters.put("EMAIndicator", new String[]{"closeprice","timeFrame"});
		indicatorparameters.put("FisherIndicator", new String[]{"MedianPriceIndicator","timeFrame", "alpha","beta"}); // a - Decimal, b - Decimal
		indicatorparameters.put("HMAIndicator", new String[]{"closeprice","timeFrame"});
		indicatorparameters.put("KAMAIndicator", new String[]{"closeprice","timeFrameEffectiveRatio", "timeFrameFast", "timeFrameSlow"});
		indicatorparameters.put("MACDIndicator", new String[]{"closeprice","shortTimeFrame", "longTimeFrame"});
		indicatorparameters.put("MassIndexIndicator", new String[]{"series","emaTimeFrame", "timeFrame"});
		indicatorparameters.put("ParabolicSarIndicator", new String[]{"series","Acceleration factor", "Max Acceleration", "Acceleration Increment"}); //Decimal,Decimal,Decimal
		indicatorparameters.put("PPOIndicator", new String[]{"closeprice","shortTimeFrame", "longTimeFrame"});
		indicatorparameters.put("RandomWalkIndexHighIndicator", new String[]{"series","timeFrame"});
		indicatorparameters.put("RandomWalkIndexLowIndicator", new String[]{"series","timeFrame"});
		indicatorparameters.put("RAVIIndicator", new String[]{"closeprice","shortSmaTimeFrame" , "longSmaTimeFrame"});
		indicatorparameters.put("ROCIndicator", new String[]{"closeprice","timeFrame"});
		indicatorparameters.put("RSIIndicator", new String[]{"closeprice","timeFrame"});
		indicatorparameters.put("SMAIndicator", new String[]{"closeprice","timeFrame"});
		indicatorparameters.put("StochasticOscillatorDIndicator", new String[]{"StochasticOscillatorKIndicator","series","timeFrame"});
		indicatorparameters.put("StochasticOscillatorKIndicator", new String[]{"series","timeFrame"});
		indicatorparameters.put("StochasticRSIIndicator", new String[]{"series","timeFrame"});
		indicatorparameters.put("TripleEMAIndicator", new String[]{"closeprice","timeFrame"});
		indicatorparameters.put("UlcerIndexIndicator", new String[]{"closeprice","timeFrame"});
		indicatorparameters.put("WilliamsRIndicator", new String[]{"series","timeFrame"});
		indicatorparameters.put("WMAIndicator", new String[]{"closeprice","timeFrame"});
		indicatorparameters.put("ZLEMAIndicator", new String[]{"closeprice","timeFrame"});
		
		//ADX
		indicatorparameters.put("ADXIndicator", new String[]{"series","diTimeFrame", "adxTimeFrame"});
		indicatorparameters.put("MinusDIIndicator", new String[]{"series","timeFrame"});
		indicatorparameters.put("PlusDIIndicator", new String[]{"series","timeFrame"});
		
		//Bollinger
		//BolingerLow
		//BolingerMiddle
		//BolingerUpper
		//BolingerWidth
		//BolingerPercent
		
		//Candles
		indicatorparameters.put("BearishEngulfingIndicator", new String[]{"series"}); //Return Boolean
		indicatorparameters.put("BearishHaramiIndicator", new String[]{"series"}); //Return Boolean
		indicatorparameters.put("BullishEngulfingIndicator", new String[]{"series"}); //Return Boolean
		indicatorparameters.put("BullishHaramiIndicator", new String[]{"series"}); //Return Boolean
		indicatorparameters.put("DojiIndicator", new String[]{"series","timeFrame","bodyFactor"}); //Return Boolean
		indicatorparameters.put("LowerShadowIndicator", new String[]{"series"}); //Normal Return
		indicatorparameters.put("RealBodyIndicator", new String[]{"series"}); //Normal Return
		indicatorparameters.put("ThreeBlackCrowsIndicator", new String[]{"series","timeFrame","factor"}); //Return Boolean
		indicatorparameters.put("ThreeWhiteSoldiersIndicator", new String[]{"series","timeFrame","factor"}); //Return Boolean
		indicatorparameters.put("UpperShadowIndicator", new String[]{"series"}); //Normal Return
		
		//Ichimoku
		indicatorparameters.put("IchimokuChikouSpanIndicator", new String[]{"series","timeDelay"});
		indicatorparameters.put("IchimokuKijunSenIndicator", new String[]{"series","timeFrame"});
		indicatorparameters.put("IchimokuSenkouSpanAIndicator", new String[]{"series","timeFrameConversionLine","timeFrameBaseLine"});
		indicatorparameters.put("IchimokuSenkouSpanBIndicator", new String[]{"series","timeFrame"});
		indicatorparameters.put("IchimokuTenkanSenIndicator", new String[]{"series","timeFrame"});
		
		//Keltner
		//Low
		//Middle
		//Upper
		
		//PivotPoints		
		indicatorparameters.put("DeMarkPivotPointIndicator", new String[]{"series","timeLevel"});
		indicatorparameters.put("DeMarkReversalIndicator", new String[]{"DeMarkPivotPointIndicator","DeMarkPivotLevel"});
		indicatorparameters.put("FibonacciReversalIndicator", new String[]{"pivotPointIndicator","fibonacciFactor","fibReversalTyp"});
		indicatorparameters.put("PivotPointIndicator", new String[]{"series","timeLevel"});
		indicatorparameters.put("StandardReversalIndicator", new String[]{"pivotPointIndicator","PivotLevel"});
		
		//Statistics
		//CorrelationCoefficientIndicator
		//CovarianceIndicator		
		indicatorparameters.put("MeanDeviationIndicator", new String[]{"closeprice","timeFrame"});
		//PearsonCorrelationIndicator
		//PeriodicalGrowthRateIndicator
		indicatorparameters.put("SigmaIndicator", new String[]{"closeprice","timeFrame"});
		indicatorparameters.put("SimpleLinearRegressionIndicator", new String[]{"closeprice","timeFrame","SimpleLinearRegressionType"});
		indicatorparameters.put("StandardDeviationIndicator", new String[]{"closeprice","timeFrame"});
		indicatorparameters.put("StandardErrorIndicator", new String[]{"closeprice","timeFrame"});
		indicatorparameters.put("VarianceIndicator", new String[]{"closeprice","timeFrame"});
		
		//Volume
		indicatorparameters.put("AccumulationDistributionIndicator", new String[]{"series"});
		indicatorparameters.put("ChaikinMoneyFlowIndicator", new String[]{"series","timeFrame"});
		indicatorparameters.put("ChaikinOscillatorIndicator", new String[]{"series","shortTimeFrame", "longTimeFrame"});
		indicatorparameters.put("IIIIndicator", new String[]{"series"});
		indicatorparameters.put("MVWAPIndicator", new String[]{"vwap","SmaTimeFrame"});
		indicatorparameters.put("NVIIndicator", new String[]{"series"});
		indicatorparameters.put("OnBalanceVolumeIndicator", new String[]{"series"});
		indicatorparameters.put("PVIIndicator", new String[]{"series"});
		indicatorparameters.put("ROCVIndicator", new String[]{"series","timeFrame"});
		indicatorparameters.put("VWAPIndicator", new String[]{"series","timeFrame"});
		
		
		//CLASS PATHS
		//Indicators
		indicatorclasspaths.put("AccelerationDecelerationIndicator", "AccelerationDecelerationIndicator");
		indicatorclasspaths.put("AroonDownIndicator", "AroonDownIndicator");
		indicatorclasspaths.put("AroonOscillatorIndicator", "AroonOscillatorIndicator");
		indicatorclasspaths.put("AroonUpIndicator", "AroonUpIndicator");
		indicatorclasspaths.put("AroonUpIndicator", "ATRIndicator");
		indicatorclasspaths.put("AwesomeOscillatorIndicator", "AwesomeOscillatorIndicator");
		indicatorclasspaths.put("CCIIndicator", "CCIIndicator");
		indicatorclasspaths.put("ChandelierExitLongIndicator", "ChandelierExitLongIndicator");
		indicatorclasspaths.put("ChandelierExitShortIndicator", "ChandelierExitShortIndicator");
		indicatorclasspaths.put("CMOIndicator", "CMOIndicator");
		indicatorclasspaths.put("CoppockCurveIndicator", "CoppockCurveIndicator");
		indicatorclasspaths.put("DoubleEMAIndicator", "DoubleEMAIndicator");
		indicatorclasspaths.put("DPOIndicator", "DPOIndicator");
		indicatorclasspaths.put("EMAIndicator", "EMAIndicator");
		indicatorclasspaths.put("FisherIndicator", "FisherIndicator");
		indicatorclasspaths.put("HMAIndicator", "HMAIndicator");
		indicatorclasspaths.put("KAMAIndicator", "KAMAIndicator");
		indicatorclasspaths.put("MACDIndicator", "MACDIndicator");
		indicatorclasspaths.put("MMAIndicator", "MMAIndicator");
		indicatorclasspaths.put("ParabolicSarIndicator", "ParabolicSarIndicator");
		indicatorclasspaths.put("PPOIndicator", "PPOIndicator");
		indicatorclasspaths.put("RandomWalkIndexHighIndicator", "RandomWalkIndexHighIndicator");
		indicatorclasspaths.put("RandomWalkIndexLowIndicator", "RandomWalkIndexLowIndicator");
		indicatorclasspaths.put("RAVIIndicator", "RAVIIndicator");
		indicatorclasspaths.put("ROCIndicator", "ROCIndicator");
		indicatorclasspaths.put("ROCIndicator", "ROCIndicator");
		indicatorclasspaths.put("RSIIndicator", "RSIIndicator");
		indicatorclasspaths.put("SMAIndicator", "SMAIndicator");
		indicatorclasspaths.put("StochasticOscillatorDIndicator", "StochasticOscillatorDIndicator");
		indicatorclasspaths.put("StochasticOscillatorKIndicator", "StochasticOscillatorKIndicator");
		indicatorclasspaths.put("StochasticRSIIndicator", "StochasticRSIIndicator");
		indicatorclasspaths.put("TripleEMAIndicator", "TripleEMAIndicator");
		indicatorclasspaths.put("UlcerIndexIndicator", "UlcerIndexIndicator");
		indicatorclasspaths.put("WilliamsRIndicator", "WilliamsRIndicator");
		indicatorclasspaths.put("WMAIndicator", "WMAIndicator");
		indicatorclasspaths.put("ZLEMAIndicator", "ZLEMAIndicator");
		
		//ADX
		indicatorclasspaths.put("ADXIndicator", "adx.ADXIndicator");
		indicatorclasspaths.put("MinusDIIndicator", "adx.MinusDIIndicator");
		indicatorclasspaths.put("PlusDIIndicator", "adx.PlusDIIndicator");
		
		//Bolingers
		//
		//
		//
		
		//Candles
		indicatorclasspaths.put("BearishEngulfingIndicator", "candles.BearishEngulfingIndicator");
		indicatorclasspaths.put("BearishHaramiIndicator", "candles.BearishHaramiIndicator");
		indicatorclasspaths.put("BullishEngulfingIndicator", "candles.BullishEngulfingIndicator");
		indicatorclasspaths.put("BullishHaramiIndicator", "candles.BullishHaramiIndicator");
		indicatorclasspaths.put("DojiIndicator", "candles.DojiIndicator");
		indicatorclasspaths.put("LowerShadowIndicator", "candles.LowerShadowIndicator");
		indicatorclasspaths.put("RealBodyIndicator", "candles.RealBodyIndicator");
		indicatorclasspaths.put("ThreeBlackCrowsIndicator", "candles.ThreeBlackCrowsIndicator");
		indicatorclasspaths.put("ThreeWhiteSoldiersIndicator", "candles.ThreeWhiteSoldiersIndicator");
		indicatorclasspaths.put("UpperShadowIndicator", "candles.UpperShadowIndicator");
				
		//Ichimoku
		indicatorclasspaths.put("IchimokuChikouSpanIndicator", "ichimoku.IchimokuChikouSpanIndicator");
		indicatorclasspaths.put("IchimokuKijunSenIndicator", "ichimoku.IchimokuKijunSenIndicator");
		indicatorclasspaths.put("IchimokuSenkouSpanAIndicator", "ichimoku.IchimokuSenkouSpanAIndicator");
		indicatorclasspaths.put("IchimokuSenkouSpanBIndicator", "ichimoku.IchimokuSenkouSpanBIndicator");
		indicatorclasspaths.put("IchimokuTenkanSenIndicator", "ichimoku.IchimokuTenkanSenIndicator");
		
		//Keltner
		//Low
		//Middle
		//Upper
		
		//PivotPoints
		indicatorclasspaths.put("DeMarkPivotPointIndicator", "pivotpoints.DeMarkPivotPointIndicator");
		indicatorclasspaths.put("DeMarkReversalIndicator", "pivotpoints.DeMarkReversalIndicator");
		indicatorclasspaths.put("FibonacciReversalIndicator", "pivotpoints.FibonacciReversalIndicator");
		indicatorclasspaths.put("PivotPointIndicator", "pivotpoints.PivotPointIndicator");
		indicatorclasspaths.put("StandardReversalIndicator", "pivotpoints.StandardReversalIndicator");
		
		//Statistics
		//CorrelationCoefficientIndicator
		//CovarianceIndicator
		indicatorclasspaths.put("MeanDeviationIndicator", "statistics.MeanDeviationIndicator");
		//PearsonCorrelationIndicator
		//PeriodicalGrowthRateIndicator
		indicatorclasspaths.put("SigmaIndicator", "statistics.SigmaIndicator");
		indicatorclasspaths.put("SimpleLinearRegressionIndicator", "statistics.SimpleLinearRegressionIndicator");
		indicatorclasspaths.put("StandardDeviationIndicator", "statistics.StandardDeviationIndicator");
		indicatorclasspaths.put("StandardErrorIndicator", "statistics.StandardErrorIndicator");
		indicatorclasspaths.put("VarianceIndicator", "statistics.VarianceIndicator");

		//Volume
		indicatorclasspaths.put("AccumulationDistributionIndicator", "volume.AccumulationDistributionIndicator");
		indicatorclasspaths.put("ChaikinMoneyFlowIndicator", "volume.ChaikinMoneyFlowIndicator");
		indicatorclasspaths.put("ChaikinOscillatorIndicator", "volume.ChaikinOscillatorIndicator");
		indicatorclasspaths.put("IIIIndicator", "volume.IIIIndicator");
		indicatorclasspaths.put("MVWAPIndicator", "volume.MVWAPIndicator");
		indicatorclasspaths.put("NVIIndicator", "volume.NVIIndicator");
		indicatorclasspaths.put("OnBalanceVolumeIndicator", "volume.OnBalanceVolumeIndicator");
		indicatorclasspaths.put("PVIIndicator", "volume.PVIIndicator");
		indicatorclasspaths.put("ROCVIndicator", "volume.ROCVIndicator");
		indicatorclasspaths.put("VWAPIndicator", "volume.VWAPIndicator");
    }
}
