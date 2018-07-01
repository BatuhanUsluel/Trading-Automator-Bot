package controllers;

public enum Indicators {
	 
		Select("Select","Select")
		,AccelerationDecelerationIndicator("Accel", "AccelerationDecelerationIndicator")
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
		, EMAIndicator("EMA", "EMAIndicator")
		, FisherIndicator("FI", "FisherIndicator")
		, HMAIndicator("HMA", "HMAIndicator")
		, KAMAIndicator("KAMA", "KAMAIndicator")
		, MACDIndicator("MACD", "MACDIndicator")
		, MMAIndicator("MMA", "MMAIndicator")
		, ParabolicSarIndicator("PSI", "ParabolicSarIndicator")
		, PPOIndicator("PPO", "PPOIndicator")
		, RandomWalkIndexHighIndicator("RWIHI", "RandomWalkIndexHighIndicator")
		, RandomWalkIndexLowIndicator("RWILI", "RandomWalkIndexLowIndicator")
		, RAVIIndicator("RAVI", "RAVIIndicator")
		, ROCIndicator("ROCI", "ROCIndicator")
		, RSIIndicator("RSI", "RSIIndicator")
		, SMAIndicator("SMA", "SMAIndicator")
		, StochasticOscillatorDIndicator("SODI", "StochasticOscillatorDIndicator")
		, StochasticOscillatorKIndicator("SOKI", "StochasticOscillatorKIndicator")
		, StochasticRSIIndicator("SRI", "StochasticRSIIndicator")
		, TripleEMAIndicator("TEI", "TripleEMAIndicator")
		, UlcerIndexIndicator("UII", "UlcerIndexIndicator")
		, WilliamsRIndicator("WRI", "WilliamsRIndicator")
		, WMAIndicator("WMA", "WMAIndicator")
		, ZLEMAIndicator("ZLEMA", "ZLEMAIndicator")
		, BollingerBandsLowerIndicator("BBLI", "BollingerBandsLowerIndicator");
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