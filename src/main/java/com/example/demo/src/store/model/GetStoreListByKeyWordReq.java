package com.example.demo.src.store.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class GetStoreListByKeyWordReq {
    private Long userId;
    private List<String> region;
    private String keyword;
}
