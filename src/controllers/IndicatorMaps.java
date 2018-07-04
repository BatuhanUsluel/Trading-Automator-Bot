package controllers;

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
		indicatorparameters.put("ADXIndicator", new String[]{"series","diTimeFrame", "adxTimeFrame"});
		indicatorparameters.put("MinusDIIndicator", new String[]{"series","timeFrame"});
		indicatorparameters.put("PlusDIIndicator", new String[]{"series","timeFrame"});
		
		indicatorparameters.put("BollingerBandsLowerIndicator", new String[]{"BollingerBandsMiddleIndicator","closeprice" , "K multiplier"}); //K-Decimal
		indicatorparameters.put("BollingerBandsMiddleIndicator", new String[]{"SMAIndicator", "timeFrame"});
		indicatorparameters.put("PlusDIIndicator", new String[]{"series","timeFrame"});
		indicatorparameters.put("PlusDIIndicator", new String[]{"series","timeFrame"});
		indicatorparameters.put("PlusDIIndicator", new String[]{"series","timeFrame"});
		indicatorclasspaths.put("AccelerationDecelerationIndicator", "org.ta4j.core.indicators.AccelerationDecelerationIndicator");
		indicatorclasspaths.put("AroonDownIndicator", "org.ta4j.core.indicators.AroonDownIndicator");
		indicatorclasspaths.put("AroonOscillatorIndicator", "org.ta4j.core.indicators.AroonOscillatorIndicator");
		indicatorclasspaths.put("AroonUpIndicator", "org.ta4j.core.indicators.AroonUpIndicator");
		indicatorclasspaths.put("AroonUpIndicator", "org.ta4j.core.indicators.ATRIndicator");
		indicatorclasspaths.put("AwesomeOscillatorIndicator", "org.ta4j.core.indicators.AwesomeOscillatorIndicator");
		indicatorclasspaths.put("CCIIndicator", "org.ta4j.core.indicators.CCIIndicator");
		indicatorclasspaths.put("ChandelierExitLongIndicator", "org.ta4j.core.indicators.ChandelierExitLongIndicator");
		indicatorclasspaths.put("ChandelierExitShortIndicator", "org.ta4j.core.indicators.ChandelierExitShortIndicator");
		indicatorclasspaths.put("CMOIndicator", "org.ta4j.core.indicators.CMOIndicator");
		indicatorclasspaths.put("CoppockCurveIndicator", "org.ta4j.core.indicators.CoppockCurveIndicator");
		indicatorclasspaths.put("DoubleEMAIndicator", "org.ta4j.core.indicators.DoubleEMAIndicator");
		indicatorclasspaths.put("DPOIndicator", "org.ta4j.core.indicators.DPOIndicator");
		indicatorclasspaths.put("EMAIndicator", "org.ta4j.core.indicators.EMAIndicator");
		indicatorclasspaths.put("FisherIndicator", "org.ta4j.core.indicators.FisherIndicator");
		indicatorclasspaths.put("HMAIndicator", "org.ta4j.core.indicators.HMAIndicator");
		indicatorclasspaths.put("KAMAIndicator", "org.ta4j.core.indicators.KAMAIndicator");
		indicatorclasspaths.put("MACDIndicator", "org.ta4j.core.indicators.MACDIndicator");
		indicatorclasspaths.put("MMAIndicator", "org.ta4j.core.indicators.MMAIndicator");
		indicatorclasspaths.put("ParabolicSarIndicator", "org.ta4j.core.indicators.ParabolicSarIndicator");
		indicatorclasspaths.put("PPOIndicator", "org.ta4j.core.indicators.PPOIndicator");
		indicatorclasspaths.put("RandomWalkIndexHighIndicator", "org.ta4j.core.indicators.RandomWalkIndexHighIndicator");
		indicatorclasspaths.put("RandomWalkIndexLowIndicator", "org.ta4j.core.indicators.RandomWalkIndexLowIndicator");
		indicatorclasspaths.put("RAVIIndicator", "org.ta4j.core.indicators.RAVIIndicator");
		indicatorclasspaths.put("ROCIndicator", "org.ta4j.core.indicators.ROCIndicator");
		indicatorclasspaths.put("ROCIndicator", "org.ta4j.core.indicators.ROCIndicator");
		indicatorclasspaths.put("RSIIndicator", "org.ta4j.core.indicators.RSIIndicator");
		indicatorclasspaths.put("SMAIndicator", "org.ta4j.core.indicators.SMAIndicator");
		indicatorclasspaths.put("StochasticOscillatorDIndicator", "org.ta4j.core.indicators.StochasticOscillatorDIndicator");
		indicatorclasspaths.put("StochasticOscillatorKIndicator", "org.ta4j.core.indicators.StochasticOscillatorKIndicator");
		indicatorclasspaths.put("StochasticRSIIndicator", "org.ta4j.core.indicators.StochasticRSIIndicator");
		indicatorclasspaths.put("TripleEMAIndicator", "org.ta4j.core.indicators.TripleEMAIndicator");
		indicatorclasspaths.put("UlcerIndexIndicator", "org.ta4j.core.indicators.UlcerIndexIndicator");
		indicatorclasspaths.put("WilliamsRIndicator", "org.ta4j.core.indicators.WilliamsRIndicator");
		indicatorclasspaths.put("WMAIndicator", "org.ta4j.core.indicators.WMAIndicator");
		indicatorclasspaths.put("ZLEMAIndicator", "org.ta4j.core.indicators.ZLEMAIndicator");
    }
}
