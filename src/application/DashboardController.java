package application;

import java.util.concurrent.TimeUnit;

import com.jfoenix.controls.JFXButton;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import org.json.JSONException;
import org.json.JSONObject;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXComboBox;
import javafx.event.EventHandler;
import com.jfoenix.controls.JFXRadioButton;
import com.jfoenix.controls.JFXTextField;

import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;

public class DashboardController {
    private final ObservableList<Person> data
    = FXCollections.observableArrayList(
            new Person("Jacob", "Smith"),
            new Person("Isabella", "Johnson"),
            new Person("Ethan", "Williams"),
            new Person("Emma", "Jones"),
            new Person("Michael", "Brown")
    );
	 @FXML private JFXButton tablee;
	 @FXML private JFXButton tableUpdate;
	 @FXML private TableView<Person> tableView = new TableView<Person>();
	
	@FXML
    public void initialize(){
        System.out.println("TEST!ASDADSA");
        tableEnable();
    }

    @SuppressWarnings("unchecked")
	@FXML
	 private void tableEnable() {
		System.out.println("TABLE2!");
		
		TableColumn<Person, String> firstNameCol = new TableColumn<Person, String>("First Name");
        firstNameCol.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        
        TableColumn<Person, String> lastNameCol = new TableColumn<Person, String>("Last Name");
        lastNameCol.setCellValueFactory(new PropertyValueFactory<>("lastName"));

        TableColumn<Person, String> actionCol = new TableColumn<Person, String>("Action");
        Callback<TableColumn<Person, String>, TableCell<Person, String>> cellFactory
                = //
                new Callback<TableColumn<Person, String>, TableCell<Person, String>>() {
            @Override
            public TableCell<Person, String> call(final TableColumn<Person, String> param) {
                final TableCell<Person, String> cell = new TableCell<Person, String>() {
                    final Button btn = new Button("Just Do It");
                    @Override
                   
                    public void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                            setText(null);
                        } else {
                            btn.setOnAction(event -> {
                                Person person = getTableView().getItems().get(getIndex());
                                
                                System.out.println(person.getFirstName()
                                        + "   " + person.getLastName());
                            });
                            setGraphic(btn);
                            setText(null);
                        }
                    }
                };
                return cell;
            }
        };
        actionCol.setCellFactory(cellFactory);
        System.out.println("TABLE30!");
        
        tableView.setItems(data);
        tableView.getColumns().addAll(firstNameCol, lastNameCol, actionCol);
	}
    
	@FXML
	private void tableUpdate() {
		final ObservableList<Person> data2
        = FXCollections.observableArrayList(
                new Person("Jacob", "Smith"),
                new Person("Isabella", "Smith"),
                new Person("Ethan", "Smith"),
                new Person("Michael", "BroSmithSmithwn")
        );
		tableView.setItems(data2);
		tableView.refresh();
	}
    public static class Person {

        private final SimpleStringProperty firstName;
        private final SimpleStringProperty lastName;
        private Person(String fName, String lName) {
            this.firstName = new SimpleStringProperty(fName);
            this.lastName = new SimpleStringProperty(lName);
        }

        public String getFirstName() {
            return firstName.get();
        }

        public void setFirstName(String fName) {
            firstName.set(fName);
        }

        public String getLastName() {
            return lastName.get();
        }

        public void setLastName(String fName) {
            lastName.set(fName);
        }

    }
}
