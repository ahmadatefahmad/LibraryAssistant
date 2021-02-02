package Controllers;

import classes.Book;
import classes.Member;
import com.jfoenix.controls.JFXTextField;
import dialogMaker.DialogMaker;
import com.jfoenix.controls.JFXButton;
import db.DatabaseHandler;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.function.Predicate;

import static listFilter.listFilter.bookFilter;
import static listFilter.listFilter.memberFilter;

public class issueBookController {

//    FXML ELEMENTS
    @FXML
    private StackPane root;
    @FXML
    private AnchorPane parentNode;

    // Book
    @FXML
    private JFXTextField searchBook;
    @FXML
    private TableView<Book> bookListView;
    @FXML
    private TableColumn<Book, Integer> bookID;
    @FXML
    private TableColumn<Book, String> titleCol;
    @FXML
    private TableColumn<Book, String> authorCol;
    @FXML
    private TableColumn<Book, String> editionCol;
    @FXML
    private TableColumn<Book, String> catCol;
    @FXML
    private TableColumn<Book, Integer> copiesCol;

    // Member
    @FXML
    private JFXTextField searchMember;
    @FXML
    private TableView<Member> memberListView;
    @FXML
    private TableColumn<Member,Integer> memberID;
    @FXML
    private TableColumn<Member,String> fNameCol;
    @FXML
    private TableColumn<Member,String> lNameCol;
    @FXML
    private TableColumn<Member,String> phoneCol;
    @FXML
    private TableColumn<Member,String> emailCol;
    @FXML
    private TableColumn<Member,Integer> rentedCol;

//    Creating DatabaseHandler Instance at Initializing the Controller
    DatabaseHandler databaseHandler;

    public void initialize(){
        databaseHandler = databaseHandler.getInstance();

        initCols();
        loadBookInfo();
        loadMemberInfo();
    }

//    Initializing Tables Columns
    private void initCols(){

//        Initializing BOOKS Table Columns
        bookID.setCellValueFactory(new PropertyValueFactory("bookID"));
        titleCol.setCellValueFactory(new PropertyValueFactory("title"));
        authorCol.setCellValueFactory(new PropertyValueFactory("author"));
        editionCol.setCellValueFactory(new PropertyValueFactory("edition"));
        copiesCol.setCellValueFactory(new PropertyValueFactory("copies"));
        catCol.setCellValueFactory(new PropertyValueFactory("category"));

//        Initializing MEMBERS Table Columns
        memberID.setCellValueFactory(new PropertyValueFactory("memberID"));
        fNameCol.setCellValueFactory(new PropertyValueFactory("fName"));
        lNameCol.setCellValueFactory(new PropertyValueFactory("lName"));
        phoneCol.setCellValueFactory(new PropertyValueFactory("phone"));
        emailCol.setCellValueFactory(new PropertyValueFactory("email"));
        rentedCol.setCellValueFactory(new PropertyValueFactory("rented"));
    }

//    Load Books
    private void loadBookInfo(){

//        Create Observable list of type Book
        ObservableList<Book> bookList = FXCollections.observableArrayList();

//        Get Books from Database
        String query = "SELECT * FROM BOOK";
        ResultSet result = databaseHandler.execQuery(query);
        try {
            while (result.next()) {
                int bookId = result.getInt("id");
                String bookTitle = result.getString("title");
                String bookAuthor = result.getString("author");
                String bookPublisher = result.getString("publisher");
                String bookEdition = result.getString("edition");
                String bookCat = result.getString("category");
                int bookCopies = result.getInt("copies");
                int bookIssued = result.getInt("issued");

//                Add the retrieved data into the Book List
                bookList.add(new Book(bookId,bookTitle,bookAuthor,bookPublisher,bookEdition,bookCat,bookCopies,bookIssued));
            }
        }
        catch(SQLException e){
            e.printStackTrace();
        }

//        Populate the Book TableView with Books in the Book List
        bookListView.setItems(bookList);

        bookFilter(bookList,searchBook,bookListView);
    }

//    Load Members
    private void loadMemberInfo(){

//        Create Observable list of type Member
        ObservableList<Member> memberList = FXCollections.observableArrayList();

//        Get Members from Database
        String query = "SELECT * FROM MEMBER";
        ResultSet result = databaseHandler.execQuery(query);
        try {
            while (result.next()) {
                int memberID = result.getInt("id");
                String fName = result.getString("fName");
                String lName = result.getString("lName");
                String address = result.getString("address");
                String phone = result.getString("phone");
                String email = result.getString("email");
                int rented = result.getInt("rented");

//                Add the retrieved data into the Member List
                memberList.add(new Member(memberID,fName,lName,address,phone,email,rented));
            }
        }
        catch(SQLException e){
            e.printStackTrace();
        }

//        Populate the Member TableView with Members in the Member List
        memberListView.setItems(memberList);

        memberFilter(memberList,searchMember,memberListView);
    }

// Insert BOOK ID AND MEMBER ID INTO ISSUED TABLE AFTER A SUCCESSFUL ISSUANCE
// DECREMENT COPIES OF THE BOOK, AND INCREMENT RENTED BOOKS FOR A MEMBER
    @FXML
    void issueBookOperation() {

//        Get Selected Book and Member
        Book selectedBook = bookListView.getSelectionModel().getSelectedItem();
        Member selectedMember =  memberListView.getSelectionModel().getSelectedItem();

//        Check if user selected Book and Member
        if(!(selectedBook == null || selectedMember == null)) {

//            Check if Number of Copies is above 0 to continue the issuance
            if (selectedBook.getCopies() > 0) {

//                Get the IDs of the Selected Book and Member
                int BookID = selectedBook.getBookID();
                int MemberID = selectedMember.getMemberID();

//                Shows Confirmation Dialog
                JFXButton yesButton = new JFXButton("YES");
                yesButton.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent event1) -> {

//                    Insert Issuance Details into Database, Decrement Copies of the Book
//                    And Increment Number of Issuance of the Book and Rented Books for the Member
                    String query1 = "INSERT INTO ISSUED(memberID,bookID,returnDate) VALUES ("
                            + "'" + MemberID + "',"
                            + "'" + BookID + "',"
                            + "NOW()::timestamp(0) + 7 "
                            + ")";
                    String query2 = "UPDATE BOOK SET copies = copies -1 WHERE id = '" + BookID + "'";
                    String query3 = "UPDATE BOOK SET issued = issued +1 WHERE id = '" + BookID + "'";
                    String query4 = "UPDATE MEMBER SET rented = rented +1 WHERE id = '" + MemberID + "'";

//                  Check if Database Operations were Executed Successfully
                    if (databaseHandler.execAction(query1) && databaseHandler.execAction(query2) && databaseHandler.execAction(query3) && databaseHandler.execAction(query4)) {
                        JFXButton button = new JFXButton("Okay");
                        DialogMaker.showDialog(root, parentNode, Arrays.asList(button), "Book Issue Complete", null);

//                        RePopulate tables with data and empty search fields
                        loadBookInfo();
                        loadMemberInfo();
                        searchBook.setText("");
                        searchMember.setText("");
                    }
                    else {
                        JFXButton button = new JFXButton("Okay");
                        DialogMaker.showDialog(root, parentNode, Arrays.asList(button), "Issue Operation Failed", null);
                    }
                });

                JFXButton noButton = new JFXButton("NO");
                noButton.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent event1) -> {
                    JFXButton button = new JFXButton("That's Okay");
                    DialogMaker.showDialog(root, parentNode, Arrays.asList(button), "Issue Cancelled", null);
                });

                DialogMaker.showDialog(root, parentNode, Arrays.asList(yesButton, noButton), "Confirm Issue", "Are you sure want to issue the book " + selectedBook.getTitle() + " to " + selectedMember.getFName() + " ?");
            }
        }
        else{
            JFXButton button = new JFXButton("Okay!");
            DialogMaker.showDialog(root, parentNode, Arrays.asList(button), "Issue Operation cancelled", "Please Choose a Book and a Member to complete the Issuance");
        }
    }
}
