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
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ResourceBundle;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author vips
 */
public class EditStatusController implements Initializable {

    @FXML
    private JFXComboBox<String> Sts;
    @FXML
    private Text DelayedText;
    @FXML
    private JFXTextField Delayed_By;
    @FXML
    private JFXButton Change;
    @FXML
    private Text Flight_id;

    @FXML
    private Text dest;

    @FXML
    private Text src;

    @FXML
    private Text departs;

    @FXML
    private Text reach;

    @FXML
    private Text Seats;

    ObservableList<String> stat = FXCollections.observableArrayList("OnTime", "Delayed", "Cancel");
    String qaz;
    String p = "";

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        reach.setText(UpdateFlightWindowController.selected.getE());
        Seats.setText(UpdateFlightWindowController.selected.getG());
        departs.setText(UpdateFlightWindowController.selected.getD());
        src.setText(UpdateFlightWindowController.selected.getB());
        dest.setText(UpdateFlightWindowController.selected.getC());
        Flight_id.setText(UpdateFlightWindowController.selected.getA());

        Sts.setItems(stat);
        Sts.setOnAction((event) -> {

            qaz = (String) Sts.getSelectionModel().getSelectedItem();
            if (qaz.equals("Delayed")) {
                DelayedText.setVisible(true);
                Delayed_By.setVisible(true);

            } else {
                DelayedText.setVisible(false);
                Delayed_By.setVisible(false);
            }
        });

        Change.setOnAction(new EventHandler<ActionEvent>() {

            public void handle(ActionEvent event) {
                if (qaz.equals("Delayed")) {
                    p = "(" + "Delayed By : " + Delayed_By.getText() + ")";
                    try {
                        String quer = "begin delayby(?,?,?);end;";
                        CallableStatement s3;

                        s3 = test.con.prepareCall(quer);

                        s3.setString(1, UpdateFlightWindowController.selected.getA());
                        s3.setDate(2, java.sql.Date.valueOf(OperatorMainWindowController.date));
                        s3.setInt(3, Integer.parseInt(Delayed_By.getText()));
                        s3.executeUpdate();

                    } catch (SQLException ex) {
                        Logger.getLogger(EditStatusController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                if (qaz.equals("Cancel")) {
                    try {
                        String quer = "begin cancel_flight(?,?);end;";
                        CallableStatement s3;

                        s3 = test.con.prepareCall(quer);

                        s3.setString(1, UpdateFlightWindowController.selected.getA());
                        s3.setDate(2, java.sql.Date.valueOf(OperatorMainWindowController.date));
                        s3.executeUpdate();

                    } catch (SQLException ex) {
                        Logger.getLogger(EditStatusController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                Data2 previous;
                previous = UpdateFlightWindowController.selected;
                String a = previous.getA();
                String b = previous.getB();
                String c = previous.getC();
                String d = previous.getD();
                String e = qaz + p;
                String f = previous.getE();
                String g = previous.getG();
                Data2 neww = new Data2(a, b, c, d, f, e, g);
                UpdateFlightWindowController.lst.remove(UpdateFlightWindowController.selected);
                UpdateFlightWindowController.lst.add(neww);
                Stage stage5;
                stage5 = (Stage) Change.getScene().getWindow();
                stage5.close();

            }
        });
    }

    // TODO
}
