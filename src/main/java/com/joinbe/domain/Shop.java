package com.joinbe.domain;

import com.joinbe.domain.enumeration.RecordStatus;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * A Shop.
 */
@Entity
@Table(name = "shop")
public class Shop implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    // @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;

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

    @ManyToOne(fetch = FetchType.LAZY)
    private Merchant merchant;

//    @ManyToOne
//    @JoinColumn(name= "area_id")
//    private City area;

    @ManyToOne
    @JoinColumn(name = "city_id")
    private City city;

    @Column(name = "city_id", updatable = false, insertable = false)
    private String cityId;

//    @Column(name = "area_id", updatable = false, insertable = false)
//    private String areaId;

    public Shop() {
    }

    public Shop(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Merchant getMerchant() {
        return merchant;
    }

    public void setMerchant(Merchant merchant) {
        this.merchant = merchant;
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

    public String getCityId() {
        return cityId;
    }

    public void setCityId(String cityId) {
        this.cityId = cityId;
    }

//    public String getAreaId() {
//        return areaId;
//    }
//
//    public void setAreaId(String areaId) {
//        this.areaId = areaId;
//    }
//
//    public City getArea() {
//        return area;
//    }
//
//    public void setArea(City area) {
//        this.area = area;
//    }

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }
}
