package controllers;

import java.io.Serializable;
import java.util.List;

import javafx.collections.ObservableList;

public class FullStrategy implements Serializable {
	private Person[] entryrules;
	private Person[] exitrules;
	
	public FullStrategy(Person[] entryrulesarray, Person[] exitrulesarray) {
		this.entryrules = entryrulesarray;
        this.exitrules = exitrulesarray;
	}
	
	public Person[]getentryrules() {
		return entryrules;
	}
	 
    public Person[] getexitrules() {
    	return exitrules;
    }
    
    public void setentryrules(Person[] entryrules) {
    	this.entryrules = entryrules;
    }
    
    public void setexitrules(Person[] exitrules) {
    	this.exitrules = exitrules;
    }
    
}
