package com.joinbe.service.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class TrendyResponse implements Serializable {

    private int status;

    private String info;

    private DataList data;

    @Data
    public static class DataList implements Serializable {
        private long total;
        private List<Car> list;
    }

    @Data
    public static class Car implements Serializable {

        private String id;
        //车牌
        private String plate_number;
        //车架号
        //'品牌',
        private String brand_name;
        //'车型',
        private String brand_model;
        //'生产年份'
        private String year;
        //款式'
        private String brand_style;
        //'颜色'
        //负责人
        private String user_name;
        //联系电话
        //名称：
        private String title;
        //门店名称：
        private Shop shoplist;
        // 城市
        private City citylist;
    }

    @Data
    public static class Shop implements Serializable {
        private String id;
        private String title;
        private String tel;
        private String path;
        private String city;
        private String area;
        private String address;
    }

    @Data
    public static class City implements Serializable {
        private String id;
        private String name;
        private List<City> son;
    }
}
