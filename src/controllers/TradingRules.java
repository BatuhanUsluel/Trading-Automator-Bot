package controllers;

public enum TradingRules {
	CrossedDownIndicatorRule("CDIR", "CrossedDownIndicatorRule")
	, CrossedUpIndicatorRule("CUIR", "CrossedUpIndicatorRule")
	, OverIndicatorRule("OIR","OverIndicatorRule")
	, UnderIndicatorRule("UIR","UnderIndicatorRule")
	, IsEqualRule("IER","IsEqualRule");
	
	   private String code;
	   private String text;
	 
	   private TradingRules(String code, String text) {
	       this.code = code;
	       this.text = text;
	   }
	   public String getCode() {
	       return code;
	   }
	 
	   public String getText() {
	       return text;
	   }
	 
	   public static TradingRules getByCode(String TradingRulesCode) {
	       for (TradingRules g : TradingRules.values()) {
	           if (g.code.equals(TradingRulesCode)) {
	               return g;
	           }
	       }
	       return null;
	   }
	 
	   @Override
	   public String toString() {
	       return this.text;
	   }
}
