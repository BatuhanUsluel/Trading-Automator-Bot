package controllers;

public class Person {
    
    private String Indicator1;
    private String Indicator2;
    private String TradingRule;
    private boolean or;
 
    public Person(String Indicator1, boolean or, String Indicator2, String TradingRule) {
        this.Indicator1 = Indicator1;
        this.or = or;
        this.Indicator2 = Indicator2;
        this.TradingRule = TradingRule;
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

    public boolean isor() {
        return or;
    }
 
    public void setor(boolean single) {
        this.or = single;
    }
}