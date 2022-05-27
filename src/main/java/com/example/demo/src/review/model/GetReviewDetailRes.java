package com.example.demo.src.review.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetReviewDetailRes {
    private Long userId;
    private String profileImgUrl;
    private String userName;
    private String isHolic;
    private int userReviewCount;
    private int userFollowCount;
    private String evaluation;
    private Long storeId;
    private String storeName;
    private String review;
    private String updatedAt;
    private int likeCount;
    private int commentCount;
    private int wishCheck;
    private int visitedCheck;
    private int likeCheck;
}
