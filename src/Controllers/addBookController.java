package Controllers;

import dialogMaker.DialogMaker;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import db.DatabaseHandler;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import java.sql.SQLException;

import javafx.scene.control.ComboBox;

import java.sql.ResultSet;
import java.util.Arrays;

public class addBookController {

//    FXML ELEMENTS
    @FXML
    private StackPane root;
    @FXML
    private AnchorPane parentNode;
    @FXML
    private JFXTextField title;
    @FXML
    private JFXTextField author;
    @FXML
    private JFXTextField publisher;
    @FXML
    private JFXTextField edition;
    @FXML
    private JFXTextField copies;
    @FXML
    private ComboBox<String> category;
    @FXML
    private JFXTextField id;
    @FXML
    private JFXButton addBtn;


//    Creating DatabaseHandler Instance at Initializing the Controller
    DatabaseHandler databaseHandler;

//    Check if in editing mode or adding mode
    public static Boolean bookEditMode = false;

    public void initialize() {
        if(bookEditMode) {
            addBtn.setText("Edit Book");
        }
        else{
            addBtn.setText("Add Book");
        }
            databaseHandler = DatabaseHandler.getInstance();
            showCategory();
    }


//    Add Book Button Event Listener to Add Book to the Database
    @FXML
    void addBook() {

//    Get Data from User
        String bookTitle = title.getText();
        String bookAuthor = author.getText();
        String bookPublisher = publisher.getText();
        String bookEdition = edition.getText();
        String bookCopies = copies.getText();
        String bookCat = category.getValue();

        try {
//        Check if Number of Copies is Integer, and Handles if not
            Integer.parseInt(bookCopies);

//        Check if User filled Book details, and Handles if not
            if(bookTitle.isEmpty()) {
                JFXButton button = new JFXButton("Okay");
                DialogMaker.showDialog(root,parentNode, Arrays.asList(button),null, "Please Write the book Title");
                title.getStyleClass().add("wrong-entry");
                return;
            }
            else if(bookAuthor.isEmpty()) {
                JFXButton button = new JFXButton("Okay");
                DialogMaker.showDialog(root,parentNode, Arrays.asList(button),null, "Please Write the book Author");
                author.getStyleClass().add("wrong-entry");
                return;
            }
            else if(bookPublisher.isEmpty()){
                JFXButton button = new JFXButton("Okay");
                DialogMaker.showDialog(root,parentNode, Arrays.asList(button),null, "Please Write the book Publisher");
                publisher.getStyleClass().add("wrong-entry");
                return;
            }
            else if(bookEdition.isEmpty()){
                JFXButton button = new JFXButton("Okay");
                DialogMaker.showDialog(root,parentNode, Arrays.asList(button),null, "Please Write the book Edition");
                edition.getStyleClass().add("wrong-entry");
                return;
            }
            else if(bookCat==null) {
                JFXButton button = new JFXButton("Okay");
                DialogMaker.showDialog(root,parentNode, Arrays.asList(button),null,"Please Choose the book Category");
                title.getStyleClass().add("wrong-entry");
                return;
            }


//        Check if is in Edit Mode
            else if(bookEditMode){
                handleEditOperation();
                return;
            }

//        Insert Book Into Database and prevent Addition if the Book Title and Edition Already exist
            else {
                String query = "INSERT INTO BOOK (title,author,publisher,edition,copies,category) SELECT " +
                        "'" + bookTitle + "'," +
                        "'" + bookAuthor + "'," +
                        "'" + bookPublisher + "'," +
                        "'" + bookEdition + "'," +
                        "'" + bookCopies + "'," +
                        "'" + bookCat + "'" +
                        " WHERE NOT EXISTS (" +
                        "SELECT title FROM BOOK WHERE title=" +
                        "'" + bookTitle + "'" +
                        "AND edition=" +
                        "'" + bookEdition + "'" +
                        ")LIMIT 1;";

                if (databaseHandler.execAction(query)) {
                    JFXButton button = new JFXButton("Okay");
                    DialogMaker.showDialog(root,parentNode, Arrays.asList(button),null,"Book Added Successfully");

                } else {
                    JFXButton button = new JFXButton("Okay");
                    DialogMaker.showDialog(root,parentNode, Arrays.asList(button),null,"Failed to add the book");
                }
            }

            resetInput();

        }
        catch (NumberFormatException e) {
            copies.getStyleClass().add("wrong-entry");
            JFXButton button = new JFXButton("Okay");
            DialogMaker.showDialog(root,parentNode, Arrays.asList(button),null,"Please Enter a valid Number of Copies");
        }
    }

//    Cancel Button Event Listener to Close the Form
    @FXML
    void cancel(ActionEvent event) {
        ((Stage) root.getScene().getWindow()).close();
    }

//    Show Categories Inside the ComboBox
    private void showCategory(){
        ResultSet result = databaseHandler.execQuery("Select Category from CATEGORY");
        try {
            while (result.next()) {
                category.getItems().addAll(result.getString("Category"));
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }

    }

//    Populate Data of selected Book from MainController in the Edit Form
    public void populateEditForm(classes.Book book){
        title.setText(book.getTitle());
        author.setText(book.getAuthor());
        publisher.setText(book.getPublisher());
        edition.setText(book.getEdition());
        copies.setText(String.valueOf(book.getCopies()));
        id.setText(String.valueOf(book.getBookID()));

    }

//    Handle the Edit Operation by taking the New/Edited Data
//    And Send it to the updateBook Function in DatabaseHandler
    private void handleEditOperation() {
        title.getText();
        author.getText();
        publisher.getText();
        edition.getText();
        int bookID = Integer.parseInt(id.getText());
        int bookCopies = Integer.parseInt(copies.getText());

        if (databaseHandler.updateBook(title.getText(),author.getText(),publisher.getText(),edition.getText(), bookCopies,bookID)) {

            JFXButton button = new JFXButton("Okay");
            DialogMaker.showDialog(root,parentNode, Arrays.asList(button),"Success","Book Updated");

        } else {
            JFXButton button = new JFXButton("Okay");
            DialogMaker.showDialog(root,parentNode, Arrays.asList(button),"Failed","Cant update Book");

        }
    }

//    Reset Input Fields after Submission
    private void resetInput(){
        title.setText("");
        title.getStyleClass().remove("wrong-entry");

        author.setText("");
        author.getStyleClass().remove("wrong-entry");

        publisher.setText("");
        publisher.getStyleClass().remove("wrong-entry");

        edition.setText("");
        edition.getStyleClass().remove("wrong-entry");

        copies.setText("");
        copies.getStyleClass().remove("wrong-entry");

        category.setValue(null);
    }

}
