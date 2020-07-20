package com.joinbe.data.collector.service.dto;

import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;

public class VehicleCalcInfoResponseItemDTO {

    @ApiModelProperty(value = "目前里程数(KM)")
    private BigDecimal currentMileageInKM;

    @ApiModelProperty(value = "剩余油量(L)")
    private BigDecimal remainFuelConsumptionInLiter;

    public BigDecimal getCurrentMileageInKM() {
        return currentMileageInKM;
    }

    public void setCurrentMileageInKM(BigDecimal currentMileageInKM) {
        this.currentMileageInKM = currentMileageInKM;
    }

    public BigDecimal getRemainFuelConsumptionInLiter() {
        return remainFuelConsumptionInLiter;
    }

    public void setRemainFuelConsumptionInLiter(BigDecimal remainFuelConsumptionInLiter) {
        this.remainFuelConsumptionInLiter = remainFuelConsumptionInLiter;
    }
}
