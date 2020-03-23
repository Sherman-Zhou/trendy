package com.joinbe.web.rest.vm;

import lombok.Data;

import java.io.Serializable;

@Data
public class Result<T> implements Serializable {

    private static final long serialVersionUID = 1L;


    private String message;


    private Integer code;

    private T data;
}
