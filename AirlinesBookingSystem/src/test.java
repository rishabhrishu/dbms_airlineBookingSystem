/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import static java.util.jar.Pack200.Packer.PASS;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.*;
import static javafx.css.StyleOrigin.USER;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/**
 *
 * @author vips
 */
public class test extends Application {

    /**
     * @param args the command line arguments
     */
    static final String USER = "c##airlinedb";
    static final String PASS = "HOLY2pass";
    public static Connection con;

    @Override
    public void start(Stage primaryStage) throws ClassNotFoundException, SQLException {
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
            con = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:orcl", USER, PASS);
            Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("GeneralLoginWindow.fxml"));
            primaryStage.resizableProperty().setValue(Boolean.FALSE);
            primaryStage.setTitle("Login Window");
            root.setId("paneGeneralLogin");
            Scene scene = new Scene(root);

            //  primaryStage.setScene(scene);
            // Scene scene = new Scene(root);
            scene.getStylesheets().addAll(this.getClass().getResource("GeneralLogin.css").toExternalForm());
            primaryStage.setScene(scene);

            primaryStage.show();

        } catch (IOException ex) {
            Logger.getLogger(test.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public static void main(String[] args) {
        launch(args);
    }

}
