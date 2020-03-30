package com.joinbe.domain.enumeration;

import org.springframework.http.HttpMethod;

public enum OperationType {
    VIEW("VIEW", HttpMethod.GET),
    ADD("ADD", HttpMethod.POST),
    EDIT("EDIT", HttpMethod.PUT),
    DELETE("DELETE", HttpMethod.DELETE),
    CLEAR("CLEAR", HttpMethod.PUT),
    RESET("RESET", HttpMethod.PUT),
    ASSIGN("ASSIGN", HttpMethod.PUT),
    ENABLE("ENABLE", HttpMethod.PUT),
    DISABLE("DISABLE", HttpMethod.PUT),
    SEARCH("SEARCH", HttpMethod.GET),
    UPLOAD("UPLOAD", HttpMethod.POST),
    DOWNLOAD("DOWNLOAD", HttpMethod.GET),
    SYNC("SYNC", HttpMethod.GET),
    OTHER("OTHER", HttpMethod.GET);

    private String code;

    private HttpMethod method;

    OperationType(String code, HttpMethod method) {
        this.code = code;
        this.method = method;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public HttpMethod getMethod() {
        return method;
    }

    public void setMethod(HttpMethod method) {
        this.method = method;
    }
}
