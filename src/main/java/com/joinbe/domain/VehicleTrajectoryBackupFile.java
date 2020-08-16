package com.joinbe.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.Instant;

@Entity
@Table(name = "vehicle_trajectory_backup_file")
public class VehicleTrajectoryBackupFile {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @NotNull
    @Size(max = 255)
    @Column(name = "rpt_name", length = 255, nullable = false)
    private String rptName;

    @Size(max = 10)
    @Column(name = "from_date", length = 10)
    private String fromDate;

    @Size(max = 10)
    @Column(name = "to_date", length = 10)
    private String toDate;

    @Size(max = 255)
    @Column(name = "err_msg", length = 255)
    private String errMsg;

    @Column(name = "is_success")
    private Boolean isSuccess;

    @Column(name = "is_completed")
    private Boolean isCompleted;

    @Column(name = "merchant_id")
    private Long merchantId;

    @Column(name = "generated_by", length = 50)
    private String generatedBy;

    @Column(name = "generated_on")
    private Instant generatedOn = Instant.now();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRptName() {
        return rptName;
    }

    public void setRptName(String rptName) {
        this.rptName = rptName;
    }

    public String getErrMsg() {
        return errMsg;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }

    public Boolean getSuccess() {
        return isSuccess;
    }

    public void setSuccess(Boolean success) {
        isSuccess = success;
    }

    public Boolean getCompleted() {
        return isCompleted;
    }

    public void setCompleted(Boolean completed) {
        isCompleted = completed;
    }

    public String getGeneratedBy() {
        return generatedBy;
    }

    public void setGeneratedBy(String generatedBy) {
        this.generatedBy = generatedBy;
    }

    public Instant getGeneratedOn() {
        return generatedOn;
    }

    public void setGeneratedOn(Instant generatedOn) {
        this.generatedOn = generatedOn;
    }

    public String getFromDate() {
        return fromDate;
    }

    public void setFromDate(String fromDate) {
        this.fromDate = fromDate;
    }

    public String getToDate() {
        return toDate;
    }

    public void setToDate(String toDate) {
        this.toDate = toDate;
    }

    public Long getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(Long merchantId) {
        this.merchantId = merchantId;
    }
}
