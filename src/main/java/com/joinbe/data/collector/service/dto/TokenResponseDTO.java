package com.joinbe.data.collector.service.dto;

import java.io.Serializable;

public class TokenResponseDTO extends ResponseDTO implements Serializable {

    public TokenResponseDTO(int code, String message) {
        super(code, message);
    }

    public TokenResponseDTO(int code, String message, TokenResponseItem data) {
        super(code, message);
        this.data = data;
    }

    private TokenResponseItem data;

    public TokenResponseItem getData() {
        return data;
    }

    public void setData(TokenResponseItem data) {
        this.data = data;
    }
}
