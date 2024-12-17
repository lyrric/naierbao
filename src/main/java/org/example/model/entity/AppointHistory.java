package org.example.model.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@TableName("appoint_history")
public class AppointHistory {

    @TableId
    private String id;

    private String phone;

    private String shopId;

    private String shopName;

    private String appointmentDate;

    private String ticketId;

    private String ticketName;
    /**
     * 状态 1：待使用， 2:已取消，3:已使用
     */
    private Integer status;
    /**
     *
     */
    private Integer lineType;

    private Boolean isPunished;

    private Integer isDeleted;

    private Date createTime;
    /**
     * 1 默认，2 旧数据，3 新数据
     */
    private Integer type;

}
