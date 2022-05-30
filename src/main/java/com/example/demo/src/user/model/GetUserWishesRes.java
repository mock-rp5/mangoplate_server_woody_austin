package com.example.demo.src.user.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetUserWishesRes {
    private String distance;
    private Long storeId;
    private String imgUrl;
    private String subRegion;
    private String name;
    private int rating;
    private int viewCount;
    private int reviewCount;
    private int wishCheck;
    private int visitedCheck;
}
