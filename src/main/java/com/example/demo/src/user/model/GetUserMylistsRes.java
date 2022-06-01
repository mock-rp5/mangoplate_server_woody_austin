package com.example.demo.src.user.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetUserMylistsRes {
    private Long mylistId;
    private String name;
    private String description;
    private String imgUrl;
    private int bookmarkCheck;
    private int bookmarkCount;
}
