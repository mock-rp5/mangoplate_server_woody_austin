package com.example.demo.src.review.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class PostReviewReq {
    private List<ReviewImg> reviewImg;
    private Long storeId;
    private String review;
    private String evaluation;
}
