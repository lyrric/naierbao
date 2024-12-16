package org.example.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Config {
    private Integer id;

    /**
     * 门店id
     */
    public String shopId;
    /**
     * 区域名称
     */
    private String areaName;
    /**
     * 门店名称
     */
    private String shopName;
    /**
     * 闲鱼昵称
     */
    private String xianyuNickname;
    /**
     * 手机号码
     */
    private String phone;
    /**
     * 备注
     */
    private String remark;
    /**
     * 推送的列表
     */
    private List<String> spts = new ArrayList<>();
    /**
     * 每天最多预约数量
     */
    private Integer maxCountPerDay = 2;
    /**
     * 预约规则
     */
    private Integer type;
    /**
     * 预约规则详情
     */
    private String data;
    /**
     * 是否已完成
     */
    private Boolean success;
    /**
     * 删除标志
     */
    private Boolean deleted;

}
