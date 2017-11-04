package daoImpl;

import com.mysql.jdbc.Statement;
import config.DBConfig;
import dao.BikeDao;

import java.sql.SQLException;

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
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

}
