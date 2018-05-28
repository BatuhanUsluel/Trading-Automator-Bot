package controllers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXDatePicker;

import application.Main;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Separator;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

public class BacktestController {
    @FXML private TableView<?> BackEntryTable;
    @FXML private TableView<?> BackExitTable;
    @FXML private JFXButton Backtest;
    @FXML private JFXComboBox<?> BackExchange;
    @FXML private TextField BackBase;
    @FXML private TextField BackAlt;
    @FXML private TextField LiveBase;
    @FXML private JFXButton BackAddExitRow;
    @FXML private JFXButton BackAddEntryRow;
    @FXML private JFXDatePicker starttime;
    @FXML private JFXDatePicker endtime;

	@FXML
    public void initialize(){
		String css = this.getClass().getResource("/assets/datepicker.css").toExternalForm();
		Controller.scene.getStylesheets().add(css);;
    }
	
    @FXML
    void addEntryRow(ActionEvent event) {

    }

    @FXML
    void addExitRow(ActionEvent event) {

    }

}
