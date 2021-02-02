package Controllers;

import classes.Book;
import classes.Member;
import com.jfoenix.controls.JFXTextField;
import dialogMaker.DialogMaker;
import com.jfoenix.controls.JFXButton;
import db.DatabaseHandler;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;

import static Controllers.addBookController.bookEditMode;
import static Controllers.addMemberController.memberEditMode;
import static Controllers.loginController.deleteDisabled;
import static Controllers.returnBookController.returnedView;
import static listFilter.listFilter.bookFilter;
import static listFilter.listFilter.memberFilter;


public class MainController{

//    FXML ELEMENTS
    @FXML
    private StackPane root;
    @FXML
    private BorderPane borderPane;
    @FXML
    private Text bookNum;
    @FXML
    private Text memberNum;
    @FXML
    private Text issuedNum;
    @FXML
    private Text loggedUser;
    @FXML
    private Button backBtn;
    @FXML
    private MenuItem showEdition;


//    Member
    @FXML
    private JFXTextField searchMember;
    @FXML
    private TableView<Member> memberListView;
    @FXML
    private TableColumn<Member, Integer> memberID;
    @FXML
    private TableColumn<Member, String> fName;
    @FXML
    private TableColumn<Member, String> lName;
    @FXML
    private TableColumn<Member, String> address;
    @FXML
    private TableColumn<Member, String> phone;
    @FXML
    private TableColumn<Member, String> email;
    @FXML
    private TableColumn<Member, Integer> rented;

//    Book
    @FXML
    private JFXTextField searchBook;
    @FXML
    private TableView<Book> bookListView;
    @FXML
    private TableColumn<Book, Integer> bookID;
    @FXML
    private TableColumn<Book, String> title;
    @FXML
    private TableColumn<Book, String> author;
    @FXML
    private TableColumn<Book, String> publisher;
    @FXML
    private TableColumn<Book, String> edition;
    @FXML
    private TableColumn<Book, Integer> copies;
    @FXML
    private TableColumn<Book, Integer> issued;
    @FXML
    private TableColumn<Book, String> category;




//    Creating DatabaseHandler Instance at Initializing the Controller
    DatabaseHandler databaseHandler;

    public void initialize(){
        databaseHandler = databaseHandler.getInstance();

        loggedUser.setText(loginController.loggedUser);

        backBtn.setVisible(false);

        initBooksCols();
        initMembersCols();

        loadBookInfo();
        loadMemberInfo();

        getTotalNum();
    }


// INITIALIZE BOOK LIST COLUMNS
    private void initBooksCols(){
        bookID.setCellValueFactory(new PropertyValueFactory("bookID"));
        title.setCellValueFactory(new PropertyValueFactory("title"));
        author.setCellValueFactory(new PropertyValueFactory("author"));
        publisher.setCellValueFactory(new PropertyValueFactory("publisher"));
        edition.setCellValueFactory(new PropertyValueFactory("edition"));
        category.setCellValueFactory(new PropertyValueFactory("category"));
        copies.setCellValueFactory(new PropertyValueFactory("copies"));
        issued.setCellValueFactory(new PropertyValueFactory("issued"));
    }

    String query = "SELECT * FROM BOOK";

    private void loadBookInfo(){

//        Create Observable list of type Book
        ObservableList<Book> bookList = FXCollections.observableArrayList();

        ResultSet result = databaseHandler.execQuery(query);
        try {
            while (result.next()) {
                int bookId = result.getInt("id");
                String bookTitle = result.getString("title");
                String bookAuthor = result.getString("author");
                String publisher = result.getString("publisher");
                String bookEdition = result.getString("edition");
                String bookCat = result.getString("category");
                int bookCopies = result.getInt("copies");
                int issued = result.getInt("issued");

//                Add the retrieved data into the Book List
                bookList.add(new Book(bookId,bookTitle,bookAuthor,publisher,bookEdition,bookCat,bookCopies,issued));
            }
        }
        catch(SQLException e){
            e.printStackTrace();
        }

//        Populate the Book TableView with Books in the Book List
        bookListView.setItems(bookList);

        bookFilter(bookList,searchBook,bookListView);
    }


//INITIALIZE MEMBER LIST COLUMNS
    private void initMembersCols(){
        memberID.setCellValueFactory(new PropertyValueFactory("memberID"));
        fName.setCellValueFactory(new PropertyValueFactory("fName"));
        lName.setCellValueFactory(new PropertyValueFactory("lName"));
        address.setCellValueFactory(new PropertyValueFactory("address"));
        phone.setCellValueFactory(new PropertyValueFactory("phone"));
        email.setCellValueFactory(new PropertyValueFactory("email"));
        rented.setCellValueFactory(new PropertyValueFactory("rented"));
    }
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

//                if(editionView){
//                    showBookEdition()
//                }
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

//    Load Window Function
    public void loadWindow(String location, String title){
        try {
            memberEditMode = bookEditMode = false;

            Parent parent = FXMLLoader.load(getClass().getResource(location));
            Stage stage = new Stage();
            stage.setTitle(title);
            stage.setScene(new Scene(parent));
            stage.show();

//            Check if the window opened window is Adding Book, Member or User
//            and close the main window
            if(title.contains("Add") || title.contains("User")){
                if(title.contains("User")) {
                    ((Stage) root.getScene().getWindow()).close();
                }
                stage.setResizable(false);
            }

//            reload data in the main window when the opened window gets closed
            stage.setOnHiding(new EventHandler<WindowEvent>() {
                public void handle(WindowEvent we) {
                    loadMemberInfo();
                    loadBookInfo();
                    getTotalNum();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


//  addBook Button Event Handler
    @FXML
    void addBook() { loadWindow("../Views/addBook.fxml","Add Book"); }

//  addMember Button Event Handler
    @FXML
    void addMember() {  loadWindow("../Views/addMember.fxml","Add Member"); }

//  issueBook Button Event Handler
    @FXML
    void issueBook() {  loadWindow("../Views/issueBook.fxml","Issue Book"); }

//  returnBook Button Event Handler
    @FXML
    void returnBook() { returnedView =false;
    loadWindow("../Views/returnBook.fxml","Return Book"); }

    @FXML
    void addUser(){  loadWindow("../Views/addUser.fxml","Add User");}

    @FXML
    void editUser(){  loadWindow("../Views/editUser.fxml","Edit User");}

    @FXML
    void deleteUser(){

//        Prevent loading if deletion is disabled
        if(deleteDisabled){
            loadWindow("../Views/deleteUser.fxml","Delete User");
        }
        else{
            JFXButton button = new JFXButton("Okay");
            DialogMaker.showDialog(root, borderPane, Arrays.asList(button), null, "Only one user exist, Deletion is Cancelled");

        }
    }



//    Delete Book from the database
    @FXML
    void bookDeleteOperation() {

//        Get Selected Book
        Book selectedBook = bookListView.getSelectionModel().getSelectedItem();

//        Check if user selected a Book
        if(selectedBook != null){

//                Shows Confirmation Dialog
            JFXButton yesButton = new JFXButton("YES");
            yesButton.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent event1) ->{

//                  Delete Book from the database
                String query = "DELETE FROM BOOK WHERE id ='"+ selectedBook.getBookID()+"'";

//                  Check if Database Operations were Executed Successfully
                if(databaseHandler.execAction(query)){
                    JFXButton button = new JFXButton("Okay");
                    DialogMaker.showDialog(root,borderPane,Arrays.asList(button),"Book Deleted","The Book "+selectedBook.getTitle()+" is Deleted Successfully");
                    loadBookInfo();
                    getTotalNum();
                }
                else{
                    JFXButton button = new JFXButton("Okay");
                    DialogMaker.showDialog(root,borderPane,Arrays.asList(button),"Failed","Cannot Delete the Book"+ selectedBook.getTitle()+" Because it's Issued");
                }
            });

            JFXButton noButton = new JFXButton("NO");
            noButton.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent event1) -> {
                JFXButton button = new JFXButton("Okay");
                DialogMaker.showDialog(root, borderPane, Arrays.asList(button), "Deletion Cancelled", "Deletion Process Cancelled");
            });

            DialogMaker.showDialog(root, borderPane, Arrays.asList(yesButton, noButton), "Confirm Delete Operation", "Are you sure want to Delete the Book " + selectedBook.getTitle() + " ?");

        }
        else{
            JFXButton button = new JFXButton("Okay");
            DialogMaker.showDialog(root,borderPane,Arrays.asList(button),"No Book Selected","Please Select a Book to Delete");
            return;
        }

    }

//    Delete Member from the database
    @FXML
    void memberDeleteOperation() {

//        Get Selected Member
        Member selectedMember =  memberListView.getSelectionModel().getSelectedItem();

//        Check if user selected a Member
        if(selectedMember != null){

//                Shows Confirmation Dialog
            JFXButton yesButton = new JFXButton("YES");
            yesButton.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent event1) ->{

//                  Delete Member from the database
                String query = "DELETE FROM MEMBER WHERE id ='"+ selectedMember.getMemberID()+"'";

//                  Check if Database Operations were Executed Successfully
                if(databaseHandler.execAction(query)){
                    JFXButton button = new JFXButton("Okay");
                    DialogMaker.showDialog(root,borderPane,Arrays.asList(button),"Member Deleted","The Member "+selectedMember.getFName()+" is Deleted Successfully");
                    loadMemberInfo();
                    getTotalNum();
                }
                else{
                    JFXButton button = new JFXButton("Okay");
                    DialogMaker.showDialog(root,borderPane,Arrays.asList(button),"Failed","Cannot Delete the Member"+ selectedMember.getFName()+" because the Member Issued a Book");
                }
            });

            JFXButton noButton = new JFXButton("NO");
            noButton.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent event1) -> {
                JFXButton button = new JFXButton("Okay");
                DialogMaker.showDialog(root, borderPane, Arrays.asList(button), "Deletion Cancelled", "Deletion Process Cancelled");
            });

            DialogMaker.showDialog(root, borderPane, Arrays.asList(yesButton, noButton), "Confirm Delete Operation", "Are you sure want to Delete the Member " + selectedMember.getFName() + " ?");

        }
        else{
            JFXButton button = new JFXButton("Okay");
            DialogMaker.showDialog(root,borderPane,Arrays.asList(button),"No Member Selected","Please Select a Member to Delete");
            return;
        }

    }

//    Edit Book in the database
    @FXML
    void editBookOperation() {

//        Get Selected Member
        Book selectedForEdit = bookListView.getSelectionModel().getSelectedItem();

//        Check if user selected a Book
        if (selectedForEdit == null) {
            JFXButton button = new JFXButton("Okay");
            DialogMaker.showDialog(root,borderPane,Arrays.asList(button),"No Book Selected","Please Select Book to Delete");
            return;
        }

        try {
//            Flag for the controller to distinguish between Adding and Editing a Book
            bookEditMode = true;
//            Load addBook.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../Views/addBook.fxml"));
            Parent parent = loader.load();

//            Load addBookController
            addBookController controller = loader.getController();

//            invoke populateEditForm Function
            controller.populateEditForm(selectedForEdit);
            Stage stage = new Stage();
            stage.setTitle("Edit Book");
            stage.setResizable(false);
            stage.setScene(new Scene(parent));
            stage.show();
            stage.setOnHiding(new EventHandler<WindowEvent>() {
                public void handle(WindowEvent we) {
                    loadBookInfo();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

//    Edit Member in the database
    @FXML
    void memberEditOperation() {

//        Get Selected Member
        Member selectedForEdit = memberListView.getSelectionModel().getSelectedItem();

//        Check if user selected a Member
        if (selectedForEdit == null) {
            JFXButton button = new JFXButton("Okay");
            DialogMaker.showDialog(root,borderPane,Arrays.asList(button),"No Member Selected","Please Select a Member to Edit");
            return;
        }

        try {
//            Flag for the controller to distinguish between Adding and Editing a Member
            memberEditMode = true;
//            Load addMember.fxml & it's controller
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../Views/addMember.fxml"));
            Parent parent = loader.load();

//            Load addBookController
            addMemberController controller = loader.getController();

//            invoke populateEditForm Function
            controller.populateEditForm(selectedForEdit);
            Stage stage = new Stage();
            stage.setTitle("Edit Member");
            stage.setResizable(false);
            stage.setScene(new Scene(parent));
            stage.show();
            stage.setOnHiding(new EventHandler<WindowEvent>() {
                public void handle(WindowEvent we) {
                    loadMemberInfo();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @FXML
    void showBookEdition(){
        Book selectedBook = bookListView.getSelectionModel().getSelectedItem();
        query = "SELECT * FROM BOOK WHERE title ='"+selectedBook.getTitle()+"'";
        loadBookInfo();
        backBtn.setVisible(true);
        showEdition.setVisible(false);

    }

    @FXML
    void showAllBooks(){
        query = "SELECT * FROM BOOK";
        loadBookInfo();
        backBtn.setVisible(false);
        showEdition.setVisible(true);
    }

// Display Total Number of Books, Members and Issued Books
    void getTotalNum(){

//      Get Number of Books, Members and Issued Books
        String totalBooksQuery = "SELECT id, COUNT(*) FROM BOOK GROUP BY id";
        String totalMembersQuery = "SELECT id, COUNT(*) FROM MEMBER GROUP BY id";
        String totalIssuedQuery = "SELECT id, COUNT(*) FROM ISSUED GROUP BY id ";


        try{
            ResultSet result = databaseHandler.execQuery(totalBooksQuery);
            while(result.next()){
//                Set Number of Rows of Books in bookNum
                bookNum.setText(String.valueOf(result.getRow()));
            }
        } catch(SQLException e){
            e.printStackTrace();
        }
        try{
            ResultSet result = databaseHandler.execQuery(totalMembersQuery);
            while(result.next()){

//                Set Number of Rows of Members in memberNum
                memberNum.setText(String.valueOf(result.getRow()));
            }
        } catch(SQLException e){
            e.printStackTrace();
        }
        try{
            ResultSet result = databaseHandler.execQuery(totalIssuedQuery);
            while(result.next()){
//                Set Number of Rows of IssuedBooks in issuedNum
                issuedNum.setText(String.valueOf(result.getRow()));
            }
        } catch(SQLException e){
            e.printStackTrace();
        }

    }
}


