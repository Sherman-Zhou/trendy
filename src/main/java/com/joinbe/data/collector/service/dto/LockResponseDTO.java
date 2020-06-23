package com.joinbe.data.collector.service.dto;

import java.io.Serializable;

public class LockResponseDTO extends ResponseDTO implements Serializable {

    public LockResponseDTO(int code, String message) {
        super(code, message);
    }

}
