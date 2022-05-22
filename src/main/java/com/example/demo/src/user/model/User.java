package com.example.demo.src.user.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class User {
<<<<<<< HEAD
    private Long userIdx;
    private String ID;
    private String userName;
    private String password;
    private String email;
=======
    private int userId;
    private String userName;
    private String email;
    private String password;
    private String phoneNumber;
>>>>>>> woody
}
