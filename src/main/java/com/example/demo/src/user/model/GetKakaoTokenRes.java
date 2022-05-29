package com.example.demo.src.user.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;


@Getter
@AllArgsConstructor
@Setter
public class GetKakaoTokenRes {
    private String accessToken;
    private String refreshToken;
}
