package com.example.demo.src.user.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetVisitedStoreRes {
    private Long storeId;
    private String storeImg;
    private String storeName;
    private String subRegion;
    private String foodCategory;
    private int viewCount;
    private int reviewCount;
}
