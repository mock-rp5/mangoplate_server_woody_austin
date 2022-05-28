package com.example.demo.src.visited.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class GetVisitedDetailRes {
    private Long visitedId;
    private Long userId;
    private String profileImgUrl;
    private String userName;
    private String isHolic;
    private int userReviewCount;
    private int userFollowCount;
    private Long storeId;
    private String storeName;
    private String storeImgUrl;
    private String subRegion;
    private String description;
    private String foodCategory;
    private int storeViewCount;
    private int storeReviewCount;
    private String updatedAt;
    private int likeCount;
    private int commentCount;
    private int wishCheck;
    private int visitedCheck;
    private int likeCheck;
}