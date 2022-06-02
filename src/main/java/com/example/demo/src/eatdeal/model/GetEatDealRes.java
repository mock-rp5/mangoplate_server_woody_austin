package com.example.demo.src.eatdeal.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetEatDealRes {
    private Long eatDealId;
    private String subRegion;
    private String storeName;
    private String menuName;
    private String imgUrl;
    private int discountPercent;
    private int originalPrice;
    private int finalPrice;
    private String memo;
}
