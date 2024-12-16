package org.example.model;

import lombok.Data;

import java.util.List;

@Data
public class Area {
    private String id;
    private String parentId;
    private String code;
    private Integer dictKey;
    private String dictValue;
    private Integer sort;
    private String remark;
    private Integer isDeleted;
    private Boolean isWeekDay;
    private String endDate;
    private String excludeDates;
    private String includeDates;
    private List<Area> children;


}
