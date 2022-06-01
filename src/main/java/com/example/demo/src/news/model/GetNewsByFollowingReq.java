package com.example.demo.src.news.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class GetNewsByFollowingReq {
    private Long userid;
    private List<String> evaluation;
    private List<String> region;
}
