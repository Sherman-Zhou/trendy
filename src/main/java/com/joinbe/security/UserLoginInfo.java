package com.joinbe.security;

import com.joinbe.service.dto.RoleDTO;

import java.io.Serializable;
import java.util.List;

public class UserLoginInfo implements Serializable {

    private String login;

    private final Long id;

    private Long merchantId;

    private RoleDTO roleDTO;

    private List<String> divisionIds;

    public UserLoginInfo(Long id, String login, Long merchantId, List<String> divisionIds) {
        this.id = id;
        this.login = login;
        this.merchantId = merchantId;
        this.divisionIds = divisionIds;

    }

    public boolean isSystemAdmin() {
        return merchantId == null;
    }

    public Long getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(Long merchantId) {
        this.merchantId = merchantId;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public RoleDTO getRoleDTO() {
        return roleDTO;
    }

    public void setRoleDTO(RoleDTO roleDTO) {
        this.roleDTO = roleDTO;
    }

    public List<String> getDivisionIds() {
        return divisionIds;
    }

    public void setDivisionIds(List<String> divisionIds) {
        this.divisionIds = divisionIds;
    }

    @Override
    public String toString() {
        return "UserLoginInfo{" +
            "login='" + login + '\'' +
            ",id=" + id +
            ", merchantId=" + merchantId +
            ", divisionIds=" + divisionIds +
            '}';
    }
}
