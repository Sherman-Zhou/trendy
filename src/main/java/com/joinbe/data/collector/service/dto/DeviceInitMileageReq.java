package com.joinbe.data.collector.service.dto;

import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.*;
import java.math.BigDecimal;

public class DeviceInitMileageReq {

    @NotEmpty(message = "device IMEI number cant not be empty")
    @Length(min = 1, max = 100, message = "device IMEI number length should between 1~100")
    @ApiModelProperty(value = "设备IMEI", required=true)
    private String imei;

    @NotNull(message = "Initial mileage cant not be Null")
    @DecimalMax(value="4294967.2", message = "Initial mileage cant not be greater than 4294967.2")
    @DecimalMin(value="0.0", message = "Initial mileage cant not be less than 0.0")
    @ApiModelProperty(value = "初始里程数",required = true)
    private BigDecimal initMileage;

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public BigDecimal getInitMileage() {
        return initMileage;
    }

    public void setInitMileage(BigDecimal initMileage) {
        this.initMileage = initMileage;
    }

    @Override
    public String toString() {
        return "DeviceInitMileageReq{" +
            "imei='" + imei + '\'' +
            ", initMileage=" + initMileage +
            '}';
    }
}
