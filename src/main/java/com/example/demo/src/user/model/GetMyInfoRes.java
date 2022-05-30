package com.example.demo.src.user.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetMyInfoRes {
    private Long userId;
    private String profileImgUrl;
    private int followerCount;
    private int followingCount;
    private String name;
    private String isHolic;
    private int eatDealCount;
    private int reviewCount;
    private int visitedCount;
    private int imgCount;
    private int wishesCount;
    private int myListCount;
    private int bookmarkCount;
    private int storeCount;
}
