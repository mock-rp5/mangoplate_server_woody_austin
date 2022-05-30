package com.example.demo.src.user.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetUserPhotoRes {
    private String distance;
    private Long reviewImgId;
    private String imgUrl;
}
