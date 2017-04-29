/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import com.jfoenix.controls.JFXButton;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author vips
 */
public class FinalBookingInfoController implements Initializable {

    @FXML
    private Text From_Text;
    @FXML
    private Text ToTextField;
    @FXML
    private Text DateTextField;
    @FXML
    private Text PassengerTextField;
    @FXML
    private Text PnrTextField;
    @FXML
    private Text FlightIdTextField;
    @FXML
    private Text Fare_Text_Field;
    @FXML
    private JFXButton HomeButton;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        ToTextField.setText(HomePageWindowController.To_value);
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

        String datestring = dateFormat.format(HomePageWindowController.date);
        DateTextField.setText(datestring);
        PassengerTextField.setText(Integer.toString(HomePageWindowController.passenger));
        PnrTextField.setText(AboutAllPassengersController.PNR);
        FlightIdTextField.setText(AboutAllPassengersController.FlightId);
        From_Text.setText(HomePageWindowController.From_value);
        Fare_Text_Field.setText(Integer.toString(AboutAllPassengersController.FARE));

        HomeButton.setOnAction(new EventHandler<ActionEvent>() {

            public void handle(ActionEvent event) {
                try {
                    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("HomePageWindow.fxml"));
                    Parent root1 = (Parent) fxmlLoader.load();
                    root1.setId("paneHomePage");
                    Stage stage4 = new Stage();
                    stage4.resizableProperty().setValue(Boolean.FALSE);
                    //stage4.getIcons().add(new Image("ico.png"));
                    stage4.setTitle("Home Page");
                    Scene scene = new Scene(root1);
                    scene.getStylesheets().addAll(this.getClass().getResource("HomePage.css").toExternalForm());
                    stage4.setScene(scene);
                    stage4.show();
                    Stage stage5;
                    stage5 = (Stage) HomeButton.getScene().getWindow();
                    stage5.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

}
