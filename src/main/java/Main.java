import config.DBConfig;
import dao.BikeDao;
import daoImpl.BikeDaoImpl;

import java.io.IOException;
import java.sql.SQLException;

public class Main {

    public static void main(String[] args) {
        BikeDao bikeDao =new BikeDaoImpl();
        try {
            DBConfig.initData();
            DBConfig.createTables();
            bikeDao.countOrders();
            bikeDao.estimateUserAddress();
            bikeDao.banAndPreserveBikes();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
}
