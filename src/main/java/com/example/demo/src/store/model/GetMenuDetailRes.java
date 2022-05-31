package com.example.demo.src.store.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetMenuDetailRes {
    private Long storeId;
    private String menuName;
    private int price;
    private String lastUpdate;
}
