/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
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
 * @author RAKA
 */
public class OperatorChangePasswordWindowController implements Initializable {

    @FXML
    private JFXPasswordField OldPasswordTextField;
    @FXML
    private JFXPasswordField NewPasswordTextFild;
    @FXML
    private JFXPasswordField ConfirmPasswordTextField;
    @FXML
    private JFXButton ChangePasswordButton;
    @FXML
    private JFXButton BackButton;
    @FXML
    private Text WarningText;
    PreparedStatement stt;
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
                    stage5 = (Stage) BackButton.getScene().getWindow();
                    stage5.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        ChangePasswordButton.setOnAction(new EventHandler<ActionEvent>() {

            public void handle(ActionEvent event) {
                try {
                    stt = test.con.prepareStatement("update emp_login_record set password = ?" + " where emp_id ='" + LoginWindowController.userNameData + "'");
                    String sss = "select t.* from emp_login_record t where emp_id ='" + LoginWindowController.userNameData + "'";
                    Statement stmtt = test.con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);

                    r1 = stmtt.executeQuery(sss);
                    if (r1.next()) {
                        if (!OldPasswordTextField.getText().equals("")) {
                            if (OldPasswordTextField.getText().equals(r1.getString("password")) && NewPasswordTextFild.getText().equals(ConfirmPasswordTextField.getText())) {

                                stt.setString(1, NewPasswordTextFild.getText());
                                stt.executeUpdate();

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
                                stage5 = (Stage) BackButton.getScene().getWindow();
                                stage5.close();
                            } else {
                                OldPasswordTextField.setText("");
                                NewPasswordTextFild.setText("");
                                ConfirmPasswordTextField.setText("");
                                WarningText.setText("Password Doesn't Match");
                            }
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

}
