package com.joinbe.web.rest.vm;

public class ResultUtil<T> {

    private static final Integer SUCCSS_CODE = 20000;
    private Result<T> result;

    public ResultUtil() {
        result = new Result<>();
        result.setMessage("success");
        result.setCode(SUCCSS_CODE);
    }

    public Result<T> setData(T t) {
        this.result.setData(t);
        this.result.setCode(SUCCSS_CODE);
        return this.result;
    }

    public Result<T> setSuccessMsg(String msg) {
        this.result.setMessage(msg);
        this.result.setCode(SUCCSS_CODE);
        this.result.setData(null);
        return this.result;
    }

    public Result<T> setData(T t, String msg) {
        this.result.setCode(SUCCSS_CODE);
        this.result.setMessage(msg);
        return this.result;
    }

    public Result<T> setErrorMsg(Integer code, String msg) {
        this.result.setMessage(msg);
        this.result.setCode(code);
        return this.result;
    }

    public static <T> Result<T> data(T t) {
        return new ResultUtil<T>().setData(t);
    }

    public static <T> Result<T> data(T t, String msg) {
        return new ResultUtil<T>().setData(t, msg);
    }

    public static <T> Result<T> success(String msg) {
        return new ResultUtil<T>().setSuccessMsg(msg);
    }


    public static <T> Result<T> error(Integer code, String msg) {
        return new ResultUtil<T>().setErrorMsg(code, msg);
    }
}
