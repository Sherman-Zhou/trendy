package com.joinbe.service.dto;

import com.joinbe.domain.City;
import com.joinbe.domain.Shop;
import com.joinbe.domain.enumeration.RecordStatus;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.List;
import java.util.Locale;

@Data
@EqualsAndHashCode(callSuper = false)
public class DivisionDTO implements Serializable {

    private String id;

    @NotNull
    @Size(max = 100)
    @ApiModelProperty(value = "部门名", required = true)
    private String name;

    @Size(max = 500)
    @ApiModelProperty(value = "部门描述" )
    private String description;

    @Size(max = 20)
    @ApiModelProperty(value = "部门代码")
    private String code;

    @ApiModelProperty(value = "状态")
    private RecordStatus status;

    @ApiModelProperty(value = "上级部门主键")
    private String parentId;

    @ApiModelProperty(value = "是否有子部门")
    private boolean hasChildren;

    @ApiModelProperty(value = "子部门")
    private List<DivisionDTO> children;

    public DivisionDTO() {

    }

    public DivisionDTO(Shop shop, Locale locale) {
        this.setId(shop.getId());
        if (Locale.CHINESE.equals(locale)) {

            this.setName(shop.getTitleCn());
            this.setDescription(shop.getTitleCn());
        } else if (Locale.JAPANESE.equals(locale)) {
            this.setName(shop.getTitleJp());
            this.setDescription(shop.getTitleJp());
        } else {
            this.setName(shop.getTitle());
            this.setDescription(shop.getTitle());
        }

        this.setParentId(null);
        this.setStatus(shop.getStatus());
    }

    public DivisionDTO(City shop, Locale locale) {
        this.setId(shop.getId());
        if (Locale.CHINESE.equals(locale)) {

            this.setName(shop.getNameCn());
            this.setDescription(shop.getNameCn());
        } else if (Locale.JAPANESE.equals(locale)) {
            this.setName(shop.getNameJp());
            this.setDescription(shop.getNameJp());
        } else {
            this.setName(shop.getName());
            this.setDescription(shop.getName());
        }

        this.setParentId(shop.getParentId());
        this.setStatus(shop.getStatus());
    }


}
