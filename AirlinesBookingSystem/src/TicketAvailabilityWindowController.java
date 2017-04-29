/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import com.jfoenix.controls.JFXButton;
import java.io.IOException;
import java.net.URL;
import java.sql.CallableStatement;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.SimpleStringProperty;
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
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import oracle.jdbc.OracleCallableStatement;
import oracle.jdbc.OracleTypes;

/**
 * FXML Controller class
 *
 * @author vips
 */
public class TicketAvailabilityWindowController implements Initializable {

    @FXML
    private JFXButton MyProfileButton;
    @FXML
    private JFXButton BookNowButton;
    @FXML
    private JFXButton LogOutButton;
    @FXML
    private JFXButton ModifyButton;
    @FXML
    private Text FromText;
    @FXML
    private Text ToText;
    @FXML
    private Text DateText;
    @FXML
    private Text NoofPassengersText;
    @FXML
    private Text ClassText;
    @FXML
    private TableView<Data> PlaneTable;
    @FXML
    private TableColumn<Data, String> AirlinesColumn;
    @FXML
    private TableColumn<Data, String> DepartureColumn;
    @FXML
    private TableColumn<Data, String> ArrivalColumn;
    @FXML
    private TableColumn<Data, String> FareColumn;
    @FXML
    private TableColumn<Data, String> DurationColumn;
    @FXML
    private Text Warning;
    @FXML
    private JFXButton Home_Botton;

    ObservableList<Data> lst = FXCollections.observableArrayList();
    public static HashMap<String, String> Codes;
    java.sql.Date sqlDate;
    public static Data neww;
    Data old;

    /**
     * Initializes th controller class..
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        Codes = new HashMap<String, String>();
        Codes.put("Delhi", "DEL");
        Codes.put("Mumbai", "BOM");
        Codes.put("Jaipur", "JAI");
        Codes.put("Kolkata", "KOL");
        Codes.put("Banglore", "BLR");
        Codes.put("Ahmedabad", "AMD");
        Codes.put("Dubai", "DXB");
        Codes.put("Chennai", "MAA");
        Codes.put("Pune", "PNQ");
        Codes.put("Hyderabad", "HYD");
        System.out.println("-------" + Codes.get("Delhi") + "---------------");
        System.out.println(HomePageWindowController.From_value);
        System.out.println(Codes.get(HomePageWindowController.From_value));

        FromText.setText(HomePageWindowController.From_value);
        ToText.setText(HomePageWindowController.To_value);
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

        String datestring = dateFormat.format(HomePageWindowController.date);
        DateText.setText(datestring);
        NoofPassengersText.setText(Integer.toString(HomePageWindowController.passenger));
        ClassText.setText(HomePageWindowController.Class);

        AirlinesColumn.setCellValueFactory(new PropertyValueFactory<Data, String>("a"));
        DepartureColumn.setCellValueFactory(new PropertyValueFactory<Data, String>("b"));

        ArrivalColumn.setCellValueFactory(new PropertyValueFactory<Data, String>("c"));
        FareColumn.setCellValueFactory(new PropertyValueFactory<Data, String>("d"));
        DurationColumn.setCellValueFactory(new PropertyValueFactory<Data, String>("e"));

        try {
            String d1 = "01-05-2017";
            SimpleDateFormat sdf1 = new SimpleDateFormat("dd-MM-yyyy");

            java.util.Date dt1 = sdf1.parse(d1);
            sqlDate = new Date(dt1.getTime());

        } catch (ParseException ex) {
            Logger.getLogger(TicketAvailabilityWindowController.class.getName()).log(Level.SEVERE, null, ex);
        }

        try {
            System.out.println("fvsdgv");

            String quer = "begin get_all_flights(?,?,?,?,?,?);end;";
            CallableStatement s3;

            s3 = test.con.prepareCall(quer);
            s3.setString(1, Codes.get(HomePageWindowController.From_value));
            s3.setString(2, Codes.get(HomePageWindowController.To_value));
            s3.setDate(3, HomePageWindowController.date);
            s3.setInt(4, HomePageWindowController.day);
            s3.setInt(5, HomePageWindowController.passenger);
            s3.registerOutParameter(6, OracleTypes.CURSOR);
            System.out.println();
            s3.executeUpdate();
            System.out.println(Codes.get(HomePageWindowController.From_value) + " " + Codes.get(HomePageWindowController.To_value) + " " + java.sql.Date.valueOf("2017-05-01") + " " + HomePageWindowController.day + " " + HomePageWindowController.passenger);

            ResultSet rs = (ResultSet) s3.getObject(6);
            Random r = new Random();
            int flag = 0;
            while (rs.next()) {
                System.out.println("fvsdg8989v");
                flag = 1;

                String a, b, c, d, e;
                a = rs.getString(1);
                b = rs.getString(2);
                c = rs.getString(3);
                d = rs.getString(4);
                int xx=rs.getInt(5);
                if(HomePageWindowController.Class=="Business")
                {
                    xx=2*xx;
                }
                e = Integer.toString(xx);
                String x = a + "(" + b + ")";
                int xxxx = Integer.parseInt(c) - Integer.parseInt(d);

                int qqqq = Math.abs(xxxx);
                String w = Integer.toString(qqqq);
                Data qq = new Data(x, c, d, e, w);
                System.out.println(x + c + d + e + w);

                lst.add(qq);

            }
            PlaneTable.setItems(lst);
            if (flag == 0) {

                Warning.setText("* No Flights For Given Date .Check for another date");
            }

        } catch (SQLException ex) {
            Logger.getLogger(TicketAvailabilityWindowController.class.getName()).log(Level.SEVERE, null, ex);
        }

        MyProfileButton.setOnAction(new EventHandler<ActionEvent>() {

            public void handle(ActionEvent event) {

                try {

                    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("MyProfile.fxml"));
                    Parent root1;

                    root1 = (Parent) fxmlLoader.load();

                    root1.setId("paneMyProfile");
                    Stage stage4 = new Stage();
                    stage4.resizableProperty().setValue(Boolean.FALSE);
                    stage4.setTitle("MyProfile");
                    Scene scene = new Scene(root1);
                    scene.getStylesheets().addAll(this.getClass().getResource("MyProfile.css").toExternalForm());
                    stage4.setScene(scene);
                    stage4.show();
                    Stage stage5;
                    stage5 = (Stage) MyProfileButton.getScene().getWindow();
                    stage5.close();

                } catch (IOException ex) {
                    Logger.getLogger(HomePageWindowController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        LogOutButton.setOnAction(new EventHandler<ActionEvent>() {

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
                    stage5 = (Stage) LogOutButton.getScene().getWindow();
                    stage5.close();

                } catch (IOException ex) {
                    Logger.getLogger(HomePageWindowController.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
        });

        PlaneTable.getSelectionModel().selectedItemProperty().addListener(new ChangeListener< Data>() {

            @Override
            public void changed(ObservableValue<? extends Data> observable, Data oldValue, Data newValue) {

                neww = PlaneTable.getSelectionModel().getSelectedItem();
                System.out.println(neww.a);
                //throw new UnsupportedOperationException("Not supported yet."); 
            }
        });

        BookNowButton.setOnAction(new EventHandler<ActionEvent>() {

            public void handle(ActionEvent event) {

                try {

                    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("AboutAllPassengers.fxml"));
                    Parent root1;

                    root1 = (Parent) fxmlLoader.load();

                    root1.setId("paneAboutAllPassengers");
                    Stage stage4 = new Stage();
                    stage4.resizableProperty().setValue(Boolean.FALSE);
                    stage4.setTitle("Detail About Passengers");
                    Scene scene = new Scene(root1);
                    scene.getStylesheets().addAll(this.getClass().getResource("AboutAllPassengers.css").toExternalForm());
                    stage4.setScene(scene);
                    stage4.show();
                    Stage stage5;
                    stage5 = (Stage) MyProfileButton.getScene().getWindow();
                    stage5.close();

                } catch (IOException ex) {
                    Logger.getLogger(HomePageWindowController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

        ModifyButton.setOnAction(new EventHandler<ActionEvent>() {

            public void handle(ActionEvent event) {

                try {

                    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("TicketCheckingModifyWindow.fxml"));
                    Parent root1;

                    root1 = (Parent) fxmlLoader.load();

                    root1.setId("paneTicketCheckingModify");
                    Stage stage4 = new Stage();
                    stage4.resizableProperty().setValue(Boolean.FALSE);
                    stage4.setTitle("Modify your Choice");
                    Scene scene = new Scene(root1);
                    scene.getStylesheets().addAll(this.getClass().getResource("TicketCheckingModify.css").toExternalForm());
                    stage4.setScene(scene);
                    stage4.show();
                    Stage stage5;
                    stage5 = (Stage) MyProfileButton.getScene().getWindow();
                    stage5.close();

                } catch (IOException ex) {
                    Logger.getLogger(HomePageWindowController.class.getName()).log(Level.SEVERE, null, ex);
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
                    stage4.setTitle("Home_Botton");
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

    }

}
