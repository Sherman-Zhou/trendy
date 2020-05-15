package com.joinbe.data.collector.exception;

public class NettyException extends RuntimeException {

    private Integer errorCode;
    private String errorMessage;

    public NettyException(ErrorEnum errorEnum) {
        this.errorCode = errorEnum.getErrorCode();
        this.errorMessage = errorEnum.getErrorMessage();
    }

    public NettyException(ErrorEnum errorEnum, String errorMessage) {
        this.errorCode = errorEnum.getErrorCode();
        this.errorMessage = errorMessage;
    }

    public NettyException(Integer errorCode, String errorMessage) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public Integer getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(Integer errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
