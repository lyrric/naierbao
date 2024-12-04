package org.example.model;

import lombok.Data;

import java.util.List;

@Data
public class BaseResult<T> {

    private Boolean success;

    private Integer code;

    private String msg;

    private T data;


}
