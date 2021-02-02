package db;

import java.sql.*;

public final class DatabaseHandler {

    private static DatabaseHandler handler;

    //    DB Variables
    private static final String DB_URL = "jdbc:h2:~/libraryDB";

//      DB URL for Mysql Instead of the embedded H2 DB
//    private static final String DB_URL = "jdbc:mysql://localhost:3306/library";

    private static Connection conn = null;
    private Statement stmt = null;

    //    class constructor calls functions
    private DatabaseHandler() {
        createConnection();
        setupLoginTable();
        setupBookTable();
        setupMemberTable();
        setupCategoryTable();
        setupIssuedTable();
    }

    public static DatabaseHandler getInstance(){
        if(handler==null){
            handler = new DatabaseHandler();
        }
        return handler;
    }

    //    Connect to DB
    void createConnection() {
        try {
//            You can remove "Class.forName" if you are using A JDK > 1.8
            Class.forName("org.h2.Driver");

            conn = DriverManager.getConnection(DB_URL,"root","underscore");
            System.out.println("connected");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //    Setup BOOK Table
    void setupBookTable() {
        try {
            stmt = conn.createStatement();
            DatabaseMetaData dbm = conn.getMetaData();
            ResultSet tables = dbm.getTables(null, null, "BOOK", null);

            if (tables.next()) {
                System.out.println("Table BOOK already exists.");
            } else {
                stmt.execute("CREATE TABLE BOOK ("
                        + "	id int AUTO_INCREMENT PRIMARY KEY,\n"
                        + "	title varchar(200),\n"
                        + "	author varchar(200),\n"
                        + "	publisher varchar(100),\n"
                        + "	edition varchar(100) ,\n"
                        + "	copies integer DEFAULT 1 NOT NULL ,"
                        + "	issued integer DEFAULT 0 NOT NULL ,"
                        + "	category varchar(100)"
                        + " )");
                System.out.println("book Table created");
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage() + " --- setupDatabase");
        }
    }

    //    Setup MEMBER Table
    void setupMemberTable() {
        try {
            stmt = conn.createStatement();
            DatabaseMetaData dbm = conn.getMetaData();
            ResultSet tables = dbm.getTables(null, null, "MEMBER", null);

            if (tables.next()) {
                System.out.println("Table MEMBER already exists.");
            } else {
                stmt.execute("CREATE TABLE MEMBER ("
                        + "	id int AUTO_INCREMENT PRIMARY KEY,\n"
                        + "	fName varchar(100),\n"
                        + "	lName varchar(100),\n"
                        + "	address varchar(200),\n"
                        + "	phone varchar(100),\n"
                        + "	email varchar(100),\n"
                        + "	rented int DEFAULT 0"
                        + " )");
                System.out.println("MEMBER Table created");
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage() + " --- setupDatabase");
        }
    }

    //    Setup CATEGORY Table
    void setupCategoryTable() {
        try {
            stmt = conn.createStatement();
            DatabaseMetaData dbm = conn.getMetaData();
            ResultSet tables = dbm.getTables(null, null, "CATEGORY", null);

            if (tables.next()) {
                System.out.println("Table CATEGORY already exists.");

            }
            else {
                stmt.execute("CREATE TABLE CATEGORY ("
                        + "	id int AUTO_INCREMENT PRIMARY KEY,\n"
                        + "	Category varchar(100) \n"
                        + " )");

                stmt.execute("INSERT INTO CATEGORY (category) VALUES " +
                        "('Arts & Music'),('Biographies'),('Business'),('Comics'), "+
                        "('Computers & Tech'),('Cooking'),('Education'),('Entertainment'),"+
                        "('Health & Fitness'),('History'),('Self-Help'),('Horror'), "+
                        "('Romance'),('Science Fiction & Fantasy'),('Science & Math'),('Travel'), "+
                        "('Religion'),('Sports & Outdoors'),('Hobbies and Crafts'),"+
                        "('Kids'),('Literature & Fiction'),('Medical'),('Parenting') ");

                System.out.println("Category Table created");
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage() + " --- setupDatabase");
        }
    }

    //    Setup ISSUED Table
    void setupIssuedTable() {
        try {
            stmt = conn.createStatement();
            DatabaseMetaData dbm = conn.getMetaData();
            ResultSet tables = dbm.getTables(null, null, "ISSUED", null);

            if (tables.next()) {
                System.out.println("Table ISSUED already exists.");
            } else {
                stmt.execute("CREATE TABLE ISSUED ("
                        + "	id int AUTO_INCREMENT PRIMARY KEY ,\n"
                        + "	bookID int ,\n"
                        + "	memberID int ,\n"
                        + "	issueDate DATETIME DEFAULT NOW()::timestamp(0),\n"
                        + "	returnDate DATETIME ,\n"
                        + "	returned int DEFAULT 0,\n"
                        + "	renewCount integer default 0,\n"
                        + "	FOREIGN KEY (bookID) REFERENCES BOOK(id),\n"
                        + "	FOREIGN KEY (memberID) REFERENCES MEMBER(id)"
                        + " )");
                System.out.println("ISSUED Table created");
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage() + " --- setupDatabase");
        }
    }

    //    Setup LOGIN Table
    void setupLoginTable() {
        try {
            stmt = conn.createStatement();
            DatabaseMetaData dbm = conn.getMetaData();
            ResultSet tables = dbm.getTables(null, null, "LOGIN", null);

            if (tables.next()) {
                System.out.println("Table LOGIN already exists.");
            } else {
                stmt.execute("CREATE TABLE LOGIN ("
                        + "	id int AUTO_INCREMENT PRIMARY KEY ,\n"
                        + "	name varchar(100) ,\n"
                        + "	user varchar(60) ,\n"
                        + "	pword varchar(60) \n"
                        + " )");



                stmt.execute("INSERT INTO LOGIN (user,pword) VALUES " +
                        "('admin','password')");
                System.out.println("Login Table created");
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage() + " --- setupDatabase");
        }
    }



    public boolean updateMember(String fName,String lName,String address,String phone,String email,int id) {
        try {
            String update = "UPDATE MEMBER SET fName=?, lName=?, address=?, phone=?, email=? WHERE id=?";
            PreparedStatement stmt = conn.prepareStatement(update);
            stmt.setString(1, fName);
            stmt.setString(2, lName);
            stmt.setString(3, address);
            stmt.setString(4, phone);
            stmt.setString(5, email);
            stmt.setInt(6, id);
            int res = stmt.executeUpdate();
            return (res>0);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    public boolean updateBook(String title,String author,String publisher,String edition,int copies,int id) {
        try {
            String update = "UPDATE BOOK SET title=?, author=?, publisher=?, edition=?, copies=? WHERE id=?";
            PreparedStatement stmt = conn.prepareStatement(update);
            stmt.setString(1, title );
            stmt.setString(2, author );
            stmt.setString(3, publisher );
            stmt.setString(4, edition);
            stmt.setInt(5, copies);
            stmt.setInt(6, id);
            int res = stmt.executeUpdate();
            return (res>0);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    //    execute query function
    public ResultSet execQuery(String query) {
        ResultSet result;
        try {
            stmt = conn.createStatement();
            result = stmt.executeQuery(query);
        } catch (SQLException ex) {
            System.out.println("Exception at execQuery:dataHandler" + ex.getLocalizedMessage());
            return null;
        }
        return result;
    }

    public boolean execAction(String qu) {
        try {
            stmt = conn.createStatement();
            stmt.execute(qu);
            return true;
        } catch (SQLException ex) {
//            JOptionPane.showMessageDialog(null, "Error:" + ex.getMessage(), "Error Occured", JOptionPane.ERROR_MESSAGE);
            System.out.println("Exception at execQuery:dataHandler" + ex.getLocalizedMessage());
            return false;
        }
    }
}