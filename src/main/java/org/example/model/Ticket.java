package org.example.model;


import lombok.Data;

@Data
public class Ticket {
    /**
     * 环球店
     */
    public static final int SHOP_HUANQIU = 10;

    private String id;
    /**
     * 店铺id
     */
    private Integer shopId;
    /**
     * 店铺名称
     */
    private String shopName;
    /**
     * 库存数量
     */
    private Integer stockNum;
    /**
     * 预约数量
     */
    private Integer appointmentNum;
    /**
     * 预约时间
     */
    private String appointmentDate;




}
