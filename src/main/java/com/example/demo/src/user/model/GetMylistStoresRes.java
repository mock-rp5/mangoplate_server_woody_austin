package com.example.demo.src.user.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetMylistStoresRes {
    private Long storeId;
    private String name;
    private String address;
    private float rating;
    private int wishCheck;
    private int visitedCheck;
    private String userName;
    private String profileImgUrl;
    private String isHolic;
    private String review;
}
