package daoImpl;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.PreparedStatement;
import com.mysql.jdbc.Statement;
import config.DBConfig;
import dao.BikeDao;

import java.io.*;
import java.sql.SQLException;
import java.sql.Timestamp;

public class BikeDaoImpl implements BikeDao {
    @Override
    public void estimateUserAddress() {
        Statement stmt;
        try {
            stmt= (Statement) DBConfig.getConn().createStatement();
            stmt.execute("CREATE TEMPORARY TABLE ua SELECT user_id,stop_address,COUNT(user_id) AS num FROM `orders` WHERE DATE_FORMAT(end_time,'%H')>=18 AND DATE_FORMAT(end_time,'%H')<=24 GROUP BY user_id,stop_address;");
            stmt.execute("CREATE TEMPORARY TABLE ub SELECT user_id,MAX(num) as max_num FROM ua GROUP BY user_id;");
            stmt.execute("CREATE TEMPORARY TABLE uc select ua.user_id,ua.stop_address FROM ua,ub WHERE ua.user_id = ub.user_id AND ua.num = ub.max_num; ");
            stmt.execute("UPDATE users,uc SET user_address = stop_address WHERE users.user_id = uc.user_id;");
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void countOrders() {
        Statement stmt;
        try {
            stmt= (Statement) DBConfig.getConn().createStatement();
            stmt.execute("UPDATE orders SET cost = 1 WHERE UNIX_TIMESTAMP(end_time) - UNIX_TIMESTAMP(start_time)<= 1800 ;");
            stmt.execute("UPDATE orders SET cost = 2 WHERE UNIX_TIMESTAMP(end_time) - UNIX_TIMESTAMP(start_time)> 1800 AND UNIX_TIMESTAMP(end_time) - UNIX_TIMESTAMP(start_time)<= 3600;");
            stmt.execute("UPDATE orders SET cost = 3 WHERE UNIX_TIMESTAMP(end_time) - UNIX_TIMESTAMP(start_time)> 3600 AND UNIX_TIMESTAMP(end_time) - UNIX_TIMESTAMP(start_time)<= 5400;");
            stmt.execute("UPDATE orders SET cost = 4 WHERE UNIX_TIMESTAMP(end_time) - UNIX_TIMESTAMP(start_time)> 5400 ;");
            stmt.execute("UPDATE users,orders SET users.money = users.money - orders.cost WHERE users.user_id = orders.user_id;");
            stmt.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }


    }

    @Override
    public void banAndPreserveBikes() {
        Statement stmt;
        try {
            stmt= (Statement) DBConfig.getConn().createStatement();
            stmt.execute("UPDATE bikes SET time_last_month = 0;");
            stmt.execute("UPDATE bikes,orders SET bikes.time_last_month = bikes.time_last_month + UNIX_TIMESTAMP(end_time) - UNIX_TIMESTAMP(start_time) WHERE bikes.bike_id = orders.bike_id AND DATE_FORMAT(NOW(),'%m')-DATE_FORMAT(end_time,'%m')=1;");
            stmt.execute("UPDATE bikes SET is_work = FALSE WHERE time_last_month > 200 * 60 * 60;");
            stmt.execute("UPDATE bikes SET last_address = (SELECT stop_address FROM `orders` WHERE bike_id = bikes.bike_id ORDER BY end_time LIMIT 1);");
            stmt.close();


        } catch (SQLException e) {
            e.printStackTrace();
        }

    }


    @Override
    public void createTables() {
        try {
            Connection connection = DBConfig.getConn();
            Statement stmt = (Statement) connection.createStatement();

            String sql = "CREATE TABLE IF NOT EXISTS  `users`(\n" +
                    "   `user_id` int(11), \n" +
                    "   `user_name` VARCHAR(20) NOT NULL,\n" +
                    "   `user_phone` VARCHAR(20) NOT NULL,\n" +
                    "   `user_address` VARCHAR(20) NOT NULL,\n" +
                    "   `money` double NOT NULL,\n" +
                    "   PRIMARY KEY ( `user_id` )\n" +
                    ")ENGINE=InnoDB DEFAULT CHARSET=utf8;" ;


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
                    "   `is_work` boolean, \n" +
                    "   `time_last_month` int(11),\n" +
                    "   `last_address` VARCHAR(20),\n" +
                    "   PRIMARY KEY ( `bike_id` )\n" +
                    ")ENGINE=InnoDB DEFAULT CHARSET=utf8;";
            stmt.execute(sql);
            stmt.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void initData(){

        try {
            PreparedStatement pstmt = null;

            File file = new File("src/main/java/data/user.txt");
            String sql = "INSERT INTO `users` (`user_id`,`user_name`,`user_phone`,`money`,`user_address`) VALUES (?,?,?,?,?)";

                pstmt = (PreparedStatement) DBConfig.getConn().prepareStatement(sql);

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
            pstmt = (PreparedStatement) DBConfig.getConn().prepareStatement(sql);
            bufferedReader = new BufferedReader(new FileReader(file));
            while (bufferedReader.readLine()!=null){

                String array[] = bufferedReader.readLine().replaceAll(""+(char)65279,"").split(";");
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
            pstmt = (PreparedStatement) DBConfig.getConn().prepareStatement(sql);
            bufferedReader = new BufferedReader(new FileReader(file));
            while (bufferedReader.readLine()!=null){

                String array[] = bufferedReader.readLine().split(";");
                pstmt.setInt(1,Integer.parseInt(array[0]));
                pstmt.setBoolean(2,true);
                pstmt.execute();
            }


            pstmt.close();

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }





}
