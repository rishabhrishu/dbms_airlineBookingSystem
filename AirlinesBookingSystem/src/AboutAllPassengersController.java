/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import java.net.URL;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.ResourceBundle;
import java.util.StringTokenizer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import oracle.jdbc.OracleTypes;
import tray.notification.NotificationType;
import tray.notification.TrayNotification;

/**
 * FXML Controller class
 *
 * @author RAKA
 */
public class AboutAllPassengersController implements Initializable {

    @FXML
    private JFXTextField Name1;
     @FXML
    private Text warning_Text;

    @FXML
    private JFXTextField Name2;
    @FXML
    private JFXTextField Name3;
    @FXML
    private JFXTextField Name4;
    @FXML
    private JFXTextField Name5;
    @FXML
    private JFXTextField Age1;
    @FXML
    private JFXTextField Age2;
    @FXML
    private JFXTextField Age3;
    @FXML
    private JFXTextField Age4;
    @FXML
    private JFXTextField Age5;
    @FXML
    private JFXComboBox<String> Gender1;
    @FXML
    private JFXComboBox<String> Gender2;
    @FXML
    private JFXComboBox<String> Gender3;
    @FXML
    private JFXComboBox<String> Gender4;
    @FXML
    private JFXComboBox<String> Gender5;
    @FXML
    private JFXButton BookTicketButton;
    @FXML
    private JFXButton BackButton;
    @FXML
    private Text AirlinesText;
    ObservableList<String> Genderr = FXCollections.observableArrayList("Male", "Female", "Others");

    @FXML
    private Text DateText;

    @FXML
    private Text NoOfPassengersText;

    @FXML
    private Text FareText;

    @FXML
    private Text FromText;

    @FXML
    private Text ToText;

    @FXML
    private Text DepartureText;

    @FXML
    private Text ArrivalText;
    String Gen1 = "No", Gen2 = "No", Gen3 = "No", Gen4 = "No", Gen5 = "No";
    public static String FlightId;
    public static String PNR;
    public static int FARE;

    /**
     *
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        AirlinesText.setText(TicketAvailabilityWindowController.neww.getA());
        ArrivalText.setText(TicketAvailabilityWindowController.neww.getC());
        DepartureText.setText(TicketAvailabilityWindowController.neww.getB());
        FareText.setText(TicketAvailabilityWindowController.neww.getD());
        FromText.setText(HomePageWindowController.From_value);
        ToText.setText(HomePageWindowController.To_value);

        Name1.setText(null);
        Name2.setText(null);
        Name3.setText(null);
        Name4.setText(null);
        Name5.setText("");
        Age1.setText("-1");
        Age2.setText("-1");
        Age3.setText("-1");
        Age4.setText("-1");
        Age5.setText("-1");

        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

        String datestring = dateFormat.format(HomePageWindowController.date);
        DateText.setText(datestring);
        NoOfPassengersText.setText(Integer.toString(HomePageWindowController.passenger));
        Gender1.setItems(Genderr);
        Gender2.setItems(Genderr);
        Gender3.setItems(Genderr);
        Gender4.setItems(Genderr);
        Gender5.setItems(Genderr);

        Gender1.setOnAction((event) -> {

            Gen1 = Gender1.getSelectionModel().getSelectedItem();
        });

        Gender2.setOnAction((event) -> {

            Gen2 = Gender2.getSelectionModel().getSelectedItem();
        });

        Gender3.setOnAction((event) -> {

            Gen3 = Gender3.getSelectionModel().getSelectedItem();
        });
        Gender4.setOnAction((event) -> {

            Gen4 = Gender4.getSelectionModel().getSelectedItem();
        });
        Gender5.setOnAction((event) -> {

            Gen5 = Gender5.getSelectionModel().getSelectedItem();
        });
        // TODO
        if (HomePageWindowController.passenger > 0) {
            Name1.setVisible(true);
            Age1.setVisible(true);
            Gender1.setVisible(true);
            Age1.setText(null);

        }
        if (HomePageWindowController.passenger > 1) {
            Name2.setVisible(true);
            Age2.setVisible(true);
            Gender2.setVisible(true);
            Age2.setText(null);

        }
        if (HomePageWindowController.passenger > 2) {
            Name3.setVisible(true);
            Age3.setVisible(true);
            Gender3.setVisible(true);
            Age3.setText(null);

        }
        if (HomePageWindowController.passenger > 3) {
            Name4.setVisible(true);
            Age4.setVisible(true);
            Gender4.setVisible(true);
            Age4.setText(null);

        }
        if (HomePageWindowController.passenger > 4) {
            Name5.setVisible(true);
            Age5.setVisible(true);
            Gender5.setVisible(true);
            Age5.setText(null);

        }

        BookTicketButton.setOnAction(new EventHandler<ActionEvent>() {

            public void handle(ActionEvent event) {
                try {

                    String quer = "begin ticketbookingPNR(?,?,?,?,?,?);end;";

                    CallableStatement s3;

                    s3 = test.con.prepareCall(quer);
                    StringTokenizer s = new StringTokenizer(TicketAvailabilityWindowController.neww.getA(), "(");
                    System.out.println(s.nextToken());
                    String p = s.nextToken();
                    FlightId = p.substring(0, p.length() - 1);
                     s3.setString(1, FlightId);
                    s3.setDate(2, HomePageWindowController.date);
                    s3.setInt(3, HomePageWindowController.passenger);
                    s3.setString(4, GeneralLoginWindowController.userNameData);
                    FARE = (HomePageWindowController.passenger) * (Integer.parseInt(TicketAvailabilityWindowController.neww.getD()));
                    s3.setInt(5, FARE);
                    System.out.println();
                    s3.registerOutParameter(6, OracleTypes.NUMBER);

                    s3.executeUpdate();
                    java.math.BigDecimal x = (java.math.BigDecimal) s3.getObject(6);
                    PNR = x.toString();
                    



                    String n1 = Name1.getText();
                    String n2 = Name2.getText();
                    String n3 = Name3.getText();
                    String n4 = Name4.getText();
                    String n5 = Name5.getText();
                    String a1 = Age1.getText();
                    String a2 = Age2.getText();
                    String a3 = Age3.getText();
                    String a4 = Age4.getText();
                    String a5 = Age5.getText();
                    
                  
                    String query = "insert into details_of_person_booked values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

                    PreparedStatement stat;

                    stat = test.con.prepareStatement(query);
                    stat.setString(1, PNR);
                    stat.setString(2, n1);
                    stat.setInt(3, Integer.parseInt(a1));
                    //System.out.println(Gen1.substring(0, 1));
                    stat.setString(4, Gen1.substring(0, 1));
                    stat.setString(5, n2);
                    stat.setInt(6, Integer.parseInt(a2));
                    stat.setString(7, Gen2.substring(0, 1));
                    stat.setString(8, n3);
                    stat.setInt(9, Integer.parseInt(a3));
                    stat.setString(10, Gen3.substring(0, 1));
                    stat.setString(11, n4);
                    stat.setInt(12, Integer.parseInt(a4));
                    stat.setString(13, Gen4.substring(0, 1));
                    stat.setString(14, n5);
                    stat.setInt(15, Integer.parseInt(a5));
                    stat.setString(16, Gen5.substring(0, 1));
                    stat.executeUpdate();

                    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("FinalBookingInfo.fxml"));
                    Parent root1 = (Parent) fxmlLoader.load();
                    root1.setId("paneFinalBookingInfo");
                    Stage stage4 = new Stage();
                    stage4.resizableProperty().setValue(Boolean.FALSE);
                    //stage4.getIcons().add(new Image("ico.png"));
                    stage4.setTitle("Final Details About your Ticket");
                    Scene scene = new Scene(root1);
                    scene.getStylesheets().addAll(this.getClass().getResource("FinalBookingInfo.css").toExternalForm());
                    stage4.setScene(scene);
                    stage4.show();
                    TrayNotification tray=new TrayNotification();
                        tray.setTitle("Congrats");
                        tray.setMessage("Ticket Booked");
                        tray.setNotificationType(NotificationType.SUCCESS);
                        tray.showAndDismiss(Duration.millis(2000));
                                           
                    System.out.println(PNR);
                    Stage stage5;
                    stage5 = (Stage) BookTicketButton.getScene().getWindow();
                    stage5.close();
                } catch (Exception e) {
                   warning_Text.setText("Invalid Entry");
                }
            }
        });
        BackButton.setOnAction(new EventHandler<ActionEvent>() {

            public void handle(ActionEvent event) {
                try {
                    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("TicketAvailabilityWindow.fxml"));
                    Parent root1 = (Parent) fxmlLoader.load();
                    root1.setId("paneTicketAvailability");
                    Stage stage4 = new Stage();
                    stage4.resizableProperty().setValue(Boolean.FALSE);
                    //stage4.getIcons().add(new Image("ico.png"));
                    stage4.setTitle("Available Tickets");
                    Scene scene = new Scene(root1);
                    scene.getStylesheets().addAll(this.getClass().getResource("TicketAvailability.css").toExternalForm());
                    stage4.setScene(scene);
                    stage4.show();
                    Stage stage5;
                    stage5 = (Stage) BackButton.getScene().getWindow();
                    stage5.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

    }

}


/*

create or replace procedure ticketbooking(
	flightid IN varchar2,
	date IN DATE,
	requestedSeats IN number,
	usrname IN varchar2,
	faretaken IN number


 */
