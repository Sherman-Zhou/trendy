package com.joinbe.common.excel;

import lombok.Data;

@Data
public class EquipmentData {
    /**
     * 設備ID
     */
    private String identifyNumber;

    /**
     * 設備IMEI
     */
    private String imei;

    /**
     * 固件版本
     */
    private String version;

    /**
     * SIM卡號
     */
    private String simCardNum;


    /**
     * 備註
     */
    private String remark;

    /**
     * 組織
     */
    private String orgName;

    /**
     * 区域
     */
    private String divName;

    private Integer rowIdx;

}
