package com.example.demo.src.store.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetStoreListRes {
    private String reviewImg;
    private String distance;
    private String storeName;
    private String foodCategory;
    private float rating;
    private String reviewCount;
}
