package Controllers;

import classes.Member;
import com.jfoenix.controls.JFXButton;
import dialogMaker.DialogMaker;
import com.jfoenix.controls.JFXTextField;
import db.DatabaseHandler;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.util.Arrays;


public class addMemberController {

//    FXML ELEMENTS
    @FXML
    private StackPane root;
    @FXML
    private AnchorPane parentNode;
    @FXML
    private JFXTextField fName;
    @FXML
    private JFXTextField lName;
    @FXML
    private JFXTextField address;
    @FXML
    private JFXTextField phone;
    @FXML
    private JFXTextField email;
    @FXML
    private JFXTextField id;
    @FXML
    private JFXButton addBtn;


//    Creating DatabaseHandler Instance while Initializing the Controller
    DatabaseHandler databaseHandler;

//    Check if in editing mode or adding mode
    public static Boolean memberEditMode = false;

    public void initialize() {
        if(memberEditMode) {
            addBtn.setText("Edit Member");
        }
        else{
            addBtn.setText("Add Member");
        }
        databaseHandler = DatabaseHandler.getInstance();
    }


//    Add Member Button Event Listener to Add Member to the Database
    @FXML
    void addMember(){

//    Get Data from User
        String firstName = fName.getText();
        String lastName = lName.getText();
        String memberAddress = address.getText();
        String memberPhone = phone.getText();
        String memberEmail = email.getText();


//    Check if User filled Member Information, and Handles if not
        if(firstName.isEmpty()) {
            JFXButton button = new JFXButton("Okay");
            DialogMaker.showDialog(root,parentNode, Arrays.asList(button),null, "Please Write the Member First Name");
            fName.getStyleClass().add("wrong-entry");
            return;
        }
        else if(lastName.isEmpty()) {
            JFXButton button = new JFXButton("Okay");
            DialogMaker.showDialog(root,parentNode, Arrays.asList(button),null, "Please Write the Member Last Name");
            lName.getStyleClass().add("wrong-entry");
            return;
        }
        else if(memberAddress.isEmpty()){
            JFXButton button = new JFXButton("Okay");
            DialogMaker.showDialog(root,parentNode, Arrays.asList(button),null, "Please Write the Member Address");
            address.getStyleClass().add("wrong-entry");
            return;
        }
        else if(memberPhone.isEmpty()){
            JFXButton button = new JFXButton("Okay");
            DialogMaker.showDialog(root,parentNode, Arrays.asList(button),null, "Please Write the Member Phone");
            phone.getStyleClass().add("wrong-entry");
            return;
        }
        else if(memberEmail.isEmpty()) {
            JFXButton button = new JFXButton("Okay");
            DialogMaker.showDialog(root,parentNode, Arrays.asList(button),null, "Please Write the Member Email Address");
            email.getStyleClass().add("wrong-entry");
            return;
        }

//    Check if is in Edit Mode
        else if(memberEditMode){

                handleEditOperation();
                return;
            }

//    Insert Member Into Database and prevent Addition if the Member First Name and Phone Number Already exist
        else {

            String query = "INSERT INTO MEMBER (fName,lName,address,phone,email) SELECT " +
                    "'" + firstName + "'," +
                    "'" + lastName + "'," +
                    "'" + memberAddress + "'," +
                    "'" + memberPhone + "'," +
                    "'" + memberEmail + "'" +
                    " WHERE NOT EXISTS (" +
                    "SELECT fName FROM MEMBER WHERE fName=" +
                    "'" + firstName + "'" +
                    "AND phone=" +
                    "'" + memberPhone + "'" +
                    ")LIMIT 1;";


            if (databaseHandler.execAction(query)) {
                JFXButton button = new JFXButton("Okay");
                DialogMaker.showDialog(root,parentNode, Arrays.asList(button),null,"Member Added Successfully");

            } else {
                JFXButton button = new JFXButton("Okay");
                DialogMaker.showDialog(root,parentNode, Arrays.asList(button),null,"Failed to add the Member");

            }
        }

        resetInput();

    }

//    Cancel Button Event Listener to Close the Form
    @FXML
    void cancel() {
        Stage stage = (Stage) root.getScene().getWindow();
        stage.close();
    }

//    Populate Data of selected Member from MainController in the Edit Form
    public void populateEditForm(Member member){
        fName.setText(member.getFName());
        lName.setText(member.getLName());
        address.setText(member.getAddress());
        phone.setText(member.getPhone());
        email.setText(member.getEmail());
        id.setText(String.valueOf(member.getMemberID()));

        memberEditMode = true;
    }

//    Handle the Edit Operation by taking the New/Edited Data
//    And Send it to the updateMember Function in DatabaseHandler
    private void handleEditOperation() {
        fName.getText();
        lName.getText();
        address.getText();
        phone.getText();
        email.getText();
        int memberID = Integer.parseInt(id.getText());

        if (databaseHandler.updateMember(fName.getText(),lName.getText(),address.getText(),phone.getText(), email.getText(),memberID)) {
            JFXButton button = new JFXButton("Okay");
            DialogMaker.showDialog(root,parentNode, Arrays.asList(button),"Success","Member Updated");
        } else {
            JFXButton button = new JFXButton("Okay");
            DialogMaker.showDialog(root,parentNode, Arrays.asList(button),"Failed","Cant update Member");

        }
    }

//    Reset Input Fields after Submission
    private void resetInput(){

        fName.setText("");
        fName.getStyleClass().remove("wrong-entry");

        lName.setText("");
        lName.getStyleClass().remove("wrong-entry");

        address.setText("");
        address.getStyleClass().remove("wrong-entry");

        phone.setText("");
        phone.getStyleClass().remove("wrong-entry");

        email.setText("");
        email.getStyleClass().remove("wrong-entry");
    }
}
