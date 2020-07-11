package com.joinbe.service.dto;

import com.joinbe.domain.City;
import com.joinbe.domain.Shop;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;
import java.util.Locale;

@Data
public class DivisionWithVehicesleDTO extends DivisionDTO {

    @ApiModelProperty(value = "车辆")
    private List<VehicleSummaryDTO> vehicles;

    public DivisionWithVehicesleDTO(Shop shop, Locale locale) {
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

    public DivisionWithVehicesleDTO(City shop, Locale locale) {
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
