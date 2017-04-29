/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import java.net.URL;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author RAKA
 */
public class LoginWindowController implements Initializable {

    @FXML
    private JFXTextField UsernameTextField;
    @FXML
    private JFXPasswordField PasswordTextField;
    @FXML
    private JFXButton SignInButton;
    @FXML
    private JFXButton BackButton;

    @FXML
    private Text warningText;
    public static String userNameData;
    ResultSet r1;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
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
                    stage5 = (Stage) BackButton.getScene().getWindow();
                    stage5.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        SignInButton.setOnAction(new EventHandler<ActionEvent>() {

            public void handle(ActionEvent event) {
                try {

                    String passwordData = PasswordTextField.getText();
                    userNameData = UsernameTextField.getText();
                    // passwordData = Encyption(passwordData);
                    boolean log = false;
                    try {
                        Class.forName("oracle.jdbc.driver.OracleDriver");
                        DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
                        PreparedStatement stt = test.con.prepareStatement("select password from emp_login_record where emp_id =?");
                        stt.setString(1, userNameData);
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
                            /*else
                    {
                    warningText.setText("*Username or Password is incorrect");
                    PasswordTextField.setText("");
                    UsernameTextField.setText("");
                    }/*/
                            
                        } else {
                            warningText.setText("*Username or Password is incorrect");
                            PasswordTextField.setText("");
                            UsernameTextField.setText("");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    if (log == true) {
                        try {

                            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("OperatorMainWindow.fxml"));
                            Parent root1 = (Parent) fxmlLoader.load();
                            root1.setId("paneOperatorMain");
                            Stage stage4 = new Stage();
                            stage4.resizableProperty().setValue(Boolean.FALSE);
                            //stage4.getIcons().add(new Image("ico.png"));
                            stage4.setTitle("Main Window");
                            Scene scene = new Scene(root1);
                            scene.getStylesheets().addAll(this.getClass().getResource("OperatorMain.css").toExternalForm());
                            stage4.setScene(scene);
                            stage4.show();
                            Stage stage5;
                            stage5 = (Stage) SignInButton.getScene().getWindow();
                            stage5.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        warningText.setText("*Username or Password is incorrect");
                        PasswordTextField.setText("");
                        UsernameTextField.setText("");
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

    }

}
