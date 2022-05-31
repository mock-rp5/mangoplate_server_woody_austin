package com.example.demo.src.store.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.text.DecimalFormat;

@Getter
@Setter
@AllArgsConstructor
public class GetStoreRes {
    private Long storeId;
    private String storeName;
    private Long viewCount;
    private Long reviewCount;
    private Long wishCount;
    private float rating;
    private int wishCheck;
    private int visitedCount;
    private String address;
    private double latitude;
    private double longitude;
    private String telephone;
    private String openTime;
    private String breakTime;
    private String dayOff;
    private String priceInfo;
    private String foodCategory;
    private String parkingInfo;
    private String website;
    private Long creatorId;
    private String lastUpdate;
}
