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


    public static void createTables() throws SQLException {

        Connection connection = getConn();
        String sql = "CREATE TABLE IF NOT EXISTS  `users`(\n" +
                "   `user_id` int(11), \n" +
                "   `user_name` VARCHAR(20) NOT NULL,\n" +
                "   `user_phone` VARCHAR(20) NOT NULL,\n" +
                "   `user_address` VARCHAR(20) NOT NULL,\n" +
                "   `money` double NOT NULL,\n" +
                "   PRIMARY KEY ( `user_id` )\n" +
                ")ENGINE=InnoDB DEFAULT CHARSET=utf8;" ;
        Statement stmt = (Statement) connection.createStatement();
        stmt.execute(sql);

        sql = "CREATE TABLE IF NOT EXISTS  `orders`(\n" +
                "   `user_id` int(11) NOT NULL, \n" +
                "   `bike_id` int(11) NOT NULL, \n" +
                "   `start_address` VARCHAR(20) NOT NULL,\n" +
                "   `start_time` datetime NOT NULL,\n" +
                "   `stop_address` VARCHAR(20) NOT NULL,\n" +
                "   `end_time` datetime NOT NULL,\n" +
                "   `cost` double NOT NULL\n" +
                ")ENGINE=InnoDB DEFAULT CHARSET=utf8;";
        stmt.execute(sql);

        sql = "CREATE TABLE IF NOT EXISTS  `bikes`(\n" +
                "   `bike_id` int(11),\n" +
                "   `is_work` boolean NOT NULL, \n" +
                "   `time_last_month` int(11),\n" +
                "   `last_address` VARCHAR(20)\n" +
                "   PRIMARY KEY ( `bike_id` )\n" +
                ")ENGINE=InnoDB DEFAULT CHARSET=utf8;";
        stmt.execute(sql);

    }

    public static void initData() throws IOException, SQLException {
        PreparedStatement pstmt = null;

        File file = new File("src/main/java/data/user.txt");
        String sql = "INSERT INTO `users` (`user_id`,`user_name`,`user_phone`,`money`,`user_address`) VALUES (?,?,?,?,?)";
        pstmt = (PreparedStatement) getConn().prepareStatement(sql);

        BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
        while (bufferedReader.readLine()!=null){

            String array[] = bufferedReader.readLine().split(";");
            pstmt.setInt(1,Integer.parseInt(array[0]));
            pstmt.setString(2,array[1]);
            pstmt.setString(3,array[2]);
            pstmt.setDouble(4,Double.parseDouble(array[3]));
            pstmt.setString(5,"");
            pstmt.execute();

        }

        file = new File("src/main/java/data/record.txt");
        sql = "INSERT INTO `orders` (`user_id`,`bike_id`,`start_address`,`start_time`,`stop_address`,`end_time`,`cost`) VALUES (?,?,?,?,?,?,?)";
        pstmt = (PreparedStatement) getConn().prepareStatement(sql);
        bufferedReader = new BufferedReader(new FileReader(file));
        while (bufferedReader.readLine()!=null){

            String array[] = bufferedReader.readLine().split(";");
            pstmt.setInt(1,Integer.parseInt(array[0]));
            pstmt.setInt(2,Integer.parseInt(array[1]));
            pstmt.setString(3,array[2]);
            pstmt.setTimestamp(4, Timestamp.valueOf(array[3].replace("-"," ").replace("/","-")));
            pstmt.setString(5,array[4]);
            pstmt.setTimestamp(6, Timestamp.valueOf(array[5].replace("-"," ").replace("/","-")));
            pstmt.setDouble(7,0);
            pstmt.execute();
        }

        file = new File("src/main/java/data/bike.txt");
        sql = "INSERT INTO `bikes` (`bike_id`,`is_work`) VALUES (?,?)";
        pstmt = (PreparedStatement) getConn().prepareStatement(sql);
        bufferedReader = new BufferedReader(new FileReader(file));
        while (bufferedReader.readLine()!=null){

            String array[] = bufferedReader.readLine().split(";");
            pstmt.setInt(1,Integer.parseInt(array[0]));
            pstmt.setBoolean(2,true);
            pstmt.execute();
        }



    }





}
