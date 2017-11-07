package config;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.PreparedStatement;
import com.mysql.jdbc.Statement;

import java.io.*;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Timestamp;

public class DBConfig {

    private static final String url ="jdbc:mysql://localhost:3306/dh2?useUnicode=true&characterEncoding=UTF-8";
    private static final String userName ="root";
    private static final String password ="zhujing";

    private static Connection connection = null;

    private DBConfig(){

    }

    public static Connection getConn(){

       if (connection == null) {
           try {
               connection = (Connection) DriverManager.getConnection(url, userName, password);
           } catch (SQLException e) {
               e.printStackTrace();
           }
       }
       return connection;
    }

    public static void closeConn(){
        if (connection != null)
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }

    }


}
