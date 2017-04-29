/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import com.jfoenix.controls.JFXButton;
import java.io.IOException;
import java.math.BigInteger;
import java.net.URL;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.HashMap;
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
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TableView;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import oracle.jdbc.OracleCallableStatement;
import oracle.jdbc.OracleTypes;

/**
 * FXML Controller class
 *
 * @author RAKA
 */
public class UpdateFlightWindowController implements Initializable {

    @FXML
    private JFXButton HomeButton;
    @FXML
    private Text Date;
    @FXML
    private JFXButton AppplyChangesButton;
    @FXML
    private TableView<Data2> TableFlights;
    @FXML
    private TableColumn<Data2, String> FlightIdColumn;

    @FXML
    private TableColumn<Data2, String> SourceColumn;

    @FXML
    private TableColumn<Data2, String> DestinationColumn;

    @FXML
    private TableColumn<Data2, String> DepartsColumn;

    @FXML
    private TableColumn<Data2, String> ReachesDestinationColumn;

    @FXML
    private TableColumn<Data2, String> StatusColumn;

    @FXML
    private TableColumn<Data2, String> SeatsRewmainigColumn;

    @FXML
    private JFXButton Edit;

    public static HashMap<String, String> Codes;
    private Text DateText;
    public static ObservableList<Data2> lst = FXCollections.observableArrayList();
    public static Data2 selected;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        lst.clear();
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

        String datestring = dateFormat.format(java.sql.Date.valueOf(OperatorMainWindowController.date));
        Date.setText(datestring);

        TableFlights.getSelectionModel().selectedItemProperty().addListener(new ChangeListener< Data2>() {
            @Override
            public void changed(ObservableValue<? extends Data2> observable, Data2 oldValue, Data2 newValue) {

                selected = TableFlights.getSelectionModel().getSelectedItem();

            }

        });

        FlightIdColumn.setCellValueFactory(new PropertyValueFactory<Data2, String>("a"));
        SourceColumn.setCellValueFactory(new PropertyValueFactory<Data2, String>("b"));

        DestinationColumn.setCellValueFactory(new PropertyValueFactory<Data2, String>("c"));
        DepartsColumn.setCellValueFactory(new PropertyValueFactory<Data2, String>("d"));
        ReachesDestinationColumn.setCellValueFactory(new PropertyValueFactory<Data2, String>("e"));
        StatusColumn.setCellValueFactory(new PropertyValueFactory<Data2, String>("f"));
        SeatsRewmainigColumn.setCellValueFactory(new PropertyValueFactory<Data2, String>("g"));

        try {

            String quer = "select f.flight_id,f.src,f.dest,f.departure,f.arrival,t.status,to_char(t.Delayed,'0000') from (select * from flight_schedule where day = ?) f \n"
                    + "left outer join (\n"
                    + "	select s.flight_id as flight_id, s.status as Status,s.delayduration as Delayed from current_runnning_status s \n"
                    + "	where dt = ?\n"
                    + ") t on f.flight_id = t.flight_id";
            PreparedStatement s3;
            Calendar ccc = Calendar.getInstance();
            ccc.setTime(java.sql.Date.valueOf(OperatorMainWindowController.date));

            s3 = test.con.prepareStatement(quer);

            s3.setInt(1, ccc.get(Calendar.DAY_OF_WEEK));
            s3.setDate(2, java.sql.Date.valueOf(OperatorMainWindowController.date));
            ResultSet rs = s3.executeQuery();

            while (rs.next()) {
                System.out.println("fvsdgv");

                String a, b, c, d, e, f, g, h;
                a = rs.getString(1);
                b = rs.getString(2);
                c = rs.getString(3);
                d = rs.getString(4);
                e = "dsv";
                f = rs.getString(5);
                g = rs.getString(6);
                if (rs.wasNull()) {
                    g = "ON TIME";
                }
                if (!g.equals("ON TIME")) {
                    String t = rs.getString(7);

                    g = g + "(Delayed by " + t + " hh:mm)";
                }

                String query = "begin find_seats(?,?,?);end;";
                CallableStatement s;
                s = test.con.prepareCall(query);

                s.setString(1, a);
                s.setDate(2, java.sql.Date.valueOf(OperatorMainWindowController.date));

                s.registerOutParameter(3, OracleTypes.NUMBER);
                s.executeUpdate();
                java.math.BigDecimal x = (java.math.BigDecimal) s.getObject(3);
                System.out.println(x);

                Data2 qq = new Data2(a, b, c, d, f, g, x.toString());
                System.out.println(a + b + c + d + e + f + g);

                lst.add(qq);

            }
            TableFlights.setItems(lst);

        } catch (SQLException ex) {
            Logger.getLogger(TicketAvailabilityWindowController.class.getName()).log(Level.SEVERE, null, ex);
        }

        Edit.setOnAction(new EventHandler<ActionEvent>() {

            public void handle(ActionEvent event) {

                try {
                    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("EditStatus.fxml"));
                    Parent root1;

                    root1 = (Parent) fxmlLoader.load();

                    root1.setId("paneEditStatus");
                    Stage stage4 = new Stage();
                    stage4.resizableProperty().setValue(Boolean.FALSE);
                    stage4.setTitle("Status");
                    Scene scene = new Scene(root1);
                    scene.getStylesheets().addAll(this.getClass().getResource("EditStatus.css").toExternalForm());

                    stage4.setScene(scene);
                    stage4.show();

                    //open popup jere, close, observable list.add
                } catch (IOException ex) {
                    Logger.getLogger(UpdateFlightWindowController.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
        });
        HomeButton.setOnAction(new EventHandler<ActionEvent>() {

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
                    stage5 = (Stage) HomeButton.getScene().getWindow();
                    stage5.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

}
