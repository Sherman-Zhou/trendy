package com.joinbe.data.collector.service.dto;

import java.math.BigDecimal;

public class MileageResponseItemDTO {

    private String data;

    private String ok;

    private String type;

    private String mileageMode;

    private BigDecimal mileageOffset;

    private BigDecimal mileageMultiple;

    private String imei;

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getOk() {
        return ok;
    }

    public void setOk(String ok) {
        this.ok = ok;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMileageMode() {
        return mileageMode;
    }

    public void setMileageMode(String mileageMode) {
        this.mileageMode = mileageMode;
    }

    public BigDecimal getMileageOffset() {
        return mileageOffset;
    }

    public void setMileageOffset(BigDecimal mileageOffset) {
        this.mileageOffset = mileageOffset;
    }

    public BigDecimal getMileageMultiple() {
        return mileageMultiple;
    }

    public void setMileageMultiple(BigDecimal mileageMultiple) {
        this.mileageMultiple = mileageMultiple;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }
}
