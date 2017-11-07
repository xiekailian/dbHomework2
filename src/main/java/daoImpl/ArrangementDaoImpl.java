package daoImpl;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.Statement;
import config.DBConfig;
import dao.ArrangementDao;

import java.sql.SQLException;

public class ArrangementDaoImpl implements ArrangementDao {
    @Override
    public void createTables() {
        try {
            Connection connection = DBConfig.getConn();
            Statement stmt = (Statement) connection.createStatement();

            String sql ="CREATE TABLE IF NOT EXISTS  `students`(\n" +
                    "   `college_name` VARCHAR(40) NOT NULL,\n" +
                    "   `student_num` VARCHAR(20) NOT NULL,\n" +
                    "   `student_name` VARCHAR(20) NOT NULL,\n" +
                    "   `sex` VARCHAR(10) NOT NULL,\n" +
                    "   `dormitory_num` VARCHAR(20) NOT NULL,\n" +
                    "   PRIMARY KEY ( `student_num` )\n" +
                    ")ENGINE=InnoDB DEFAULT CHARSET=utf8;\n" ;


            stmt.execute(sql);

            sql ="CREATE TABLE IF NOT EXISTS `dormitories`(\n" +
                    "   `dormitory_num` VARCHAR(20) NOT NULL,\n" +
                    "   `dormitory_phone` VARCHAR(20) NOT NULL, \n" +
                    "   `campus_name` VARCHAR(20) NOT NULL, \n" +
                    "   `dormitory_payment` int(11),\n" +
                    "   PRIMARY KEY ( `dormitory_num` )\n" +
                    ")ENGINE=InnoDB DEFAULT CHARSET=utf8;\n" ;

            stmt.execute(sql);
            stmt.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initData() {
        try {
            Connection connection = DBConfig.getConn();
            Statement stmt = (Statement) connection.createStatement();

            String sql ="load data local infile '/Users/zhujin/Desktop/sharingBike/src/main/java/data/phone.txt' into table dormitories character set utf8 fields terminated by';';" ;

            stmt.execute(sql);

            sql ="load data local infile '/Users/zhujin/Desktop/sharingBike/src/main/java/data/arrange_plan.txt' into table students character set utf8 fields terminated by',';" ;

            stmt.execute(sql);
            stmt.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void question4() {
        try {
            Connection connection = DBConfig.getConn();
            Statement stmt = (Statement) connection.createStatement();

            String sql ="SELECT DISTINCT(college_name) FROM students WHERE dormitory_num IN (SELECT dormitory_num FROM students WHERE student_name = '王小星');";

            stmt.execute(sql);
            stmt.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void question5() {
        try {
            Connection connection = DBConfig.getConn();
            Statement stmt = (Statement) connection.createStatement();

            String sql ="UPDATE dormitories SET dormitory_payment = 1200 WHERE dormitory_num = '陶园1舍';\n";

            stmt.execute(sql);
            stmt.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void question6() {
        try {
            Connection connection = DBConfig.getConn();
            Statement stmt = (Statement) connection.createStatement();

            String sql ="UPDATE students SET dormitory_num = '陶园1舍' WHERE college_name = '软件学院' AND sex = '男';\n";

            stmt.execute(sql);
            sql ="UPDATE students SET dormitory_num = '南园1舍' WHERE college_name = '软件学院' AND sex = '女';\n";

            stmt.execute(sql);
            stmt.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
}
