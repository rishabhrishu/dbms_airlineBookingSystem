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
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ResourceBundle;
import java.util.StringTokenizer;
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
import javafx.util.Duration;
import tray.notification.NotificationType;
import tray.notification.TrayNotification;

/**
 * FXML Controller class
 *
 * @author vips
 */
public class MyProfileController implements Initializable {
    @FXML
    private Text Username_Text;

    @FXML
    private JFXButton Save_Changes_Field;

    @FXML
    private JFXTextField Mobile_Field;

    @FXML
    private Text Name_Field;

    @FXML
    private Text Dob_Field;

    @FXML
    private Text Email_Field;

    @FXML
    private Text Nationality_Field;

    @FXML
    private JFXPasswordField Cur_Pass_Field;

    @FXML
    private JFXPasswordField New_Pass_Field;

    @FXML
    private JFXPasswordField Confirm_Pass_Field;

    @FXML
    private Text Warning_pass_text;

    @FXML
    private JFXTextField Address_Line1;

    @FXML
    private JFXTextField Address_Line2;

    @FXML
    private JFXTextField Address_Line3;

    
    
    ResultSet r1, r2;
    PreparedStatement st, st1;
    PreparedStatement stt;
  


    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
            String ss = "select t.* from user_details t where username ='" + GeneralLoginWindowController.userNameData + "'";
            Statement stmt = test.con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);

            r1 = stmt.executeQuery(ss);
            stt = test.con.prepareStatement("update login_record set password = ?" + " where username ='" + GeneralLoginWindowController.userNameData + "'");

            if (r1.next()) {

                String stringToParse = r1.getString("Address");
                System.out.println(stringToParse);
                StringTokenizer st = new StringTokenizer(stringToParse, "|");
                //System.out.println("gvg");
                Username_Text.setText(r1.getString("username"));

                Mobile_Field.setText(r1.getString("Mobile"));
                Name_Field.setText(r1.getString("First_Name")+" " + r1.getString("LAST_NAME"));
                DateFormat df = new SimpleDateFormat("dd/MM/yyyy");;
                Dob_Field.setText(df.format(r1.getDate("DOB")));
               
                Email_Field.setText(r1.getString("Email"));
                String One,Two,Three,four;
                One=st.nextToken();
                Two=st.nextToken();
                Three=st.nextToken();
                
               //four=st.nextToken();
              Address_Line1.setText(One);
                Address_Line2.setText(Two);
               Address_Line3.setText(Three);
               //Nationality_Field.setText(four);
               

            }

        } catch (ClassNotFoundException ex) {
            Logger.getLogger(MyProfileController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(MyProfileController.class.getName()).log(Level.SEVERE, null, ex);
        }

        // TODO
        Save_Changes_Field.setOnAction(new EventHandler<ActionEvent>() {

            public void handle(ActionEvent event) {
                try {
                    String sss = "select t.* from login_record t where username ='" + GeneralLoginWindowController.userNameData + "'";
                    Statement stmtt = test.con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);

                    r2 = stmtt.executeQuery(sss);

                    String Addr = Address_Line1.getText()+ "|" + Address_Line2.getText() + "|" + Address_Line3.getText() ;
                    r1.updateString("Address", Addr);
                    r1.updateLong(6, Long.parseLong(Mobile_Field.getText()));
                    r1.updateRow();

                    if (r2.next()) {
                        if (!Cur_Pass_Field.getText().equals("")) {
                            if (Cur_Pass_Field.getText().equals(r2.getString("password")) && New_Pass_Field.getText().equals(Confirm_Pass_Field.getText())) {

                                stt.setString(1, New_Pass_Field.getText());
                                stt.executeUpdate();

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
                        tray.setMessage("Changes Saved ");
                        tray.setNotificationType(NotificationType.SUCCESS);
                        tray.showAndDismiss(Duration.millis(2000));
                                    Stage stage5;
                                    stage5 = (Stage) Save_Changes_Field.getScene().getWindow();
                                    stage5.close();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else {
                                Cur_Pass_Field.setText("");
                                New_Pass_Field.setText("");
                                Confirm_Pass_Field.setText("");
                                Warning_pass_text.setText("Password Doesn't Match");

                            }
                        } else {
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
                        tray.setMessage("Changes Saved ");
                        tray.setNotificationType(NotificationType.SUCCESS);
                        tray.showAndDismiss(Duration.millis(2000));
                                Stage stage5;
                                stage5 = (Stage) Save_Changes_Field.getScene().getWindow();
                                stage5.close();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }

                } catch (SQLException ex) {
                    Logger.getLogger(MyProfileController.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
        });

    }
}
