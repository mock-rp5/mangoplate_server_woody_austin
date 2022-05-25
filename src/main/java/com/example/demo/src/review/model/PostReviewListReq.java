package com.example.demo.src.review.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PostReviewListReq {
    private Long userId;
    private Long storeId;
    private String review;
    private String evaluation;
}
