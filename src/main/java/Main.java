import config.DBConfig;
import dao.ArrangementDao;
import dao.BikeDao;
import daoImpl.ArrangementDaoImpl;
import daoImpl.BikeDaoImpl;

import java.io.*;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Main {

    public static void main(String[] args) throws IOException {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        BikeDao bikeDao = new BikeDaoImpl();
        ArrangementDao arrangementDao = new ArrangementDaoImpl();

        System.out.println(df.format(new Date()));
        arrangementDao.createTables();

        System.out.println(df.format(new Date()));
        arrangementDao.initData();

        System.out.println(df.format(new Date()));
        arrangementDao.question4();

        System.out.println(df.format(new Date()));
        arrangementDao.question5();

        System.out.println(df.format(new Date()));
        arrangementDao.question6();

        System.out.println(df.format(new Date()));

        System.out.println("---------------------------");

        System.out.println(df.format(new Date()));
        bikeDao.createTables();

        System.out.println(df.format(new Date()));
        bikeDao.initData();

        System.out.println(df.format(new Date()));
        bikeDao.countOrders();

        System.out.println(df.format(new Date()));
        bikeDao.estimateUserAddress();

        System.out.println(df.format(new Date()));
        bikeDao.banAndPreserveBikes();

        System.out.println(df.format(new Date()));
        DBConfig.closeConn();



    }
}
