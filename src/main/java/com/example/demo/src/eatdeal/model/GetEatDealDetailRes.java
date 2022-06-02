package com.example.demo.src.eatdeal.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetEatDealDetailRes {
    private Long eatDealId;
    private String imgUrl;
    private String subRegion;
    private String storeName;
    private String menuName;
    private String startDate;
    private String endDate;
    private int discountPercent;
    private int originalPrice;
    private int finalPrice;
    private Long storeId;
    private double latitude;
    private double longitude;
    private String memo;
    private String storeDescription;
    private String menuDescription;
}
