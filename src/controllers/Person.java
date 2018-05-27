package controllers;

public class Person {
    
    private String fullName;
    private String Indicator1;
    private String Indicator2;
    private String TradingRule;
    private boolean single;
 
    public Person(String fullName, String Indicator1, boolean single, String Indicator2, String TradingRule) {
        this.fullName = fullName;
        this.Indicator1 = Indicator1;
        this.single = single;
        this.Indicator2 = Indicator2;
        this.TradingRule = TradingRule;
    }
 
    public String getFullName() {
        return fullName;
    }
 
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
 
    public String getIndicator1() {
        return Indicator1;
    }
    
    public String getIndicator2() {
        return Indicator2;
    }
 
 
    public void setIndicator1(String Indicator1) {
        this.Indicator1 = Indicator1;
    }

    public void setIndicator2(String Indicator2) {
        this.Indicator2 = Indicator2;
    }
    
    
    
    public String getTradingRule() {
        return TradingRule;
    }
    public void setTradingRule(String TradingRule) {
        this.TradingRule = TradingRule;
    }

    public boolean isSingle() {
        return single;
    }
 
    public void setSingle(boolean single) {
        this.single = single;
    }
}