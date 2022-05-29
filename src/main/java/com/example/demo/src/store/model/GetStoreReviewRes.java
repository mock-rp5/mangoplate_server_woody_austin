package com.example.demo.src.store.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class GetStoreReviewRes {
    private Long reviewId;
    private String profileImgUrl;
    private Long userId;
    private String userName;
    private String isHolic;
    private int reviewCount;
    private int followCount;
    private String evaluation;
    private String review;
    private String updatedAt;
    private int reviewLikes;
    private int reviewComments;
    private int likeCheck;
    List<GetStoreReviewImgRes> imgList;
}
