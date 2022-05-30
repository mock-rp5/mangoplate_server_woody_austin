package com.example.demo.src.user.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class GetUserVisitedRes {

    private String distance;
    private Long visitedId;
    private Long userId;
    private String profileImgUrl;
    private String userName;
    private String isHolic;
    private int userReviewCount;
    private int userFollowCount;
    private Long storeId;
    private String storeTag;
    private String description;
    private int likeCount;
    private int commentCount;
    private String visitedCreated;
    private int visitedCheck;
    private int wishCheck;
    private int likeCheck;
    private List<GetVisitedStoreRes> storeInfo;
}
