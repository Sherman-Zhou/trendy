package com.joinbe.common.util;

import org.springframework.util.Assert;

public class RequestKey {
    private String url;
    private String method;

    public RequestKey(String url) {
        this(url, null);
    }

    public RequestKey(String url, String method) {
        Assert.notNull(url, "url cannot be null");
        this.url = url;
        this.method = method;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    @Override
    public int hashCode() {
        int result = this.url.hashCode();
        result = 31 * result + (this.method != null ? this.method.hashCode() : 0);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof RequestKey)) {
            return false;
        }

        RequestKey key = (RequestKey) obj;

        if (!url.equals(key.url)) {
            return false;
        }

        if (method == null) {
            return key.method == null;
        }

        return method.equals(key.method);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(url.length() + 7);
        sb.append("[");
        if (method != null) {
            sb.append(method).append(",");
        }
        sb.append(url);
        sb.append("]");

        return sb.toString();
    }
}
