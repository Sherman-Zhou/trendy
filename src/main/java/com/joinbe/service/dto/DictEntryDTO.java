package com.joinbe.service.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Size;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * A DTO for the {@link com.joinbe.domain.DictEntry} entity.
 */
@ApiModel(description = "字典数据表")
@Data
public class DictEntryDTO implements Serializable {

    private Long id;

    /**
     * 字典排序
     */
    @ApiModelProperty(value = "字典排序")
    private BigDecimal sortOrder;

    /**
     * 字典标签
     */
    @Size(max = 100)
    @ApiModelProperty(value = "字典标签")
    private String label;

    /**
     * 字典键值
     */
    @Size(max = 100)
    @ApiModelProperty(value = "字典键值")
    private String value;

    @ApiModelProperty(value = "是否默认选中")
    private Boolean isDefault;

    /**
     * 备注
     */
    @Size(max = 500)
    @ApiModelProperty(value = "备注")
    private String remark;

    @Size(max = 10)
    @ApiModelProperty(value = "语言")
    private String lang;

//    @NotNull
//    @Size(max = 1)
//    private String status;
//
//    @Size(max = 50)
//    private String lastModifiedBy;
//
//    private Instant lastModifiedDate;
//
//    @NotNull
//    @Size(max = 50)
//    private String createdBy;
//
//    private Instant createdDate;
//
//
//    private Long dictTypeId;


}
