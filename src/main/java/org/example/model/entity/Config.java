package org.example.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Config {

    @TableId(type = IdType.AUTO)
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
    private String spts;
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

    public List<String> getSptsList() {
        if (spts == null) {
            return new ArrayList<>();
        }
        return Arrays.stream(spts.split(",")).collect(Collectors.toList());
    }
}
