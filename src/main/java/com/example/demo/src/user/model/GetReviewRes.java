package com.example.demo.src.user.model;

import com.example.demo.src.news.model.GetImgRes;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetReviewRes {
    private String distance;
    private Long reviewId;
    private Long userId;
    private String profileImgUrl;
    private String name;
    private String isHolic;
    private int reviewCount;
    private int followCount;
    private String evaluation;
    private Long storeId;
    private String storeName;
    private String review;
    private int reviewLikes;
    private int reviewComments;
    private String reviewCreated;
    private int wishCheck;
    private int likeCheck;
    private List<GetImgRes> ImgList;
}
