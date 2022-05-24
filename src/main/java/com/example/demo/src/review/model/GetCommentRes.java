package com.example.demo.src.review.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetCommentRes {
    private Long userId;
    private String profileImgUrl;
    private String userName;
    private String isHolic;
    private String tagUserName;
    private String comment;
    private String updatedAt;
}
