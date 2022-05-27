package com.example.demo.src.visited.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PostVisitedReq {
    private Long userId;
    private Long storeId;
    private String isPublic;
    private String description;
}
