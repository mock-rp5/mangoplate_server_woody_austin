package com.example.demo.src.user;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.user.model.*;
import com.example.demo.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.io.IOException;

import java.util.List;


import static com.example.demo.config.BaseResponseStatus.*;
import static com.example.demo.utils.ValidationRegex.isRegexEmail;

@RestController
@RequestMapping("/users")
public class UserController {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private final UserProvider userProvider;
    @Autowired
    private final UserService userService;
    @Autowired
    private final JwtService jwtService;



    public UserController(UserProvider userProvider, UserService userService, JwtService jwtService){
        this.userProvider = userProvider;
        this.userService = userService;
        this.jwtService = jwtService;
    }


    /**
     * 회원 조회 API
     * [GET] /users
     * 회원 번호 및 이메일 검색 조회 API
     * [GET] /users? Email=
     * @return BaseResponse<List<GetUserRes>>
     */
    //Query String
    @ResponseBody
    @GetMapping("") // (GET) 127.0.0.1:9000/app/users
    public BaseResponse<List<GetUserRes>> getUsers(@RequestParam(required = false) String Email) {
        try{
            if(Email == null){
                List<GetUserRes> getUsersRes = userProvider.getUsers();
                return new BaseResponse<>(getUsersRes);
            }
            // Get Users
            List<GetUserRes> getUsersRes = userProvider.getUsersByEmail(Email);
            return new BaseResponse<>(getUsersRes);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 회원가입 API
     * [POST] /users
     * @return BaseResponse<PostUserRes>
     */
    // Body
    @ResponseBody
    @PostMapping("")
    public BaseResponse<PostUserRes> createUser(@RequestBody PostUserReq postUserReq) {

        if(postUserReq.getEmail() == null){
            return new BaseResponse<>(POST_USERS_EMPTY_EMAIL);
        }

        //null validation
        if(postUserReq.getEmail() == null){
            return new BaseResponse<>(POST_USERS_EMPTY_EMAIL);
        }
        if(postUserReq.getUserName() == null){
            return new BaseResponse<>(POST_USERS_EMPTY_NAME);
        }
        if(postUserReq.getPassword() == null){
            return new BaseResponse<>(POST_USERS_EMPTY_PASSWORD);
        }
        if(postUserReq.getPhoneNumber() == null){
            return new BaseResponse<>(POST_USERS_EMPTY_PHONENUMBER);
        }

        //이메일 정규표현
        if(!isRegexEmail(postUserReq.getEmail())){
            return new BaseResponse<>(POST_USERS_INVALID_EMAIL);
        }
        try{
            PostUserRes postUserRes = userService.createUser(postUserReq);
            return new BaseResponse<>(postUserRes);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }
    /**
     * 로그인 API
     * [POST] /users/login
     * @return BaseResponse<PostLoginRes>
     */
    @ResponseBody
    @GetMapping("/login")
    public BaseResponse<PostLoginRes> logIn(@RequestBody PostLoginReq postLoginReq){
        if(postLoginReq.getEmail() == null) {
            return new BaseResponse<>(POST_USERS_EMPTY_EMAIL);
        }
        if(postLoginReq.getPassword() == null) {
            return new BaseResponse<>(POST_USERS_EMPTY_PASSWORD);
        }
        if(!isRegexEmail(postLoginReq.getEmail())){
            return new BaseResponse<>(POST_USERS_INVALID_EMAIL);
        }
        try{
            PostLoginRes postLoginRes = userProvider.logIn(postLoginReq);
            return new BaseResponse<>(postLoginRes);
        } catch (BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }


    /**
     * 카카오 로그인 API
     * [POST] /users/oauth/code?=
     * @return BaseResponse<PostUserKakaoRes>
     */
    @ResponseBody
    @GetMapping("/oauth")
    public BaseResponse<PostLoginRes> KakaoLogIn(@RequestParam String code) {

        try {
            String access_Token = userService.getKaKaoAccessToken(code);
            PostUserKakaoLoginReq postUserKakaoLoginReq = userService.getKakaoUser(access_Token);
            PostUserKakaoReq postUserKakaoReq = new PostUserKakaoReq(postUserKakaoLoginReq.getKakaoName(), postUserKakaoLoginReq.getKakaoEmail());

            PostLoginRes postLoginRes = null;
            //카카오 DB 저장 후 postUserKakaoReq 연동
            //만약 유저 정보가 없으면 카카오 db 저장 후 유저 정보에 저장한다.
            if (userProvider.getKakaoLogin(postUserKakaoLoginReq.getKakaoEmail()) == 0) {
                userService.createOauthUser(postUserKakaoLoginReq);
                postLoginRes = userService.createKakaoUser(postUserKakaoReq);
            }
            //만약 유저 정보가 User 테이블에 있으면 로그인 후 jwt access_Token 발급

            if (userProvider.checkEmail(postUserKakaoLoginReq.getKakaoEmail()) == 1) {
                postLoginRes = userProvider.logInKakao(postUserKakaoLoginReq.getKakaoEmail());

            }
            return new BaseResponse<>(postLoginRes);
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

    /**
     * 유저 위치정보 업데이트 API
     * [PATCH] /users/location/:userId
     * @return BaseResponse<String>
     */
    @ResponseBody
    @PatchMapping("/location/{userId}")
    public BaseResponse<String> updateUserLocation(@PathVariable("userId") Long userId, @RequestBody PatchUserLocationReq patchUserLocationReq) {
        if(-90 > patchUserLocationReq.getLatitude() || patchUserLocationReq.getLatitude() > 90) {
            return new BaseResponse<>(WRONG_LATITUDE_VALUE);
        }
        if(-180 > patchUserLocationReq.getLongitude() || patchUserLocationReq.getLongitude() > 180) {
            return new BaseResponse<>(WRONG_LONGITUDE_VALUE);
        }
        try {
            Long userIdByJwt = jwtService.getUserIdx();

            if (userId != userIdByJwt) {
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            userService.updateUserLocation(patchUserLocationReq, userId);
            String result = "성공적으로 업데이트 완료";
                return new BaseResponse<>(result);
            } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
            }
        }

    }
