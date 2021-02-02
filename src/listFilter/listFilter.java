package listFilter;

import classes.Book;
import classes.IssuedBook;
import classes.Member;
import com.jfoenix.controls.JFXTextField;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.scene.control.TableView;

import java.util.function.Predicate;

public class listFilter {

    public static void recordFilter(ObservableList<IssuedBook> list, JFXTextField searchField, TableView listView){

//        Create Filtered list of type Issued
        FilteredList<IssuedBook> filteredIssuedList = new FilteredList<>(list, e-> true);

//        searchField Key Press event Handler
        searchField.setOnKeyPressed(e ->{

//            Event Handler of Input Text
            searchField.textProperty().addListener(((observableValue, oldVal, newVal) ->{

//                Add Books into FilteredIssuedList
                filteredIssuedList.setPredicate((Predicate<? super IssuedBook>) issued ->{

//                    Check if user is searching
                    if(newVal == null || newVal.isEmpty()){
                        return true;
                    }

//                    Check if user Input Matches with Issued Book details in the Table
                    if(issued.getBookIssued().toLowerCase().contains(newVal.toLowerCase())){
                        return true;
                    }
                    else if(issued.getMemberIssued().toLowerCase().contains(newVal.toLowerCase())){
                        return true;
                    }
                    else if(issued.getMemberPhoneIssued().toLowerCase().contains(newVal.toLowerCase())){
                        return true;
                    }
                    else if(issued.getIssuanceDate().toLowerCase().contains(newVal.toLowerCase())){
                        return true;
                    }
                    else if(issued.getReturnDate().toLowerCase().contains(newVal.toLowerCase())){
                        return true;
                    }
                    return false;
                });
            }));

//        Populate the issuedListView with Issued Books in the filteredIssuedList
            listView.setItems(filteredIssuedList);
        });
    }
    public static void memberFilter(ObservableList<Member> memberList, JFXTextField searchMember, TableView memberListView){
        //        Create Filtered list of type Member
        FilteredList<Member> filteredMemberList = new FilteredList<>(memberList, e-> true);

//        searchMember textField Key Press event Handler
        searchMember.setOnKeyPressed(e ->{

//            Event Handler of Input Text
            searchMember.textProperty().addListener(((observableValue, oldVal, newVal) ->{

//                Add Members into filteredMemberList
                filteredMemberList.setPredicate((Predicate<? super Member>) member ->{

//                    Check if user is searching
                    if(newVal == null || newVal.isEmpty()){
                        return true;
                    }

                    if(member.getFName().toLowerCase().contains(newVal.toLowerCase())){
                        return true;
                    }
                    else if(member.getFName().toLowerCase().contains(newVal.toLowerCase())){
                        return true;
                    }
                    else if(member.getLName().toLowerCase().contains(newVal.toLowerCase())){
                        return true;
                    }
                    else if(member.getAddress().toLowerCase().contains(newVal.toLowerCase())){
                        return true;
                    }
                    else if(member.getPhone().toLowerCase().contains(newVal.toLowerCase())){
                        return true;
                    }
                    else if(member.getEmail().toLowerCase().contains(newVal.toLowerCase())){
                        return true;
                    }
                    return false;
                });
            }));

//        Populate the memberListView with Books in the filteredMemberList
            memberListView.setItems(filteredMemberList);
        });
    }
    public static void bookFilter(ObservableList<Book> bookList, JFXTextField searchBook, TableView bookListView){
        //        Create Filtered list of type Book
        FilteredList<Book> filteredBookList = new FilteredList<>(bookList, e-> true);

//        searchBook textField Key Press event Handler
        searchBook.setOnKeyPressed(e ->{

//            Event Handler of Input Text
            searchBook.textProperty().addListener(((observableValue, oldVal, newVal) ->{

//                Add Books into FilteredBookList
                filteredBookList.setPredicate((Predicate<? super Book>) book ->{

//                    Check if user is searching
                    if(newVal == null || newVal.isEmpty()){
                        return true;
                    }

//                    Check if user Input Matches with Book details in the Table
                    if(book.getTitle().toLowerCase().contains(newVal.toLowerCase())){
                        return true;
                    }
                    else if(book.getAuthor().toLowerCase().contains(newVal.toLowerCase())){
                        return true;
                    }
                    else if(book.getPublisher().toLowerCase().contains(newVal.toLowerCase())){
                        return true;
                    }
                    else if(book.getEdition().toLowerCase().contains(newVal.toLowerCase())){
                        return true;
                    }
                    else if(book.getCategory().toLowerCase().contains(newVal.toLowerCase())){
                        return true;
                    }
                    return false;
                });
            }));

//        Populate the bookListView with Books in the filteredBookList
            bookListView.setItems(filteredBookList);
        });
    }
}
