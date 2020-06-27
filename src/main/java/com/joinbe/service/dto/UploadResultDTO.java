package com.joinbe.service.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class UploadResultDTO implements Serializable {
    private Long rowNum;
    private String msg;
    private Boolean isSuccess;
}
