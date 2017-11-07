package dao;

public interface BikeDao {

    /**
     *
     */
    void createTables();

    /**
     *
     */
    void initData();

    /**
     * 根据晚上6点到12点之间，单车归还的最频繁的地点，估计用户地址，完善用户表
     */
    void estimateUserAddress();


    /**
     * 计算订单费用，自动补全费用字段，并在用户账户中，扣除相应的金额
     */
    void countOrders();

    /**
     * 每个月初，禁用上一个月内使用超200小时的单车，计算小车最后停车点
     */
    void banAndPreserveBikes();




}
