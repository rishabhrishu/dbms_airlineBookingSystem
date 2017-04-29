/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import java.net.URL;
import java.security.MessageDigest;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
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
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.image.Image;
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
public class GeneralLoginWindowController implements Initializable {

    @FXML
    private AnchorPane GeneralLoginWindowPane;
    @FXML
    private Hyperlink ForOfficialsLink;
    @FXML
    private JFXTextField UserNameTextField;
    @FXML
    private JFXPasswordField PasswordTextField;
    @FXML
    private JFXButton LoginButton;
    @FXML
    private JFXButton SignUpButton;

    @FXML
    private Text warningText;
    ResultSet r1;
    PreparedStatement st;
    public static String userNameData;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        ForOfficialsLink.setOnAction(new EventHandler<ActionEvent>() {

            public void handle(ActionEvent event) {
                
                 try {
                    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("LoginWindow.fxml"));
                    Parent root1 = (Parent) fxmlLoader.load();
                    root1.setId("paneLogin");
                    Stage stage4 = new Stage();
                    stage4.resizableProperty().setValue(Boolean.FALSE);
                    //stage4.getIcons().add(new Image("ico.png"));
                    stage4.setTitle("Login");
                    Scene scene = new Scene(root1);
                    scene.getStylesheets().addAll(this.getClass().getResource("Login.css").toExternalForm());
                    stage4.setScene(scene);
                    stage4.show();
                    Stage stage5;
                    stage5 = (Stage) SignUpButton.getScene().getWindow();
                   stage5.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            
                
                
                
                
            }});
        
        
        
        
        
      
        SignUpButton.setOnAction(new EventHandler<ActionEvent>() {

            public void handle(ActionEvent event) {
                try {
                    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("SignUpWindow.fxml"));
                    Parent root1 = (Parent) fxmlLoader.load();
                    root1.setId("paneSignUp");
                    Stage stage4 = new Stage();
                    stage4.resizableProperty().setValue(Boolean.FALSE);
                    //stage4.getIcons().add(new Image("ico.png"));
                    stage4.setTitle("SignUp");
                    Scene scene = new Scene(root1);
                    scene.getStylesheets().addAll(this.getClass().getResource("SignUp.css").toExternalForm());
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

        LoginButton.setOnAction(new EventHandler<ActionEvent>() {

            public void handle(ActionEvent event) {
                String passwordData = PasswordTextField.getText();
                userNameData = UserNameTextField.getText();
               // passwordData = Encyption(passwordData);
                boolean log = false;
                try {
                    Class.forName("oracle.jdbc.driver.OracleDriver");
                    DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
                    //Connection con = DriverManager.getConnection("jdbc:mysql://172.26.47.153:3306/login_chat", "admin", "12345678");
                    PreparedStatement stt = test.con.prepareStatement("select password from login_record where username =?");
                    stt.setString(1,userNameData);
                    r1 = stt.executeQuery();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    if (r1.next()) {
                        
                        String s = r1.getString("PASSWORD");
                        System.out.println(s);
                        if (s.equals(passwordData)) {
                            log = true;
                        }
                    }
                    if (log == true) {
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
                            TrayNotification tray=new TrayNotification();
                        tray.setTitle("Success");
                        tray.setMessage("Logged In ");
                        tray.setNotificationType(NotificationType.SUCCESS);
                        tray.showAndDismiss(Duration.millis(2000));
                            Stage stage5;
                            stage5 = (Stage) SignUpButton.getScene().getWindow();
                            stage5.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        warningText.setText("*Username or Password is incorrect");
                        PasswordTextField.setText("");
                        UserNameTextField.setText("");
                    }
                } catch (SQLException ex) {
                    Logger.getLogger(LoginWindowController.class.getName()).log(Level.SEVERE, null, ex);
                }

            }

        });
    }

    private String Encyption(String password) {
        String algorithm = "SHA";

        byte[] plainText = password.getBytes();

        MessageDigest md = null;

        try {
            md = MessageDigest.getInstance(algorithm);
        } catch (Exception e) {
            e.printStackTrace();
        }

        md.reset();
        md.update(plainText);
        byte[] encodedPassword = md.digest();

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < encodedPassword.length; i++) {
            if ((encodedPassword[i] & 0xff) < 0x10) {
                sb.append("0");
            }
            sb.append(Long.toString(encodedPassword[i] & 0xff, 16));
        }
        return sb.toString(); //To change body of generated methods, choose Tools | Templates.
    }

    // TODO
}
