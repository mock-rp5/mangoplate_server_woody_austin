package com.example.demo.src.visited.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class GetVisitedRes {
    GetVisitedDetailRes getVisitedDetailRes;
    List<GetVisitedCommentsRes> getVisitedCommentsResList;
}
