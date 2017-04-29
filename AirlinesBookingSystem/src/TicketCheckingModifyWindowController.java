/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXDatePicker;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Callback;

/**
 * FXML Controller class
 *
 * @author vips
 */
public class TicketCheckingModifyWindowController implements Initializable {

    @FXML
    private JFXDatePicker DateTextField;
    @FXML
    private JFXComboBox<String> Class_Combo;
    @FXML
    private JFXComboBox<String> From_Combo;
    @FXML
    private JFXComboBox<Integer> Passenger_Combo;
    @FXML
    private JFXComboBox<String> To_Combo;
    @FXML
    private JFXButton Apply;
       @FXML
    private Text Warning_Text;
    ObservableList<String> from = FXCollections.observableArrayList("Dubai", "Jaipur", "Ahmedabad", "Delhi", "Mumbai", "Kolkata", "Banglore", "Hyderabad", "Chennai", "Pune");
    ObservableList<String> classs = FXCollections.observableArrayList("Business", "Economy");
    ObservableList<Integer> passengers = FXCollections.observableArrayList(1, 2, 3, 4, 5);

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        DateTextField.getChronology().dateNow();
        DateTextField.setValue(LocalDate.now());
        //System.out.println(LocalDate.now());
        final Callback<DatePicker, DateCell> dayCellFactory
                = new Callback<DatePicker, DateCell>() {
                    @Override
                    public DateCell call(final DatePicker datePicker) {
                        return new DateCell() {
                            @Override
                            public void updateItem(LocalDate item, boolean empty) {
                                super.updateItem(item, empty);
                                if (item.isBefore(LocalDate.now()
                                        )) {
                                    //  System.out.println("hey");
                                    setDisable(true);
                                    setStyle("-fx-background-color: #ffc0cb;");
                                }
                            }
                        };
                    }
                };
        DateTextField.setDayCellFactory(dayCellFactory);
        
         From_Combo.setItems(from);
        To_Combo.setItems(from);
        Class_Combo.setItems(classs);
        Passenger_Combo.setItems(passengers);
        
        
        From_Combo.setOnAction((event) -> {

            HomePageWindowController.From_value = (String) From_Combo.getSelectionModel().getSelectedItem();
            if (HomePageWindowController.To_value.equals(HomePageWindowController.From_value)) {
                Warning_Text.setText("Source and Destination are same!!");
            } else {
                Warning_Text.setText("");
            }

        });
        To_Combo.setOnAction((event) -> {

            HomePageWindowController.To_value = (String) To_Combo.getSelectionModel().getSelectedItem();
            if (HomePageWindowController.To_value.equals(HomePageWindowController.From_value)) {
                Warning_Text.setText("Source and Destination are same!!");
            } else {
                Warning_Text.setText("");
            }
        });
        Class_Combo.setOnAction((event) -> {

            HomePageWindowController.Class = (String) Class_Combo.getSelectionModel().getSelectedItem();
        });

       
        Passenger_Combo.setOnAction((event) -> {

            HomePageWindowController.passenger = (Integer) Passenger_Combo.getSelectionModel().getSelectedItem();
        });
        
        
           
        Apply.setOnAction(new EventHandler<ActionEvent>() {

            public void handle(ActionEvent event) {
                HomePageWindowController.date = java.sql.Date.valueOf(DateTextField.getValue());
                Calendar c = Calendar.getInstance();
                c.setTime(HomePageWindowController.date);
                HomePageWindowController.day = c.get(Calendar.DAY_OF_WEEK);

                System.out.println(HomePageWindowController.From_value);
                System.out.println(HomePageWindowController.To_value);
                 System.out.println(HomePageWindowController.date);
                System.out.println(HomePageWindowController.day);
                System.out.println(HomePageWindowController.passenger);
                System.out.println(HomePageWindowController.Class);
                try {

                    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("TicketAvailabilityWindow.fxml"));
                    Parent root1;

                    root1 = (Parent) fxmlLoader.load();

                    root1.setId("paneTicketAvailability");
                    Stage stage4 = new Stage();
                    stage4.resizableProperty().setValue(Boolean.FALSE);
                    stage4.setTitle("Ticket Availability");
                    Scene scene = new Scene(root1);
                    scene.getStylesheets().addAll(this.getClass().getResource("TicketAvailability.css").toExternalForm());
                    stage4.setScene(scene);
                    stage4.show();
                    Stage stage5;
                    stage5 = (Stage) Apply.getScene().getWindow();
                    stage5.close();

                } catch (IOException ex) {
                    Logger.getLogger(HomePageWindowController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
       
        
        
        // TODO
    }    
    
}
