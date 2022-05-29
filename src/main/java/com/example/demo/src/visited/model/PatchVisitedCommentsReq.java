package com.example.demo.src.visited.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PatchVisitedCommentsReq {
    private Long tagUserId;
    private String comment;
}
