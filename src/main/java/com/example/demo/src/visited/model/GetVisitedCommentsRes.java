package com.example.demo.src.visited.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetVisitedCommentsRes {
    private Long commentId;
    private Long userId;
    private String profileImgUrl;
    private String userName;
    private String isHolic;
    private String tagUserName;
    private String comment;
    private String updatedAt;
}
