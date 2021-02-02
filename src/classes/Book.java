package classes;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class Book{
    private final SimpleIntegerProperty bookID;
    private final SimpleStringProperty title;
    private final SimpleStringProperty author;
    private final SimpleStringProperty publisher;
    private final SimpleStringProperty edition;
    private final SimpleStringProperty category;
    private final SimpleIntegerProperty copies;
    private final SimpleIntegerProperty issued;

    public Book(int bookID, String title, String author, String publisher, String edition, String category, int copies, int issued){
        this.bookID = new SimpleIntegerProperty(bookID);
        this.title = new SimpleStringProperty(title);
        this.author = new SimpleStringProperty(author);
        this.publisher = new SimpleStringProperty(publisher);
        this.edition = new SimpleStringProperty(edition);
        this.category = new SimpleStringProperty(category);
        this.copies = new SimpleIntegerProperty(copies);
        this.issued = new SimpleIntegerProperty(issued);

    }
    public int getBookID(){
        return bookID.get();
    }
    public String getTitle() {
        return title.get();
    }
    public String getAuthor() {
        return author.get();
    }
    public String getPublisher() {
        return publisher.get();
    }
    public String getEdition() {
        return edition.get();
    }
    public String getCategory() {
        return category.get();
    }
    public int getCopies() {
        return copies.get();
    }
    public int getIssued() {
        return issued.get();
    }
}