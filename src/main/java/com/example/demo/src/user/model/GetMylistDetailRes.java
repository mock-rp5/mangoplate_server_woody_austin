package com.example.demo.src.user.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class GetMylistDetailRes {
    private String updatedAt;
    private int viewCount;
    private int bookmarkCount;
    private String mylistName;
    private String userName;
    private String profileImgUrl;
    private int reviewCount;
    private int followerCount;
    private int followCheck;
    private String description;
}
