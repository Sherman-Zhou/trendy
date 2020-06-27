package com.joinbe.common.excel;

import lombok.Data;

import java.io.Serializable;

@Data
public class BindingData implements Serializable {

    private String identifyNumber;

    private String licensePlateNumber;

    private Integer rowIdx;
}
