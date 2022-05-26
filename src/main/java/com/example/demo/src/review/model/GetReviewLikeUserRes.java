package com.example.demo.src.review.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetReviewLikeUserRes {
    private Long userId;
    private String profileImgUrl;
    private String name;
    private String isHolic;
    private int reviewCount;
    private int followCount;
    private int followCheck;
}
