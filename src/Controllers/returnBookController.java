package Controllers;

import classes.Book;
import classes.IssuedBook;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPTable;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import db.DatabaseHandler;
import dialogMaker.DialogMaker;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;

import com.itextpdf.text.pdf.PdfWriter;
import javafx.stage.FileChooser;

import static listFilter.listFilter.*;

public class returnBookController {

//    FXML ELEMENTS
    @FXML
    private StackPane root;
    @FXML
    private AnchorPane parentNode;

    @FXML
    private JFXTextField searchField;
    @FXML
    private TableView<IssuedBook> listView;
    @FXML
    private TableColumn<IssuedBook, String> bookIssued;
    @FXML
    private TableColumn<IssuedBook, String> bookEditionIssued;
    @FXML
    private TableColumn<IssuedBook, String> memberIssued;
    @FXML
    private TableColumn<IssuedBook, String> memberPhoneIssued;
    @FXML
    private TableColumn<IssuedBook, String> issuanceDate;
    @FXML
    private TableColumn<IssuedBook, String> returnDate;
    @FXML
    private TableColumn<IssuedBook,Integer> renew;
    @FXML
    private MenuItem deleteRecord;
    @FXML
    private JFXButton returnBtn;
    @FXML
    private JFXButton renewBtn;


    public static boolean returnedView = false;

//    Creating DatabaseHandler Instance at Initializing the Controller
    DatabaseHandler databaseHandler;

    public void initialize(){
        databaseHandler = databaseHandler.getInstance();
        initIssuedCols();
        loadIssuedInfo();
    }

//      Initializing Tables Columns
    private void initIssuedCols(){
        bookIssued.setCellValueFactory(new PropertyValueFactory("bookIssued"));
        bookEditionIssued.setCellValueFactory(new PropertyValueFactory("bookEditionIssued"));
        memberIssued.setCellValueFactory(new PropertyValueFactory("memberIssued"));
        memberPhoneIssued.setCellValueFactory(new PropertyValueFactory("memberPhoneIssued"));
        issuanceDate.setCellValueFactory(new PropertyValueFactory("issuanceDate"));
        returnDate.setCellValueFactory(new PropertyValueFactory("returnDate"));
        renew.setCellValueFactory(new PropertyValueFactory("renew"));
    }


//      Load Issued Books
    private void loadIssuedInfo(){

//        Create Observable list of type Issued
        ObservableList<IssuedBook> issuedList = FXCollections.observableArrayList();
        ObservableList<IssuedBook> returnedList = FXCollections.observableArrayList();

//        Get Issued Books from Database
        String query = "SELECT * FROM ISSUED";
        ResultSet result = databaseHandler.execQuery(query);
        try {
            while (result.next()) {
                int bookID = result.getInt("bookID");
                int memberID = result.getInt("memberID");
                String issuanceDate = result.getString("issueDate");
                String returnDate = result.getString("returnDate");
                int returned = result.getInt("returned");
                int renew = result.getInt("renewCount");

//                 Get Issued Book Details
                String getBookQuery = "SELECT * FROM BOOK WHERE id = '" + bookID + "'";

                ResultSet book = databaseHandler.execQuery(getBookQuery);
                String bookIssued = null;
                String bookEditionIssued = null;
                while (book.next()) {
                    bookIssued = book.getString("title");
                    bookEditionIssued = book.getString("edition");
                }

//                 Get Member who Issued Information
                String getMemberQuery = "SELECT * FROM MEMBER WHERE id = '" + memberID + "'";
                ResultSet member = databaseHandler.execQuery(getMemberQuery);
                String memberIssued = null;
                String memberPhoneIssued = null;
                while (member.next()) {
                    memberIssued = member.getString("fName");
                    memberPhoneIssued = member.getString("phone");
                }

//                Add the retrieved data into the Issued Books List
                if(returned == 0){
                    issuedList.add(new IssuedBook(bookIssued,bookEditionIssued,memberIssued,memberPhoneIssued,issuanceDate,returnDate,returned, renew,bookID,memberID));
                }
                else if(returned ==1){
                    returnedList.add(new IssuedBook(bookIssued,bookEditionIssued,memberIssued,memberPhoneIssued,issuanceDate,returnDate,returned, renew,bookID,memberID));
                }
            }
        }
        catch(SQLException e){
            e.printStackTrace();
        }

        if(returnedView){
//        Populate the Issued TableView with Issued Books in the Issued List
            listView.setItems(returnedList);
            recordFilter(returnedList,searchField,listView);
            deleteRecord.setVisible(true);
        }
        else if(!returnedView){
//        Populate the Issued TableView with Issued Books in the Issued List
            listView.setItems(issuedList);
            recordFilter(issuedList,searchField,listView);
            deleteRecord.setVisible(false);
        }
    }


// DELETE ISSUED BOOK FROM ISSUED AFTER A SUCCESSFUL RETURN
// INCREMENT COPIES OF THE BOOK, AND DECREMENT RENTED BOOKS FOR A MEMBER
    @FXML
    void returnBookOperation(ActionEvent event){

//        Get Selected Issued Book
            IssuedBook selectedItem = listView.getSelectionModel().getSelectedItem();

//        Check if user selected Issued Book
            if(selectedItem != null){

//                Shows Confirmation Dialog
                JFXButton yesButton = new JFXButton("YES");
                yesButton.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent event1) -> {

//                Delete From Issued, Increment Copies of the Book
//                And Decrement Number of Issuance of the Book and Rented Books for the Member
                    String query1 = "UPDATE ISSUED SET returned  = 1 WHERE issueDate = '" + selectedItem.getIssuanceDate() + "'";
                    String query2 = "UPDATE BOOK SET copies = copies + 1 WHERE id = '" + selectedItem.getBookID() + "'";
                    String query3 = "UPDATE MEMBER SET rented = rented - 1 WHERE id = '" + selectedItem.getMemberID() + "'";

//                  Check if Database Operations were Executed Successfully
                    if (databaseHandler.execAction(query1) && databaseHandler.execAction(query2) && databaseHandler.execAction(query3)) {

                        JFXButton button = new JFXButton("Okay");
                        DialogMaker.showDialog(root,parentNode,Arrays.asList(button),"Success","The Book "+selectedItem.getBookIssued()+" Returned Successfully");

                        loadIssuedInfo();
                    }
                    else {
                        JFXButton button = new JFXButton("Okay");
                        DialogMaker.showDialog(root,parentNode,Arrays.asList(button),null,"Book Return Failed");
                    }
                });


                JFXButton noButton = new JFXButton("NO");
                noButton.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent event1) -> {
                    JFXButton button = new JFXButton("Okay");
                    DialogMaker.showDialog(root, parentNode, Arrays.asList(button), "Renewal Cancelled", "Renewal Operation Cancelled");

                });

                DialogMaker.showDialog(root, parentNode, Arrays.asList(yesButton, noButton), "Confirm Return Operation","Are you sure want to return the book " + selectedItem.getBookIssued() + " ?");

            }
            else{
                JFXButton button = new JFXButton("Okay");
                DialogMaker.showDialog(root,parentNode,Arrays.asList(button),"Failed","Please Select a Book to Return");
            }
    }

// UPDATE RETURN DATE, AND INCREMENT RENEW COUNT FOR THAT ISSUANCE BOOKS FOR A MEMBER
    @FXML
    void renewBookOperation(){

            //        Get Selected Issued Book
            IssuedBook selectedItem = listView.getSelectionModel().getSelectedItem();

//        Check if user selected Issued Book
            if(!(selectedItem == null)){

//                Shows Confirmation Dialog
                JFXButton yesButton = new JFXButton("YES");
                yesButton.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent event1) -> {

//                Update Return Date and Increment Renew Count for a Book
                    String query = "UPDATE ISSUED SET returnDate = NOW()::timestamp(0) + 7, renewCount = renewCount + 1  WHERE issueDate = '"+selectedItem.getIssuanceDate()+"'";

//                  Check if Database Operations were Executed Successfully
                    if (databaseHandler.execAction(query)) {


                        JFXButton button = new JFXButton("Okay");
                        DialogMaker.showDialog(root,parentNode,Arrays.asList(button),"Success","Book Renewed");

//                        RePopulate table with data and empty search field
                        loadIssuedInfo();
                        initialize();
                        searchField.setText("");

                    }
                    else {
                        JFXButton button = new JFXButton("Okay");
                        DialogMaker.showDialog(root,parentNode,Arrays.asList(button),null,"Book Renewal Failed");
                    }
                });

                JFXButton noButton = new JFXButton("NO");
                noButton.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent event1) -> {
                    JFXButton button = new JFXButton("Okay");
                    DialogMaker.showDialog(root, parentNode, Arrays.asList(button), "Renewal Cancelled", "Renewal Operation Cancelled");

                });

                DialogMaker.showDialog(root, parentNode, Arrays.asList(yesButton, noButton), "Confirm Renewal Operation","Are you sure want to Renew the book " + selectedItem.getBookIssued() + " ?");

            }
            else{
                JFXButton button = new JFXButton("Okay");
                DialogMaker.showDialog(root,parentNode,Arrays.asList(button),"Failed","Please select a book to Renew");
            }
    }

//    Print the issued books viewed in listView not in DB in XML and PDF files
    @FXML
    void exportRecords(){


    //        Create an array List of IssuedBooks are currently viewed in the listview
            ArrayList<IssuedBook> issuedBooks = new ArrayList<>();
            for(IssuedBook book : listView.getItems()){
                    issuedBooks.add(book);
                }

    //        Writes the IssuedBooks List in an XML file
            try{
                FileOutputStream fos = new FileOutputStream(new File("./data.xml"));
                XMLEncoder encoder = new XMLEncoder(fos);
                encoder.writeObject(issuedBooks);
                encoder.close();
                fos.close();
            }
            catch (IOException ex){
                ex.printStackTrace();
            }

    //        Writes the IssuedBooks List in a PDF file
            Document pdf = new Document();
            try{
                PdfWriter writer = PdfWriter.getInstance(pdf,new FileOutputStream("./data.pdf"));
                pdf.open();
                pdf.add(new Paragraph("Issued Books List"));

    //            Create Table with predefined column's titles and widths
                PdfPTable table = new PdfPTable(new float[] {2,2,2,2,4,4,1});
                table.setWidthPercentage(100);
                table.setSpacingBefore(5);
                table.addCell("Book Title");
                table.addCell("Book Edition");
                table.addCell("Member Name");
                table.addCell("Member Phone");
                table.addCell("Issuance Date");
                table.addCell("Return Date");
                table.addCell("Renewed");
                table.setHeaderRows(1);
                for (IssuedBook book : issuedBooks) {
                    table.addCell(book.getBookIssued());
                    table.addCell(book.getBookEditionIssued());
                    table.addCell(book.getMemberIssued());
                    table.addCell(book.getMemberPhoneIssued());
                    table.addCell(book.getIssuanceDate());
                    table.addCell(book.getReturnDate());
                    table.addCell(String.valueOf(book.getRenew()));
                }
                pdf.add(table);
                pdf.close();
                writer.close();
            }
            catch (DocumentException | FileNotFoundException e){
                e.printStackTrace();
            }
    }

//    Load IssuedBook List from an XML file
//    and if a book issuance doesn't exist already it gets added to the database
    @FXML
    void importRecords(){

//        To make sure all the books in the database are showing in the listview
    if(searchField.getText().equals("")){

//        File Chooser to choose the XML file from the computer
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose An XML File");

//        Adding Filter to prevent choosing files other than XML
        FileChooser.ExtensionFilter extFilter =
                new FileChooser.ExtensionFilter("XML files (*.xml)", "*.xml");

        fileChooser.getExtensionFilters().add(extFilter);
        File file = fileChooser.showOpenDialog(root.getScene().getWindow());

        if(file != null){
            try{
//                Decoding the Array of Issued Books
                FileInputStream fls = new FileInputStream(file);
                XMLDecoder decoder = new XMLDecoder(fls);

                ArrayList<IssuedBook> issuedBooksLoaded = (ArrayList<IssuedBook>) decoder.readObject();

//                count of new issuance records got added
//                and a flag to check an issuance record has been added
                int count=0;
                boolean isAdded=false;
//                Check if the book issuance already Exist
                for(IssuedBook loadedBook: issuedBooksLoaded){
//                    if it doesn't exist, it gets inserted to the DB
                    if(!listView.getItems().contains(loadedBook) && (loadedBook.getReturnedStatus() == 1)){
                        String query = "INSERT INTO ISSUED(memberID,bookID,issueDate,returnDate,returned,renewCount) VALUES ("
                                + "'" + loadedBook.getMemberID() + "',"
                                + "'" + loadedBook.getBookID() + "',"
                                + "'" + loadedBook.getIssuanceDate()+"',"
                                + "'" + loadedBook.getReturnDate()+"',"
                                + "'" + loadedBook.getReturnedStatus()+"',"
                                + "'" + loadedBook.getRenew()+"'"
                                + ")";
                        if(databaseHandler.execAction(query)){
//                            reload the ListView after inserting the book successfully
                         loadIssuedInfo();
                            count++;
                            isAdded = true;
                          }
                    }
                }

                decoder.close();
                fls.close();
//          show a message if an issuance record or not
                if(isAdded){
                    JFXButton button = new JFXButton("Okay");
                    DialogMaker.showDialog(root, parentNode, Arrays.asList(button), count+" Returned Book Record/s Got Added Successfully", null);

                }
                else {
                    JFXButton button = new JFXButton("Okay");
                    DialogMaker.showDialog(root, parentNode, Arrays.asList(button), "There is no new returned book record found", null);

                }


            }
            catch (IOException ex){
            ex.printStackTrace();
        }
        }
    }
    else{
        JFXButton button = new JFXButton("Okay");
        DialogMaker.showDialog(root, parentNode, Arrays.asList(button), "Please empty the search field before Loading the XML file", null);
    }
    }

    @FXML
    void viewReturned(){
        returnedView = true;
        returnBtn.setVisible(false);
        renewBtn.setVisible(false);
        loadIssuedInfo();

    }

    @FXML
    void viewIssued(){
        returnedView = false;
        returnBtn.setVisible(true);
        renewBtn.setVisible(true);
        loadIssuedInfo();

    }

    @FXML
    void delReturnedRec(){
//        Get Selected Book
        IssuedBook selectedItem = listView.getSelectionModel().getSelectedItem();

//        Check if user selected a Book
        if(selectedItem != null){

//                  Delete Book from the database
                String query = "DELETE FROM ISSUED WHERE issueDate ='"+ selectedItem.getIssuanceDate()+"'";

//                  Check if Database Operations were Executed Successfully
                if(databaseHandler.execAction(query)){
                    JFXButton button = new JFXButton("Okay");
                    DialogMaker.showDialog(root,parentNode,Arrays.asList(button),null,"Record is deleted successfully");
                    loadIssuedInfo();
                }
                else{
                    JFXButton button = new JFXButton("Okay");
                    DialogMaker.showDialog(root,parentNode,Arrays.asList(button),"Failed","Couldn't delete the record");
                }
            }
        else{
            JFXButton button = new JFXButton("Okay");
            DialogMaker.showDialog(root,parentNode,Arrays.asList(button),"No Book Selected","Please Select a Book to Delete");

        }
    }

}
