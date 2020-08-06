package com.joinbe.data.collector.service.dto;

import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

public class DeviceMileageMultipleReq {

    @NotEmpty(message = "device IMEI number cant not be empty")
    @Length(min = 1, max = 100, message = "device IMEI number length should between 1~100")
    @ApiModelProperty(value = "设备IMEI", required=true)
    private String imei;

    @NotNull(message = "Mileage multiple cant not be Null")
    @ApiModelProperty(value = "里程数倍数",required = true)
    @DecimalMin(value="0.0", message = "Initial mileage cant not be less than 0.0")
    private BigDecimal multipleMileage;

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public BigDecimal getMultipleMileage() {
        return multipleMileage;
    }

    public void setMultipleMileage(BigDecimal multipleMileage) {
        this.multipleMileage = multipleMileage;
    }

    @Override
    public String toString() {
        return "DeviceMileageMultipleReq{" +
            "imei='" + imei + '\'' +
            ", multipleMileage=" + multipleMileage +
            '}';
    }
}
