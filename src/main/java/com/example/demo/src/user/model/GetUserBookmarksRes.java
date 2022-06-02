package com.example.demo.src.user.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetUserBookmarksRes {
    private Long mylistId;
    private String userName;
    private String isHolic;
    private String mylistName;
    private String description;
    private String imgUrl;
    private int bookmarkCheck;
    private int bookmarkCount;
}
