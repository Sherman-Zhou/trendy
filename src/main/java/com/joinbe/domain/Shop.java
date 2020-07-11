package com.joinbe.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.joinbe.domain.enumeration.RecordStatus;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * A Shop.
 */
@Entity
@Table(name = "shop")
public class Shop implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    //    @NotNull
    @Size(max = 200)
    @Column(name = "title")
    private String title;

    @Size(max = 200)
    @Column(name = "title_cn")
    private String titleCn;

    @Size(max = 200)
    @Column(name = "title_jp")
    private String titleJp;

    @Size(max = 200)
    @Column(name = "address")
    private String address;

    @Size(max = 200)
    @Column(name = "address_cn")
    private String addressCn;

    @Size(max = 200)
    @Column(name = "address_jp")
    private String addressJp;

    @Column(name = "lng")
    private String lng;

    @Column(name = "lat")
    private String lat;

    @Column(name = "status")
    private RecordStatus status;


    @Column(name = "parent_id", updatable = false, insertable = false)
    private Long parentId;
    @ManyToOne
    @JsonIgnoreProperties("divisions")
    @TableField(exist = false)
    private Shop parent;
    @OneToMany(mappedBy = "parent")
    @TableField(exist = false)
    private List<Shop> children = new ArrayList<>();

    public Shop() {
    }

    public Shop(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitleCn() {
        return titleCn;
    }

    public void setTitleCn(String titleCn) {
        this.titleCn = titleCn;
    }

    public String getTitleJp() {
        return titleJp;
    }

    public void setTitleJp(String titleJp) {
        this.titleJp = titleJp;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAddressCn() {
        return addressCn;
    }

    public void setAddressCn(String addressCn) {
        this.addressCn = addressCn;
    }

    public String getAddressJp() {
        return addressJp;
    }

    public void setAddressJp(String addressJp) {
        this.addressJp = addressJp;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }


    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public RecordStatus getStatus() {
        return status;
    }

    public void setStatus(RecordStatus status) {
        this.status = status;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public Shop getParent() {
        return parent;
    }

    public void setParent(Shop parent) {
        this.parent = parent;
    }

    public List<Shop> getChildren() {
        return children;
    }

    public void setChildren(List<Shop> children) {
        this.children = children;
    }

}
