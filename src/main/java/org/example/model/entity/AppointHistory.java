package org.example.model.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

/**
 * 预约记录
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@TableName("appoint_history")
public class AppointHistory {

    @TableId
    private String id;
    /**
     * 电话号码
     */
    private String phone;

    /**
     * 门店id
     */
    private String shopId;

    /**
     * 门店名称
     */
    private String shopName;

    /**
     * 预约日期
     */
    private String appointmentDate;

    /**
     * 门票id
     */
    private String ticketId;

    /**
     * 门票名称
     */
    private String ticketName;
    /**
     * 状态 1：待使用， 2:已取消，3:已使用
     */
    private Integer status;

    private Integer lineType;

    private Boolean isPunished;

    private Integer isDeleted;

    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 1 默认，2 旧数据，3 新数据
     */
    private Integer type;

}
