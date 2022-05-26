package com.example.demo.src.store.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetStoreMainRes {
    private Long storeId;
    private String reviewImg;
    private int wishCheck;
    private String distance;
    private String storeName;
    private float rating;
    private int viewCount;
    private int reviewCount;
}
