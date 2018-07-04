package controllers;

import org.ta4j.core.Indicator;

public class Person {
    
    private String Indicator1;
    private String Indicator2;
    private String TradingRule;
    private Indicator firstindicator;
    private Indicator secondindicator;
    private boolean or;
    private Object[] Indic1Param;
    private Object[] Indic2Param;
    public Person(String Indicator1, boolean or, String Indicator2, String TradingRule, Indicator firstindicator, Indicator secondindicator, Object[] Indic1Param, Object[] Indic2Param) {
        this.Indicator1 = Indicator1;
        this.or = or;
        this.Indicator2 = Indicator2;
        this.TradingRule = TradingRule;
        this.firstindicator = firstindicator;
        this.secondindicator = secondindicator;
        this.Indic1Param = Indic2Param;
        this.Indic2Param = Indic2Param;
    }

 
    public Indicator getfirstindicator() {
    	return firstindicator;
    }
    public Indicator getsecondindicator() {
    	return secondindicator;
    }
    public void setfirstindicator(Indicator firstindicator) {
    	this.firstindicator=firstindicator;
    }
    public void setsecondindicator(Indicator secondindicator) {
    	this.secondindicator=secondindicator;
    }
    
    public Object[] getIndic1Param() {
    	return Indic1Param;
    }
    public Object[] getIndic2Param() {
    	return Indic2Param;
    }
    public void setIndic1Param(Object[] Indic1Param) {
    	this.Indic1Param = Indic1Param;
    }
    public void setIndic2Param(Object[] Indic2Param) {
    	this.Indic2Param = Indic2Param;
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