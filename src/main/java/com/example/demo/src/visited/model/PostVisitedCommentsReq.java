package com.example.demo.src.visited.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PostVisitedCommentsReq {
    private Long visitedId;
    private Long userId;
    private Long tagUserId;
    private String comment;
}
