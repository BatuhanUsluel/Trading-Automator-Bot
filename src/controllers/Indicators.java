package controllers;

public enum Indicators {
	 
		Select("Select","Select")
		, SMAIndicator("SMA", "SMAIndicator")
		, MACDIndicator("MACD", "MACDIndicator")
		, EMAIndicator("EMA", "EMAIndicator")
		, RSIIndicator("RSI", "RSIIndicator")
		, ClosePriceIndicator("CP","ClosePriceIndicator")
		, VWAPIndicator("VWAP", "VWAPIndicator")
		, DecimalValue("Dec", "DecimalValue")
		, BooleanValue("Bool", "BooleanValue")
		, AccelerationDecelerationIndicator("Accel", "AccelerationDecelerationIndicator")
		, AroonDownIndicator("AroonDown", "AroonDownIndicator")
		, AroonOscillatorIndicator("AroonOscil","AroonOscillatorIndicator")
		, AroonUpIndicator("ArronUp", "AroonUpIndicator")
		, AwesomeOscillatorIndicator("AWS", "AwesomeOscillatorIndicator")
		, CCIIndicator("CCI", "CCIIndicator")
		, ChandelierExitLongIndicator("CELI", "ChandelierExitLongIndicator")
		, ChandelierExitShortIndicator("CESI", "ChandelierExitShortIndicator")
		, CMOIndicator("CMO", "CMOIndicator")
		, CoppockCurveIndicator("CoCI", "CoppockCurveIndicator")
		, DoubleEMAIndicator("DEI", "DoubleEMAIndicator")
		, DPOIndicator("DPO", "DPOIndicator")
		, FisherIndicator("FI", "FisherIndicator")
		, HMAIndicator("HMA", "HMAIndicator")
		, KAMAIndicator("KAMA", "KAMAIndicator")
		, ParabolicSarIndicator("PSI", "ParabolicSarIndicator")
		, PPOIndicator("PPO", "PPOIndicator")
		, RandomWalkIndexHighIndicator("RWIHI", "RandomWalkIndexHighIndicator")
		, RandomWalkIndexLowIndicator("RWILI", "RandomWalkIndexLowIndicator")
		, RAVIIndicator("RAVI", "RAVIIndicator")
		, ROCIndicator("ROCI", "ROCIndicator")
		, StochasticOscillatorDIndicator("SODI", "StochasticOscillatorDIndicator")
		, StochasticOscillatorKIndicator("SOKI", "StochasticOscillatorKIndicator")
		, StochasticRSIIndicator("SRI", "StochasticRSIIndicator")
		, TripleEMAIndicator("TEI", "TripleEMAIndicator")
		, UlcerIndexIndicator("UII", "UlcerIndexIndicator")
		, WilliamsRIndicator("WRI", "WilliamsRIndicator")
		, WMAIndicator("WMA", "WMAIndicator")
		, ZLEMAIndicator("ZLEMA", "ZLEMAIndicator")
		, ADXIndicator("ADX", "ADXIndicator")
		, MinusDIIndicator("MinusDI", "MinusDIIndicator")
		, PlusDIIndicator("PlusDI", "PlusDIIndicator")
		, BearishEngulfingIndicator("BearishEI", "BearishEngulfingIndicator")
		, BearishHaramiIndicator("BearishHI", "BearishHaramiIndicator")
		, BullishEngulfingIndicator("BullishEI", "BullishEngulfingIndicator")
		, BullishHaramiIndicator("BullishHI", "BullishHaramiIndicator")
		, DojiIndicator("Doji", "DojiIndicator")
		, LowerShadowIndicator("LSI", "LowerShadowIndicator")
		, RealBodyIndicator("RBI", "RealBodyIndicator")
		, ThreeBlackCrowsIndicator("TBCI", "ThreeBlackCrowsIndicator")
		, ThreeWhiteSoldiersIndicator("TWSI", "ThreeWhiteSoldiersIndicator")
		, UpperShadowIndicator("UpperSI", "UpperShadowIndicator")
		, IchimokuChikouSpanIndicator("IcCSI", "IchimokuChikouSpanIndicator")
		, IchimokuKijunSenIndicator("IcKSI", "IchimokuKijunSenIndicator")
		, IchimokuSenkouSpanAIndicator("IcSSA", "IchimokuSenkouSpanAIndicator")
		, IchimokuSenkouSpanBIndicator("IcSSB", "IchimokuSenkouSpanBIndicator")
		, IchimokuTenkanSenIndicator("IcTSI", "IchimokuTenkanSenIndicator")
		, DeMarkPivotPointIndicator("DMPPI", "DeMarkPivotPointIndicator")
		, DeMarkReversalIndicator("DMRI", "DeMarkReversalIndicator")
		, FibonacciReversalIndicator("FRI", "FibonacciReversalIndicator")
		, PivotPointIndicator("PivPI", "PivotPointIndicator")
		, StandardReversalIndicator("StaRI", "StandardReversalIndicator")
		, MeanDeviationIndicator("MeDI", "MeanDeviationIndicator")
		, SigmaIndicator("SigI", "SigmaIndicator")
		, SimpleLinearRegressionIndicator("SimLRI", "SimpleLinearRegressionIndicator")
		, StandardDeviationIndicator("StaDI", "StandardDeviationIndicator")
		, StandardErrorIndicator("StaEI", "StandardErrorIndicator")
		, VarianceIndicator("VarI", "VarianceIndicator")
		, AccumulationDistributionIndicator("AccumDI", "AccumulationDistributionIndicator")
		, ChaikinMoneyFlowIndicator("CMFI", "ChaikinMoneyFlowIndicator")
		, ChaikinOscillatorIndicator("COI", "ChaikinOscillatorIndicator")
		, IIIIndicator("IIII", "IIIIndicator")
		, MVWAPIndicator("MVWAP", "MVWAPIndicator")
		, NVIIndicator("NVI", "NVIIndicator")
		, OnBalanceVolumeIndicator("OBVI", "OnBalanceVolumeIndicator")
		, PVIIndicator("PVI", "PVIIndicator")
		, ROCVIndicator("ROCV", "ROCVIndicator");
		
	   private String code;
	   private String text;
	 
	   private Indicators(String code, String text) {
	       this.code = code;
	       this.text = text;
	   }
	 
	   public String getCode() {
	       return code;
	   }
	 
	   public String getText() {
	       return text;
	   }
	 
	   public static Indicators getByCode(String indicatorCode) {
	       for (Indicators g : Indicators.values()) {
	           if (g.code.equals(indicatorCode)) {
	               return g;
	           }
	       }
	       return null;
	   }
	 
	   public static String getByString(String indicatortext) {
		   for(Indicators g : Indicators.values()) {
			   if (g.text.equals(indicatortext)) {
				   return g.code;
			   }
		   }
		   return null;
	   }
	   @Override
	   public String toString() {
	       return this.text;
	   }
	 
	}