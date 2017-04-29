/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import java.io.IOException;
import java.net.URL;
import java.sql.CallableStatement;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import tray.notification.NotificationType;
import tray.notification.TrayNotification;

/**
 * FXML Controller class
 *
 * @author RAKA
 */
public class SignUpWindowController implements Initializable {

   @FXML
    private AnchorPane SignUpWindowPane;
       @FXML
    private Text Warning_Text;

    @FXML
    private JFXButton CheckAvailabilityButton;

    @FXML
    private JFXButton SignUpButton;

    @FXML
    private JFXButton BackButton;

    @FXML
    private JFXPasswordField PasswordTextField;

    @FXML
    private JFXPasswordField ConfirmPasswordTextField;

    @FXML
    private JFXTextField FirstNameTextField;

    @FXML
    private JFXTextField LastNameTextField;

    @FXML
    private JFXTextField AadharNoTextField;

    @FXML
    private JFXTextField UsernameTextField;

    @FXML
    private JFXTextField NationalityTextField;

    @FXML
    private JFXTextField EmailTextField;

    @FXML
    private JFXTextField MobileNoTextField;

    @FXML
    private JFXDatePicker Date_Field;

    @FXML
    private JFXTextField Address_Line1;

    @FXML
    private JFXTextField Address_Line2;

    @FXML
    private JFXTextField Address_Line3;

    String insertTableSQL = "INSERT INTO login_record " + " VALUES" + "(?,?)";
    ResultSet r1;
    String insertTableSQL2 = "INSERT INTO user_details " + " VALUES" + "(?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

    PreparedStatement st, st1;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(SignUpWindowController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(SignUpWindowController.class.getName()).log(Level.SEVERE, null, ex);
        }
        CheckAvailabilityButton.setOnAction(new EventHandler<ActionEvent>() {

            public void handle(ActionEvent event) {

                try {
                    st = test.con.prepareStatement(insertTableSQL);
                    PreparedStatement stt = test.con.prepareStatement("select * from login_record where Username = ? ");
                    stt.setString(1, UsernameTextField.getText());
                    r1 = stt.executeQuery();
                    if (r1.next()) {
                        UsernameTextField.setText("");

                    }

                } catch (SQLException ex) {
                    Logger.getLogger(SignUpWindowController.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
        });

        // TODO
        SignUpButton.setOnAction(new EventHandler<ActionEvent>() {

            public void handle(ActionEvent event) {
                String User = UsernameTextField.getText();
                String Email = EmailTextField.getText();

                String cpasswordData = ConfirmPasswordTextField.getText();
                String lastNameData = LastNameTextField.getText();
                String passwordData = PasswordTextField.getText();
                String firstNameData = FirstNameTextField.getText();
                String Adhar = AadharNoTextField.getText();
                String nationality = NationalityTextField.getText();

                String Mobile = MobileNoTextField.getText();
                LocalDate date = Date_Field.getValue();

                System.out.println(date);

                try {

                    st = test.con.prepareStatement(insertTableSQL);
                    st1 = test.con.prepareStatement(insertTableSQL2);

                    PreparedStatement stt = test.con.prepareStatement("select * from login_record where Username = ? ");
                    stt.setString(1, User);
                    r1 = stt.executeQuery();

                } catch (Exception e) {
                    e.printStackTrace();
                }

                try {
                    if (!r1.next() && !User.equals("")&&!Address_Line1.getText().equals("")&&!Address_Line2.getText().equals("")&&!Address_Line3.getText().equals("")  && !nationality.equals("") && !Email.equals("") && !Mobile.equals("") && !Adhar.equals("") && passwordData.equals(cpasswordData) && !passwordData.equals("") && !cpasswordData.equals("") && !firstNameData.equals("") && !lastNameData.equals("")) {

                        
                        CallableStatement s2;
                    String Addr = Address_Line1.getText()+ "|" + Address_Line2.getText() + "|" + Address_Line3.getText()+"|"+NationalityTextField.getText() ;
                        String quer = "begin insert_user(?,?,?,?,?,?,?,?,?);end;";

                        s2 = test.con.prepareCall(quer);
                        s2.setString(1, Adhar);
                        s2.setString(2, User);
                        s2.setString(3, firstNameData);
                        s2.setString(4, lastNameData);
                        s2.setDate(5, java.sql.Date.valueOf(date));
                        s2.setLong(6, Long.parseLong(Mobile));
                        s2.setString(7, Email);
                        s2.setString(8, Addr);
                        s2.setString(9, passwordData);

                        s2.execute();

                        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("GeneralLoginWindow.fxml"));
                        Parent root1 = (Parent) fxmlLoader.load();
                        root1.setId("paneGeneralLogin");
                        Stage stage4 = new Stage();
                        stage4.resizableProperty().setValue(Boolean.FALSE);
                        //stage4.getIcons().add(new Image("ico.png"));
                        stage4.setTitle("Login");
                        Scene scene = new Scene(root1);
                        scene.getStylesheets().addAll(this.getClass().getResource("GeneralLogin.css").toExternalForm());
                        stage4.setScene(scene);
                        stage4.show();
                        TrayNotification tray=new TrayNotification();
                        tray.setTitle("Success");
                        tray.setMessage("Account Created ");
                        tray.setNotificationType(NotificationType.SUCCESS);
                        tray.showAndDismiss(Duration.millis(2000));
                        Stage stage5;
                        stage5 = (Stage) SignUpButton.getScene().getWindow();
                        stage5.close();

                    } else {
                           
                    Warning_Text.setText("Invalid Credentials");
                    }

                } catch (SQLException ex) {
                    Logger.getLogger(SignUpWindowController.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(SignUpWindowController.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
        });

        BackButton.setOnAction(new EventHandler<ActionEvent>() {

            public void handle(ActionEvent event) {
                try {
                    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("GeneralLoginWindow.fxml"));
                    Parent root1 = (Parent) fxmlLoader.load();
                    root1.setId("paneGeneralLogin");
                    Stage stage4 = new Stage();
                    stage4.resizableProperty().setValue(Boolean.FALSE);
                    //stage4.getIcons().add(new Image("ico.png"));
                    stage4.setTitle("Login");
                    Scene scene = new Scene(root1);
                    scene.getStylesheets().addAll(this.getClass().getResource("GeneralLogin.css").toExternalForm());
                    stage4.setScene(scene);
                    stage4.show();
                    Stage stage5;
                    stage5 = (Stage) SignUpButton.getScene().getWindow();
                    stage5.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

}
