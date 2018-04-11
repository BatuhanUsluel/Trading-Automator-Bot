package controllers;

import org.controlsfx.control.CheckComboBox;

import application.Exchanges;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;

public class ArbitrageController {
    @FXML CheckComboBox<String> ArbitrageExchange;
	@FXML
    public void initialize(){
		 // create the data to show in the CheckComboBox 
		 final ObservableList<String> strings = FXCollections.observableArrayList();
		 for (int i = 0; i < Exchanges.list.size(); i++) {
		     strings.add(Exchanges.list.get(i));
		 }
		 System.out.println("Strings: " + strings);
		 // Create the CheckComboBox with the data 
		 ArbitrageExchange.getItems().addAll(strings);
		 // and listen to the relevant events (e.g. when the selected indices or 
		 // selected items change).
		 ArbitrageExchange.getCheckModel().getCheckedItems().addListener(new ListChangeListener<String>() {
		     public void onChanged(ListChangeListener.Change<? extends String> c) {
		         System.out.println(ArbitrageExchange.getCheckModel().getCheckedItems());
		     }
		 });
		 }
    }
