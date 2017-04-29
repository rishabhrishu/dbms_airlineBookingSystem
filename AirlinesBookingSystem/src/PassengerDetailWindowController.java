/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import com.jfoenix.controls.JFXButton;
import java.net.URL;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
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
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import oracle.jdbc.OracleCallableStatement;
import oracle.jdbc.OracleTypes;

/**
 * FXML Controller class
 *
 * @author RAKA
 */
public class PassengerDetailWindowController implements Initializable {

    @FXML
    private TableView<Data3> PassengersTable;

    @FXML
    private TableColumn<Data3, String> Pnr;

    @FXML
    private TableColumn<Data3, String> Flightid;

    @FXML
    private TableColumn<Data3, String> Username;

    @FXML
    private TableColumn<Data3, String> Date_book;

    @FXML
    private TableColumn<Data3, String> seat;

    @FXML
    private TableColumn<Data3, String> fare;

    @FXML
    private TableColumn<Data3, String> datejourney;

    @FXML
    private TableColumn<Data3, String> status;

    @FXML
    private JFXButton BackButton;
    ObservableList<Data3> lst = FXCollections.observableArrayList();

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO

        Pnr.setCellValueFactory(new PropertyValueFactory<Data3, String>("a"));
        Flightid.setCellValueFactory(new PropertyValueFactory<Data3, String>("b"));

        Username.setCellValueFactory(new PropertyValueFactory<Data3, String>("c"));
        Date_book.setCellValueFactory(new PropertyValueFactory<Data3, String>("d"));
        seat.setCellValueFactory(new PropertyValueFactory<Data3, String>("e"));
        fare.setCellValueFactory(new PropertyValueFactory<Data3, String>("f"));

        datejourney.setCellValueFactory(new PropertyValueFactory<Data3, String>("g"));

        status.setCellValueFactory(new PropertyValueFactory<Data3, String>("h"));

        try {

            String quer = "select * from transactions";
            PreparedStatement s3;

            s3 = test.con.prepareCall(quer);

            ResultSet rs = s3.executeQuery();

            while (rs.next()) {

                String a, b, c, d, e, f, g, h;
                java.math.BigDecimal xx = rs.getBigDecimal(1);
                java.sql.Timestamp q = rs.getTimestamp(2);

                a = xx.toString();
                b = q.toString();
                c = rs.getString(3);
                d = rs.getString(4);
                xx = rs.getBigDecimal(5);
                e = xx.toString();
                xx = rs.getBigDecimal(6);
                f = xx.toString();
                g = rs.getString(7);
                java.sql.Date date = rs.getDate(8);
                DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

                h = dateFormat.format(date);

                Data3 qq = new Data3(a, d, c, b, f, e, h, g);
                System.out.println();

                lst.add(qq);

            }
            PassengersTable.setItems(lst);

        } catch (SQLException ex) {
            Logger.getLogger(TicketAvailabilityWindowController.class.getName()).log(Level.SEVERE, null, ex);
        }

        BackButton.setOnAction(new EventHandler<ActionEvent>() {

            public void handle(ActionEvent event) {
                try {
                    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("OperatorMainWindow.fxml"));
                    Parent root1 = (Parent) fxmlLoader.load();
                    root1.setId("paneOperatorMain");
                    Stage stage4 = new Stage();
                    stage4.resizableProperty().setValue(Boolean.FALSE);
                    //stage4.getIcons().add(new Image("ico.png"));
                    stage4.setTitle("Operator Main Window");
                    Scene scene = new Scene(root1);
                    scene.getStylesheets().addAll(this.getClass().getResource("OperatorMain.css").toExternalForm());
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
