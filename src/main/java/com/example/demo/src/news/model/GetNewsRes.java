package com.example.demo.src.news.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class GetNewsRes {
    private Long reviewId;
    private String profileImgUrl;
    private String name;
    private String isHolic;
    private int reviewCount;
    private int followCount;
    private String evaluation;
    private Long storeId;
    private String storeName;
    private String review;
    private String reviewCreated;
    private int reviewLikes;
    private int reviewComments;
    private int wishCheck;
    private int visitedCheck;
    private int likeCheck;
    private List<GetImgRes> ImgList;
}
