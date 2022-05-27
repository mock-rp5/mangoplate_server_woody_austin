package com.example.demo.src.user.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetUserFollowerListRes {
    private Long userId;
    private String profileImgUrl;
    private String name;
    private String isHolic;
    private int reviewCount;
    private int followerCount;
    private int followCheck;


}
