package com.joinbe.web.rest.vm;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class PageData<T> implements Serializable {

    private static final long serialVersionUID = 1L;


    private long total;


    private List<T> items;
}
