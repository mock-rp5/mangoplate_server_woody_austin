package com.example.demo.src.visited.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetVisitedLikeUserRes {
    private Long userId;
    private String profileImgUrl;
    private String userName;
    private String isHolic;
    private int reviewCount;
    private int followerCount;
    private int followCheck;
}
