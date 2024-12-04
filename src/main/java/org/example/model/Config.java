package org.example.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Config {
    /**
     * 推送的列表
     */
    private List<String> spts = new ArrayList<>();
    /**
     * 每天最多预约数量
     */
    private Long maxCountPerDay = 2L;

}
