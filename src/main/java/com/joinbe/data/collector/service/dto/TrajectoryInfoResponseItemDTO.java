package com.joinbe.data.collector.service.dto;

import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;

public class TrajectoryInfoResponseItemDTO {

    @ApiModelProperty(value = "总公里数(KM)")
    private BigDecimal totalMileageInKM;

    @ApiModelProperty(value = "总油耗(L)")
    private BigDecimal totalFuelConsumptionInLiter;

    @ApiModelProperty(value = "车辆的油耗(KM/L)")
    private BigDecimal vehicleFuelConsumptionInKMPerLiter;

    @ApiModelProperty(value = "设备的imei")
    private String imei;

    @ApiModelProperty(value = "车牌号")
    private String plateNumber;

    public BigDecimal getTotalMileageInKM() {
        return totalMileageInKM;
    }

    public void setTotalMileageInKM(BigDecimal totalMileageInKM) {
        this.totalMileageInKM = totalMileageInKM;
    }

    public BigDecimal getTotalFuelConsumptionInLiter() {
        return totalFuelConsumptionInLiter;
    }

    public void setTotalFuelConsumptionInLiter(BigDecimal totalFuelConsumptionInLiter) {
        this.totalFuelConsumptionInLiter = totalFuelConsumptionInLiter;
    }

    public BigDecimal getVehicleFuelConsumptionInKMPerLiter() {
        return vehicleFuelConsumptionInKMPerLiter;
    }

    public void setVehicleFuelConsumptionInKMPerLiter(BigDecimal vehicleFuelConsumptionInKMPerLiter) {
        this.vehicleFuelConsumptionInKMPerLiter = vehicleFuelConsumptionInKMPerLiter;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public String getPlateNumber() {
        return plateNumber;
    }

    public void setPlateNumber(String plateNumber) {
        this.plateNumber = plateNumber;
    }
}
