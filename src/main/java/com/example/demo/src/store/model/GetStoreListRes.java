package com.example.demo.src.store.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetStoreListRes {
    private Long storeId;
    private String reviewImg;
    private int wishCheck;
    private int visitedCheck;
    private String subRegion;
    private float distance;
    private String storeName;
    private float rating;
    private int viewCount;
    private int reviewCount;
}
