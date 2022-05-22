package com.example.demo.src.user.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PostUserReq {
    private String userName;
    private String email;
    private String password;
    private String phoneNumber;
    private String imageUrl;
    private String agreeLocation;
    private String agreeMarketing;

}
