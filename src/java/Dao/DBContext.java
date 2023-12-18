package Dao;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBContext {
    public static Connection con;

    public DBContext() {
        if (con == null) {
            // MySQL connection details
            String dbUrl = "jdbc:mysql://localhost:3306/IOT_FINAL";
            String dbUser = "root";
            String dbPassword = "Tnq27062002@";
            String dbClass = "com.mysql.cj.jdbc.Driver";

            try {
                Class.forName(dbClass);
                con = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}


//import java.sql.Connection;
//import java.sql.DriverManager;
//
//
//public class DBContext {
//    
//    /*USE BELOW METHOD FOR YOUR DATABASE CONNECTION FOR BOTH SINGLE AND MULTILPE SQL SERVER INSTANCE(s)*/
//    /*DO NOT EDIT THE BELOW METHOD, YOU MUST USE ONLY THIS ONE FOR YOUR DATABASE CONNECTION*/
//     public Connection getConnection()throws Exception {
//        String url = "jdbc:sqlserver://"+serverName+":"+portNumber + "\\" + instance +";databaseName="+dbName;
//        if(instance == null || instance.trim().isEmpty())
//            url = "jdbc:sqlserver://"+serverName+":"+portNumber +";databaseName="+dbName;
//        Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
//        return DriverManager.getConnection(url, userID, password);
//    }   
////      public String getImagePath() throws Exception {
////        return "image/";
////    }
//    /*Insert your other code right after this comment*/
//    /*Change/update information of your database connection, DO NOT change name of instance variables in this class*/
//    private final String serverName = "//MSI\\\\SERVER_SQL2";
//    private final String dbName = "Account";
//    private final String portNumber = "1433";
//    private final String instance="";//LEAVE THIS ONE EMPTY IF YOUR SQL IS A SINGLE INSTANCE
//    private final String userID = "sa";
//    private final String password = "11111";
//}