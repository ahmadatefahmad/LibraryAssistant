package classes;

public class IssuedBook {

     String bookIssued;
     String bookEditionIssued;
     String memberIssued;
     String memberPhoneIssued;
     String issuanceDate;
     String returnDate;
     int returnedStatus;
     int renew;
     int bookID;
     int memberID;

    public IssuedBook(){}

    public IssuedBook(String bookIssued, String bookEditionIssued, String memberIssued, String memberPhoneIssued, String issuanceDate, String returnDate, int returned, int renew,int bookID,int memberID){

        this.bookIssued =  bookIssued;
        this.bookEditionIssued = bookEditionIssued;
        this.memberIssued = memberIssued;
        this.memberPhoneIssued = memberPhoneIssued;
        this.issuanceDate = issuanceDate;
        this.returnDate = returnDate;
        this.returnedStatus = returned;
        this.renew = renew;
        this.bookID = bookID;
        this.memberID = memberID;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof IssuedBook && ((IssuedBook) obj).bookIssued.equals(bookIssued) && ((IssuedBook) obj).issuanceDate.equals(issuanceDate);
    }

    public String getBookIssued() {
        return bookIssued;
    }

    public void setBookIssued(String bookIssued) {
        this.bookIssued = bookIssued;
    }

    public String getBookEditionIssued() {
        return bookEditionIssued;
    }

    public void setBookEditionIssued(String bookEditionIssued) {
        this.bookEditionIssued = bookEditionIssued;
    }

    public String getMemberIssued() {
        return memberIssued;
    }

    public void setMemberIssued(String memberIssued) {
        this.memberIssued = memberIssued;
    }

    public String getMemberPhoneIssued() {
        return memberPhoneIssued;
    }

    public void setMemberPhoneIssued(String memberPhoneIssued) {
        this.memberPhoneIssued = memberPhoneIssued;
    }

    public String getIssuanceDate() {
        return issuanceDate;
    }

    public void setIssuanceDate(String issuanceDate) {
        this.issuanceDate = issuanceDate;
    }

    public String getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(String returnDate) {
        this.returnDate = returnDate;
    }

    public int getReturnedStatus() { return returnedStatus; }

    public void setReturnedStatus(int returnedStatus) { this.returnedStatus = returnedStatus; }

    public int getRenew() {
        return renew;
    }

    public void setRenew(int renew) {
        this.renew = renew;
    }

    public int getBookID(){ return bookID;}


    public void setBookID(int bookID){this.bookID = bookID; }

    public int getMemberID(){ return memberID;}

    public void setMemberID(int memberID){this.memberID = memberID; }
}
