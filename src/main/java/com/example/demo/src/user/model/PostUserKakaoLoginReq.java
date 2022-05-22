package com.example.demo.src.user.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PostUserKakaoLoginReq {
    private String kakaoName;
    private Long kakaoId;
    private String kakaoEmail;
}
