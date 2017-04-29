/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXSlider;
import com.jfoenix.controls.JFXTextField;
import static com.sun.org.apache.xalan.internal.lib.ExsltDatetime.date;
import static com.sun.org.apache.xalan.internal.lib.ExsltDatetime.date;
import static com.sun.org.apache.xalan.internal.lib.ExsltDatetime.date;
import java.io.IOException;
import java.net.URL;
import java.sql.CallableStatement;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.HashMap;
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
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Hyperlink;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Callback;
import oracle.jdbc.OracleTypes;

/**
 * FXML Controller class
 *
 * @author vips
 */
public class HomePageWindowController implements Initializable {

    @FXML
    private AnchorPane Plane;
    @FXML
    private ComboBox<String> From_Combo_Box;
    @FXML
    private ComboBox<String> To_Combo_Box;
    @FXML
    private ComboBox<String> Class_Combo_Box;
    @FXML
    private JFXButton Submit_Quick_Book;
    @FXML
    private JFXDatePicker Date_Field;
    @FXML
    private JFXComboBox<Integer> No_Of_Passenger_Field;
    @FXML
    private Hyperlink Booking_History_Link;
    @FXML
    private Hyperlink Cancel_Ticket_Link;
    @FXML
    private JFXButton LogOut_Botton;
    @FXML
    private JFXButton My_Profile_Button;
    @FXML
    private Text Warning_Text;
    @FXML
    private JFXButton Check_Status;
    @FXML
    private Text durationText;

    @FXML
    private Text statusText;
    @FXML
    private JFXTextField Booking_Id_Flight_Status;
    public static String From_value = "", To_value = "", Class;
    public static Date date;
    public static int day;
    public static int passenger;
    ObservableList<String> from = FXCollections.observableArrayList("Dubai", "Jaipur", "Ahmedabad", "Delhi", "Mumbai", "Kolkata", "Banglore", "Hyderabad", "Chennai", "Pune");
    ObservableList<String> classs = FXCollections.observableArrayList("Business", "Economy");
    ObservableList<Integer> passengers = FXCollections.observableArrayList(1, 2, 3, 4, 5);

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        
         Date_Field.getChronology().dateNow();
        Date_Field.setValue(LocalDate.now());
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
        Date_Field.setDayCellFactory(dayCellFactory);
        
        From_Combo_Box.setItems(from);
        To_Combo_Box.setItems(from);
        Class_Combo_Box.setItems(classs);
        No_Of_Passenger_Field.setItems(passengers);

        From_Combo_Box.setOnAction((event) -> {

            From_value = (String) From_Combo_Box.getSelectionModel().getSelectedItem();
            if (To_value.equals(From_value)) {
                Warning_Text.setText("Source and Destination are same!!");
            } else {
                Warning_Text.setText("");
            }

        });
        To_Combo_Box.setOnAction((event) -> {

            To_value = (String) To_Combo_Box.getSelectionModel().getSelectedItem();
            if (To_value.equals(From_value)) {
                Warning_Text.setText("Source and Destination are same!!");
            } else {
                Warning_Text.setText("");
            }
        });
        Class_Combo_Box.setOnAction((event) -> {

            Class = (String) Class_Combo_Box.getSelectionModel().getSelectedItem();
        });

        No_Of_Passenger_Field.setOnAction((event) -> {

            passenger = (Integer) No_Of_Passenger_Field.getSelectionModel().getSelectedItem();
        });

        Check_Status.setOnAction(new EventHandler<ActionEvent>() {

            public void handle(ActionEvent event) {
                String pnr = Booking_Id_Flight_Status.getText();
                try {
                    String quer = "begin check_pnr_status(?,?,?);end;";
                    CallableStatement s3;
                    s3 = test.con.prepareCall(quer);
                    s3.setString(1, pnr);
                    s3.registerOutParameter(2, OracleTypes.VARCHAR);
                    s3.registerOutParameter(3, OracleTypes.VARCHAR);
                    System.out.println();
                    s3.executeUpdate();

                    String status = s3.getString(2);
                    String delay = s3.getString(3);

                    statusText.setText(status);
                    if (status.compareTo("DELAYED") == 0) {
                        durationText.setText(delay);
                    }

                } catch (SQLException ex) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Invali PNR");
                    alert.setHeaderText("Invalid PNR");
                    alert.setContentText("Please input valid pnr");
                                        alert.showAndWait();

                }

            }
        });
        My_Profile_Button.setOnAction(new EventHandler<ActionEvent>() {

            public void handle(ActionEvent event) {

                try {

                    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("MyProfile.fxml"));
                    Parent root1;

                    root1 = (Parent) fxmlLoader.load();

                    root1.setId("paneMyProfile");
                    Stage stage4 = new Stage();
                    stage4.resizableProperty().setValue(Boolean.FALSE);
                    stage4.setTitle("My Profile");
                    Scene scene = new Scene(root1);
                    scene.getStylesheets().addAll(this.getClass().getResource("MyProfile.css").toExternalForm());
                    stage4.setScene(scene);
                    stage4.show();
                    Stage stage5;
                    stage5 = (Stage) My_Profile_Button.getScene().getWindow();
                    stage5.close();

                } catch (IOException ex) {
                    Logger.getLogger(HomePageWindowController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

        Submit_Quick_Book.setOnAction(new EventHandler<ActionEvent>() {

            public void handle(ActionEvent event) {
                date = java.sql.Date.valueOf(Date_Field.getValue());
                Calendar c = Calendar.getInstance();
                c.setTime(date);
                day = c.get(Calendar.DAY_OF_WEEK);

                System.out.println(From_value);
                System.out.println(To_value);
                System.out.println(date);
                System.out.println(day);
                System.out.println(passenger);
                System.out.println(Class);
                try {

                    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("TicketAvailabilityWindow.fxml"));
                    Parent root1;

                    root1 = (Parent) fxmlLoader.load();

                    root1.setId("paneTicketAvailability");
                    Stage stage4 = new Stage();
                    stage4.resizableProperty().setValue(Boolean.FALSE);
                    stage4.setTitle("Available Tickets");
                    Scene scene = new Scene(root1);
                    scene.getStylesheets().addAll(this.getClass().getResource("TicketAvailability.css").toExternalForm());
                    stage4.setScene(scene);
                    stage4.show();
                    Stage stage5;
                    stage5 = (Stage) My_Profile_Button.getScene().getWindow();
                    stage5.close();

                } catch (IOException ex) {
                    Logger.getLogger(HomePageWindowController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

        LogOut_Botton.setOnAction(new EventHandler<ActionEvent>() {

            public void handle(ActionEvent event) {

                try {
                    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("GeneralLoginWindow.fxml"));
                    Parent root1;

                    root1 = (Parent) fxmlLoader.load();

                    root1.setId("paneGeneralLogin");
                    Stage stage4 = new Stage();
                    stage4.resizableProperty().setValue(Boolean.FALSE);
                    stage4.setTitle("Login");
                    Scene scene = new Scene(root1);
                    scene.getStylesheets().addAll(this.getClass().getResource("GeneralLogin.css").toExternalForm());

                    stage4.setScene(scene);
                    stage4.show();
                    Stage stage5;
                    stage5 = (Stage) LogOut_Botton.getScene().getWindow();
                    stage5.close();

                } catch (IOException ex) {
                    Logger.getLogger(HomePageWindowController.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
        });
        Booking_History_Link.setOnAction(new EventHandler<ActionEvent>() {

            public void handle(ActionEvent event) {

                try {
                    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("BookingHistoryWindow.fxml"));
                    Parent root1;

                    root1 = (Parent) fxmlLoader.load();

                    root1.setId("paneBookingHistory");
                    Stage stage4 = new Stage();
                    stage4.resizableProperty().setValue(Boolean.FALSE);
                    stage4.setTitle("Booking History");
                    Scene scene = new Scene(root1);
                    scene.getStylesheets().addAll(this.getClass().getResource("BookingHistory.css").toExternalForm());

                    stage4.setScene(scene);
                    stage4.show();
                    Stage stage5;
                    stage5 = (Stage) LogOut_Botton.getScene().getWindow();
                    stage5.close();

                } catch (IOException ex) {
                    Logger.getLogger(HomePageWindowController.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
        });
        Cancel_Ticket_Link.setOnAction(new EventHandler<ActionEvent>() {

            public void handle(ActionEvent event) {

                try {
                    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Cancel_Ticket_Window.fxml"));
                    Parent root1;

                    root1 = (Parent) fxmlLoader.load();

                    root1.setId("paneCancelTicket");
                    Stage stage4 = new Stage();
                    stage4.resizableProperty().setValue(Boolean.FALSE);
                    stage4.setTitle("Cancel our Ticket");
                    Scene scene = new Scene(root1);
                    scene.getStylesheets().addAll(this.getClass().getResource("CancelTicket.css").toExternalForm());

                    stage4.setScene(scene);
                    stage4.show();
                    Stage stage5;
                    stage5 = (Stage) LogOut_Botton.getScene().getWindow();
                    stage5.close();

                } catch (IOException ex) {
                    Logger.getLogger(HomePageWindowController.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
        });

    }

    @FXML
    private void initialize(MouseEvent event) {
    }

}
