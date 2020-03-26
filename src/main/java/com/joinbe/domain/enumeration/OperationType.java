package com.joinbe.domain.enumeration;

import org.springframework.http.HttpMethod;

public enum OperationType {
    VIEW("VIEW", HttpMethod.GET),
    ADD("ADD", HttpMethod.POST),
    EDIT("EDIT", HttpMethod.PUT),
    DELETE("DELETE", HttpMethod.DELETE),
    CLEAR("CLEAR", HttpMethod.PUT),
    ENABLE("ENABLE", HttpMethod.PUT),
    DISABLE("DISABLE", HttpMethod.PUT),
    SEARCH("SEARCH", HttpMethod.GET),
    UPLOAD("UPLOAD", HttpMethod.POST),
    DOWNLOAD("DOWNLOAD", HttpMethod.GET),
    OTHER("OTHER", HttpMethod.GET);

    private String code;

    private HttpMethod method;

    OperationType(String code, HttpMethod method){
        this.code = code;
        this.method = method;
    }

}
