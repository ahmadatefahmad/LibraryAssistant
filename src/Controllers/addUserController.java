package Controllers;


import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import db.DatabaseHandler;
import dialogMaker.DialogMaker;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class addUserController {

    @FXML
    private StackPane root;
    @FXML
    private Pane parentNode;
    @FXML
    private JFXTextField username;
    @FXML
    private JFXPasswordField password;
    @FXML
    private JFXPasswordField passwordConf;

    //    Creating DatabaseHandler Instance at Initializing the Controller
    DatabaseHandler databaseHandler;

    public void initialize() {
        databaseHandler = databaseHandler.getInstance();
    }


    @FXML
    void addUser(){

//        Get User Input
        String userName = username.getText();
        String pWord = password.getText();
        String pWordConf = passwordConf.getText();

//        Arrays to store users  retrieved from the database
        ArrayList<String> users = new ArrayList<>();

        String query = "SELECT * FROM LOGIN";
        ResultSet result = databaseHandler.execQuery(query);
        try {
            while (result.next()) {
//                Storing users in the list
                users.add(result.getString("user"));
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }

//        Check if Input Was Empty
        if (userName.isEmpty() || pWord.isEmpty()) {
            JFXButton button = new JFXButton("Okay");
            DialogMaker.showDialog(root, parentNode, Arrays.asList(button), null, "Please, Fill in the Username And Password");
            return;
        }

//        Check if new username Already Exists
        else if (users.contains(userName)) {
            JFXButton button = new JFXButton("Okay");
            DialogMaker.showDialog(root, parentNode, Arrays.asList(button), null, "Username already Exists");
            username.getStyleClass().add("wrong-entry");
            return;
        }

//        Check if new password confirmation doesn't match
        else if (!(pWord.equals(pWordConf))) {

            JFXButton button = new JFXButton("Failed");
            DialogMaker.showDialog(root, parentNode, Arrays.asList(button), null, "Password Confirmation Doesn't Match");
            passwordConf.getStyleClass().add("wrong-entry");
            return;
        }

        else {

//            Insert user into the database
            String insertQuery = "INSERT INTO LOGIN (user,pword) VALUES ('" + userName + "', '" + pWord + "')";

            if (databaseHandler.execAction(insertQuery)) {
                JFXButton button = new JFXButton("Okay");
                button.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent event1) -> {

                    Parent parent = null;
                    try {
                        parent = FXMLLoader.load(getClass().getResource("../Views/login.fxml"));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    Stage stage = new Stage();
                    stage.setTitle("Login");
                    stage.setScene(new Scene(parent));

                    ((Stage) root.getScene().getWindow()).close();
                    stage.show();

                });
                DialogMaker.showDialog(root, parentNode, Arrays.asList(button), null, "User Added Successfully, you will be Logged out Now");

            }
            else {
                JFXButton button = new JFXButton("Okay");
                DialogMaker.showDialog(root, parentNode, Arrays.asList(button), null, "Failed to add the User");
            }
        }
    }

//    Load Main FXML at Cancel Button Event
    @FXML
    void cancel() throws IOException {
        loginController.loadMain();
        ((Stage) root.getScene().getWindow()).close();
    }


}
