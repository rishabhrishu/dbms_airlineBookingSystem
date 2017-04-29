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
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import oracle.jdbc.OracleTypes;

/**
 * FXML Controller class
 *
 * @author RAKA
 */
public class Cancel_Ticket_WindowController implements Initializable {

    @FXML
    private JFXButton Home_Botton;
    @FXML
    private TableView<Data5> CancelTicketTable;
    @FXML
    private TableColumn<Data5, String> PNRColumn;
    @FXML
    private TableColumn<Data5, String> DateColumn;
    @FXML
    private TableColumn<Data5, String> NoofPassengersColumn;
    @FXML
    private TableColumn<Data5, String> SourceColumn;
    @FXML
    private TableColumn<Data5, String> DestinationColumn;
    @FXML
    private TableColumn<Data5, String> FlightIDColumn;
    @FXML
    private JFXButton CancelTicketButton;
    public static ObservableList<Data5> lst = FXCollections.observableArrayList();
    public static Data5 selected;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        lst.clear();

        CancelTicketTable.getSelectionModel().selectedItemProperty().addListener(new ChangeListener< Data5>() {
            @Override
            public void changed(ObservableValue<? extends Data5> observable, Data5 oldValue, Data5 newValue) {

                selected = CancelTicketTable.getSelectionModel().getSelectedItem();

            }

        });

        PNRColumn.setCellValueFactory(new PropertyValueFactory<Data5, String>("a"));
        DateColumn.setCellValueFactory(new PropertyValueFactory<Data5, String>("b"));

        NoofPassengersColumn.setCellValueFactory(new PropertyValueFactory<Data5, String>("c"));
        SourceColumn.setCellValueFactory(new PropertyValueFactory<Data5, String>("d"));
        DestinationColumn.setCellValueFactory(new PropertyValueFactory<Data5, String>("e"));
        FlightIDColumn.setCellValueFactory(new PropertyValueFactory<Data5, String>("f"));

        try {

            String quer = "begin tocancel(?,?);end;";
            CallableStatement s3;

            s3 = test.con.prepareCall(quer);

            s3.setString(1, GeneralLoginWindowController.userNameData);
            s3.registerOutParameter(2, OracleTypes.CURSOR);
            s3.executeUpdate();
            ResultSet rs = (ResultSet) s3.getObject(2);

            while (rs.next()) {
                System.out.println("adfsagagagfvsdgv");

                java.math.BigDecimal a = rs.getBigDecimal(1);
                java.sql.Date b = rs.getDate(2);
                int c = rs.getInt(3);
                String d = rs.getString(4);

                String query = "select src,dest from flight_schedule where flight_id = ?";
                CallableStatement s;
                s = test.con.prepareCall(query);

                s.setString(1, d);
                String e = null, f = null;
                ResultSet r = s.executeQuery();
                while (r.next()) {
                    e = r.getString(1);
                    f = r.getString(2);
                }
                Data5 qq = new Data5(a.toString(), b.toString(), Integer.toString(c), e, f, d);

                lst.add(qq);

            }
            CancelTicketTable.setItems(lst);

        } catch (SQLException ex) {
            Logger.getLogger(TicketAvailabilityWindowController.class.getName()).log(Level.SEVERE, null, ex);
        }

        CancelTicketButton.setOnAction(new EventHandler<ActionEvent>() {

            public void handle(ActionEvent event) {
                try {
                    String quer = "begin cancelticket(?,?);end;";
                    CallableStatement s3;
                    s3 = test.con.prepareCall(quer);
                    s3.setString(1, selected.getA());
                    s3.registerOutParameter(2, OracleTypes.NUMBER);
                    s3.execute();
                    java.math.BigDecimal bd= s3.getBigDecimal(2);
                    Alert alert = new Alert(AlertType.INFORMATION);
                    alert.setTitle("Ticket Cancelled");
                    alert.setHeaderText("Ticket Cancelled");
                    alert.setContentText("Your Ticket has been Cancelled!"+" Refund is "+bd.toString());

                    alert.showAndWait();
                    lst.remove(selected);

                } catch (SQLException ex) {
                    Logger.getLogger(TicketAvailabilityWindowController.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
        });

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
