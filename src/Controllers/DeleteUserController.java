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
import java.util.ArrayList;
import java.util.Arrays;

public class DeleteUserController {

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
//        Set Logged in username on the Edit Form, not editable
        username.setText(loginController.loggedUser);
        username.setEditable(false);
    }


    @FXML
    void deleteUser(){

//        Get User Input
        String loggedUser = loginController.loggedUser;
        String loggedUserPass = loginController.loggedUserPass;
        String pass = password.getText();
        String passConf = passwordConf.getText();



//        Check if Input Was Empty
        if (pass.isEmpty()) {
            JFXButton button = new JFXButton("Okay");
            DialogMaker.showDialog(root, parentNode, Arrays.asList(button), null, "Please, Write Your Password");
            return;
        }
//        Check if user didn't enter the old password correctly
        else if (!(pass.equals(loggedUserPass))){
            JFXButton button = new JFXButton("Failed");
            DialogMaker.showDialog(root, parentNode, Arrays.asList(button), null, "You entered Wrong Password");
            password.getStyleClass().add("wrong-entry");
            return;
        }

//        Check if new password confirmation doesn't match
        else if (!(pass.equals(passConf))) {

            JFXButton button = new JFXButton("Failed");
            DialogMaker.showDialog(root, parentNode, Arrays.asList(button), null, "Password Confirmation Doesn't Match");
            passwordConf.getStyleClass().add("wrong-entry");
            return;
        }

        else {
//            Delete user From the database
            String deleteQuery = "DELETE FROM LOGIN WHERE user = '" + loggedUser + "'";

            if (databaseHandler.execAction(deleteQuery)) {
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
                DialogMaker.showDialog(root, parentNode, Arrays.asList(button), null, "User Deleted Successfully, you will be Logged out Now");

            }
            else {
                JFXButton button = new JFXButton("Okay");
                DialogMaker.showDialog(root, parentNode, Arrays.asList(button), null, "Failed to Delete the User");
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

