package Controllers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import db.DatabaseHandler;
import dialogMaker.DialogMaker;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;

public class editUserController {

//    FXML ELEMENTS
    @FXML
    private StackPane root;
    @FXML
    private Pane parentNode;
    @FXML
    private JFXTextField loggedUser;
    @FXML
    private JFXTextField newUsername;
    @FXML
    private JFXPasswordField oldPassword;
    @FXML
    private JFXPasswordField newPassword;
    @FXML
    private JFXPasswordField newPasswordConf;


    //    Creating DatabaseHandler Instance at Initializing the Controller
    DatabaseHandler databaseHandler;

    public void initialize() {
        databaseHandler = databaseHandler.getInstance();
//        Set Logged in username on the Edit Form, not editable
        loggedUser.setText(loginController.loggedUser);
        loggedUser.setEditable(false);
    }

    @FXML
    void editUser(){

//        Get Logged in username and password from loginController
        String loggedUser = loginController.loggedUser;
        String loggedUserPass = loginController.loggedUserPass;

//        Get user Input
        String oldPass = oldPassword.getText();
        String newUser = newUsername.getText();
        String newPass = newPassword.getText();
        String newPassConf = newPasswordConf.getText();

//         Create Array to save the users and password in the database
        ArrayList<String> users = new ArrayList<>();
        ArrayList<String> passwords = new ArrayList<>();

//        Get Accounts from Login Table in Database
        String query = "SELECT * FROM LOGIN";
        ResultSet result = databaseHandler.execQuery(query);
        try {
            while (result.next()) {
//                Save the users in the database in the users list, except the logged in user
                users.add(result.getString("user"));
                users.remove(loggedUser);
//                Save the passwords in the database in the passwords list
                passwords.add(result.getString("pword"));
            }
        }
        catch (SQLException e) { e.printStackTrace();
        }

//        Check if Input Was Empty
        if (oldPass.isEmpty() || newUser.isEmpty() || newPass.isEmpty()) {
            JFXButton button = new JFXButton("Okay");
            DialogMaker.showDialog(root, parentNode, Arrays.asList(button), null, "Please, Fill in the New Username And Password");
            return;
        }

//        Check if new username Already Exists
        else if (users.contains(newUser)) {
            JFXButton button = new JFXButton("Okay");
            DialogMaker.showDialog(root, parentNode, Arrays.asList(button), null, "Username "+newUser+" already Exists");
            newUsername.getStyleClass().add("wrong-entry");
            return;
        }

//        Check if user didn't enter the old password correctly
        else if (!(oldPass.equals(loggedUserPass))){
            JFXButton button = new JFXButton("Failed");
            DialogMaker.showDialog(root, parentNode, Arrays.asList(button), null, "You entered Wrong Password");
            oldPassword.getStyleClass().add("wrong-entry");
            return;
        }

//        CHeck if new password confirmation doesn't match
        else if (!(newPass.equals(newPassConf))) {
            JFXButton button = new JFXButton("Failed");
            DialogMaker.showDialog(root, parentNode, Arrays.asList(button), null, "Passwords Don't Match");
            newPassword.getStyleClass().add("wrong-entry");
            newPasswordConf.getStyleClass().add("wrong-entry");
            return;
        }
        else {

//            Update username and password in the database
            String updateUser = "UPDATE LOGIN SET user = '" +newUser+ "' WHERE user = '"+loggedUser+"'";
            String updatePass = "UPDATE LOGIN SET pword = '" +newPass+"' WHERE pword = '"+loggedUserPass+"'";
            if (databaseHandler.execAction(updateUser) && databaseHandler.execAction(updatePass)) {
                JFXButton button = new JFXButton("Okay");
                button.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent event1) -> {

//                    Loggin out the user by running the logging in form and closing the Edit Form
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
                DialogMaker.showDialog(root, parentNode, Arrays.asList(button), null, "User Updated Successfully, you will be Logged out Now");
            }
            else {
                JFXButton button = new JFXButton("Okay");
                DialogMaker.showDialog(root, parentNode, Arrays.asList(button), null, "Failed to Update the User");
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

