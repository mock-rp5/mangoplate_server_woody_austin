package com.example.demo.src.user.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetMyProfileRes {
    private Long userId;
    private String profileImgUrl;
    private String name;
    private String email;
    private String phoneNumber;
}
