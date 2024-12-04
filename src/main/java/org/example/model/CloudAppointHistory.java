package org.example.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CloudAppointHistory {

    private String id;

    private String phone;

    private Integer shopId;

    private String shopName;

    private String appointmentDate;

    private String ticketId;

    private String ticketName;
    /**
     * 状态 1：待使用， 2已取消，3已使用
     */
    private Integer status;
    /**
     *
     */
    private Integer lineType;

    private Boolean isPunished;

    private Integer isDeleted;
}
