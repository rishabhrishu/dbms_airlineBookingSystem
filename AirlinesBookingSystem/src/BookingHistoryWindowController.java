/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import com.jfoenix.controls.JFXButton;
import java.io.IOException;
import java.net.URL;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import oracle.jdbc.OracleTypes;

/**
 * FXML Controller class
 *
 * @author RAKA
 */
public class BookingHistoryWindowController implements Initializable {

    @FXML
    private JFXButton Home_Botton;
    @FXML
    private TableView<Data4> BookingHistoryTable;
    @FXML
    private TableColumn<Data4, String> PNRColumn;
    @FXML
    private TableColumn<Data4, String> DateColumn;
    @FXML
    private TableColumn<Data4, String> NoofPassengersColumn;
    @FXML
    private TableColumn<Data4, String> SourceColumn;
    @FXML
    private TableColumn<Data4, String> DestinationColumn;
    @FXML
    private TableColumn<Data4, String> FlightIDColumn;
    @FXML
    private TableColumn<Data4, String> StatusColumn;

    public static ObservableList<Data4> lst = FXCollections.observableArrayList();

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        lst.clear();
        PNRColumn.setCellValueFactory(new PropertyValueFactory<Data4, String>("a"));
        DateColumn.setCellValueFactory(new PropertyValueFactory<Data4, String>("b"));

        NoofPassengersColumn.setCellValueFactory(new PropertyValueFactory<Data4, String>("c"));
        SourceColumn.setCellValueFactory(new PropertyValueFactory<Data4, String>("d"));
        DestinationColumn.setCellValueFactory(new PropertyValueFactory<Data4, String>("e"));
        FlightIDColumn.setCellValueFactory(new PropertyValueFactory<Data4, String>("f"));
        StatusColumn.setCellValueFactory(new PropertyValueFactory<Data4, String>("g"));

        try {

            String quer = "select t.pnr,t.date_of_journey,t.no_of_seats_booked,s.src,s.dest,t.flight_id,t.reservation_status from (select * from transactions where transactions.username=?) t \n"
                    + " inner join(\n"
                    + " select d.flight_id as flight_id,d.src as src,d.dest as dest from flight_schedule d) s \n"
                    + "on t.flight_id = s.flight_id";
            PreparedStatement s3;
            /* Calendar ccc = Calendar.getInstance();
            ccc.setTime(java.sql.Date.valueOf(OperatorMainWindowController.date));
             */
            s3 = test.con.prepareStatement(quer);

            s3.setString(1, GeneralLoginWindowController.userNameData);
            ResultSet rs = s3.executeQuery();

            while (rs.next()) {
                System.out.println("fvsdgv");

                java.math.BigDecimal pnr = rs.getBigDecimal(1);
                java.sql.Date date = rs.getDate(2);
                int seats = rs.getInt(3);

                String srcCode = rs.getString(4);
                String destCode = rs.getString(5);
                String fid = rs.getString(6);
                String status = rs.getString(7);

                /*     String query = "begin find_seats(?,?,?);end;";
                CallableStatement s;
                s = test.con.prepareCall(query);

                s.setString(1, a);
                s.setDate(2, java.sql.Date.valueOf(OperatorMainWindowController.date));

                s.registerOutParameter(3, OracleTypes.NUMBER);
                s.executeUpdate();
                java.math.BigDecimal x = (java.math.BigDecimal) s.getObject(3);
                System.out.println(x);*/
                Data4 qq = new Data4(pnr.toString(), date.toString(), Integer.toString(seats), srcCode, destCode, fid, status);
                System.out.println(pnr.toString() + date.toString() + seats + srcCode + destCode + fid + status);

                lst.add(qq);

            }
            BookingHistoryTable.setItems(lst);

        } catch (SQLException ex) {
            Logger.getLogger(TicketAvailabilityWindowController.class.getName()).log(Level.SEVERE, null, ex);
        }

        Home_Botton.setOnAction(new EventHandler<ActionEvent>() {

            public void handle(ActionEvent event) {

                try {
                    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("HomePageWindow.fxml"));
                    Parent root1;

                    root1 = (Parent) fxmlLoader.load();

                    root1.setId("paneHomePage");
                    Stage stage4 = new Stage();
                    stage4.resizableProperty().setValue(Boolean.FALSE);
                    stage4.setTitle("Home Page");
                    Scene scene = new Scene(root1);
                    scene.getStylesheets().addAll(this.getClass().getResource("HomePage.css").toExternalForm());

                    stage4.setScene(scene);
                    stage4.show();
                    Stage stage5;
                    stage5 = (Stage) Home_Botton.getScene().getWindow();
                    stage5.close();

                } catch (IOException ex) {
                    Logger.getLogger(HomePageWindowController.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
        });
        // TODO
    }

}
