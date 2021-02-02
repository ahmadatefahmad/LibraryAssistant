package Controllers;

import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import db.DatabaseHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class loginController {


    //    FXML ELEMENTS
    @FXML
    private JFXTextField username;
    @FXML
    private JFXPasswordField password;


//    Creating DatabaseHandler Instance at Initializing the Controller
    DatabaseHandler databaseHandler;

//    Variables to Save Logged Username and Password
    public static String loggedUser = null;
    public static String loggedUserPass = null;
//    Flag to disable user deletion if only one user exist
    public static boolean deleteDisabled = true;

    public void initialize() {
        databaseHandler = databaseHandler.getInstance();
    }

    @FXML
    private void handleLoginButtonAction() throws IOException {

//        Get User Input
        String user = username.getText();
        String pWord = password.getText();

//        Arrays to store users and passwords retrieved from the database
        ArrayList<String> users = new ArrayList<>();
        ArrayList<String> passwords = new ArrayList<>();

//        Get Accounts from Login Table in Database
        String query = "SELECT * FROM LOGIN";
        ResultSet result = databaseHandler.execQuery(query);

        try {
            while (result.next()) {

//                store username and password in the lists
                users.add(result.getString("user"));
                passwords.add(result.getString("pword"));
            }
        }
        catch (SQLException e) { e.printStackTrace(); }

        if(users.size() <= 1){
            deleteDisabled = false;
        }
//        Check if User Input Matches the Accounts Retrieved from the Database
        for(int i = 0; i < users.size(); i++){
            if (users.get(i).equals(user) && passwords.get(i).equals(pWord)) {

//                Save Logged Username and Password
                loggedUser = users.get(i);
                loggedUserPass = passwords.get(i);

//            Close Login Form and Invoke loadMain Function
                ((Stage) username.getScene().getWindow()).close();
                loadMain();


            }
            else {
                username.getStyleClass().add("wrong-entry");
                password.getStyleClass().add("wrong-entry");
            }
        }


    }

//    Cancel Button Event Listener to Exit the Program
    @FXML
    private void handleCancelButtonAction() {
        System.exit(0);
    }


//    Load the Main.FXML
    public static void loadMain() throws IOException {
            Parent parent = FXMLLoader.load(loginController.class.getResource("../Views/main.fxml"));
            Stage stage = new Stage(StageStyle.DECORATED);
            stage.setTitle("Library Assistant");
            stage.setScene(new Scene(parent));
            stage.show();
        }
}
