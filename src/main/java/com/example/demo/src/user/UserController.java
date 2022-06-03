package com.example.demo.src.user;

import com.example.demo.src.user.model.DeleteUserFollowReq;
import net.nurigo.java_sdk.exceptions.CoolsmsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.user.model.*;
import com.example.demo.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


import java.util.ArrayList;
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


    public UserController(UserProvider userProvider, UserService userService, JwtService jwtService) {
        this.userProvider = userProvider;
        this.userService = userService;
        this.jwtService = jwtService;
    }


    /**
     * 회원 조회 API
     * [GET] /users
     * 회원 번호 및 이메일 검색 조회 API
     * [GET] /users? Email=
     *
     * @return BaseResponse<List < GetUserRes>>
     */
    //Query String
    @ResponseBody
    @GetMapping("") // (GET) 127.0.0.1:9000/app/users
    public BaseResponse<List<GetUserRes>> getUsers(@RequestParam(required = false) String Email) {
        try {
            if (Email == null) {
                List<GetUserRes> getUsersRes = userProvider.getUsers();
                return new BaseResponse<>(getUsersRes);
            }
            // Get Users
            List<GetUserRes> getUsersRes = userProvider.getUsersByEmail(Email);
            return new BaseResponse<>(getUsersRes);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 회원가입 API
     * [POST] /users
     *
     * @return BaseResponse<PostUserRes>
     */
    // Body
    @ResponseBody
    @PostMapping("")
    public BaseResponse<PostUserRes> createUser(@RequestBody PostUserReq postUserReq) {

        if (postUserReq.getEmail() == null) {
            return new BaseResponse<>(POST_USERS_EMPTY_EMAIL);
        }

        //null validation
        if (postUserReq.getEmail() == null) {
            return new BaseResponse<>(POST_USERS_EMPTY_EMAIL);
        }
        if (postUserReq.getUserName() == null) {
            return new BaseResponse<>(POST_USERS_EMPTY_NAME);
        }
        if (postUserReq.getPassword() == null) {
            return new BaseResponse<>(POST_USERS_EMPTY_PASSWORD);
        }
        if (postUserReq.getPhoneNumber() == null) {
            return new BaseResponse<>(POST_USERS_EMPTY_PHONENUMBER);
        }

        //이메일 정규표현
        if (!isRegexEmail(postUserReq.getEmail())) {
            return new BaseResponse<>(POST_USERS_INVALID_EMAIL);
        }
        try {
            PostUserRes postUserRes = userService.createUser(postUserReq);
            return new BaseResponse<>(postUserRes);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 로그인 API
     * [POST] /users/login
     *
     * @return BaseResponse<PostLoginRes>
     */
    @ResponseBody
    @PostMapping("/login")
    public BaseResponse<PostLoginRes> logIn(@RequestBody PostLoginReq postLoginReq) {
        if (postLoginReq.getEmail() == null) {
            return new BaseResponse<>(POST_USERS_EMPTY_EMAIL);
        }
        if (postLoginReq.getPassword() == null) {
            return new BaseResponse<>(POST_USERS_EMPTY_PASSWORD);
        }
        if (!isRegexEmail(postLoginReq.getEmail())) {
            return new BaseResponse<>(POST_USERS_INVALID_EMAIL);
        }
        try {
            PostLoginRes postLoginRes = userProvider.logIn(postLoginReq);
            return new BaseResponse<>(postLoginRes);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }


    /**
     * 카카오 로그인 액세스 토큰 발급
     */
    @ResponseBody
    @GetMapping("/oauth")
    public BaseResponse<String> getKaKaoAccessToken(@RequestParam String code){
        List<GetKakaoTokenRes> getKaKaoAccessToken = null;
        try {
            String accessToken = userService.getKaKaoAccessToken(code);
            return  new BaseResponse<>(accessToken);
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }

    }


    /**
     * 카카오 로그인 API
     * [POST] /users/oauth/code?=
     *
     * @return BaseResponse<PostUserKakaoRes>
     */


    @ResponseBody
    @PostMapping("/oauth/logIn")
    public BaseResponse<PostLoginRes> KakaoLogIn(@RequestBody PostKakaoLogInReq postKakaoLogInReq) {

        try {

            PostUserKakaoLoginReq postUserKakaoLoginReq = userService.getKakaoUser(postKakaoLogInReq.getAccessToken());
            PostUserKakaoReq postUserKakaoReq = new PostUserKakaoReq(postUserKakaoLoginReq.getKakaoName(), postUserKakaoLoginReq.getKakaoEmail());

            PostLoginRes postLoginRes = null;
            //카카오 DB 저장 후 postUserKakaoReq 연동
            //만약 유저 정보가 없으면 카카오 db 저장 후 유저 정보에 저장한다.
            if (userProvider.getKakaoLogin(postUserKakaoLoginReq.getKakaoEmail()) == 0) {
                Long userIdx = userService.createKakaoUser(postUserKakaoReq);
                userService.createOauthUser(postUserKakaoLoginReq,userIdx);

            }
            //만약 유저 정보가 카카오 테이블에 있으면 로그인 후 jwt access_Token 발급

            if (userProvider.getKakaoLogin(postUserKakaoLoginReq.getKakaoEmail()) == 1) {
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
     *
     * @return BaseResponse<String>
     */
    @ResponseBody
    @PatchMapping("/location/{userId}")
    public BaseResponse<String> updateUserLocation(@PathVariable("userId") Long userId, @RequestBody PatchUserLocationReq patchUserLocationReq) {
        if (-90 > patchUserLocationReq.getLatitude() || patchUserLocationReq.getLatitude() > 90) {
            return new BaseResponse<>(WRONG_LATITUDE_VALUE);
        }
        if (-180 > patchUserLocationReq.getLongitude() || patchUserLocationReq.getLongitude() > 180) {
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

    /**
     * 팔로우 API
     * [POST] /follow/{userId}/{followedUserId}
     *
     * @return BaseResponse<PostLoginRes>
     */
    @ResponseBody
    @PostMapping("/follow/{userId}/{followedUserId}")
    public BaseResponse<String> createUserFollow(@PathVariable("userId") Long userId, @PathVariable("followedUserId") Long followedUserId) {
        try {
            Long userIdxByJwt = jwtService.getUserIdx();
            if (userId != userIdxByJwt) {
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            GetUserFollowReq getUserFollowReq = new GetUserFollowReq(userId, followedUserId);
            userService.createUserFollow(getUserFollowReq);
            String result = "팔로우 성공";
            return new BaseResponse<>(result);
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }

    }

    /**
     * 언팔로우 API
     * [POST] /follow/{userId}/{followedUserId}
     *
     * @return BaseResponse<PostLoginRes>
     */
    @ResponseBody
    @DeleteMapping("/follow/{userId}/{followedUserId}")
    public BaseResponse<String> userUnFollow(@PathVariable("userId") Long userId, @PathVariable("followedUserId") Long followedUserId) {
        try {
            Long userIdxByJwt = jwtService.getUserIdx();
            if (userId != userIdxByJwt) {
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            DeleteUserFollowReq deleteUserFollowReq = new DeleteUserFollowReq(userId, followedUserId);
            userService.userUnFollow(deleteUserFollowReq);
            String result = "팔로우 취소 성공";
            return new BaseResponse<>(result);
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }

    }

    /**
     * 팔로워 조회 API
     * [GET] /follower/{userId}/{followedUserId}
     *
     * @return BaseResponse<GetFollowerListRes>
     */
    @ResponseBody
    @GetMapping("/follower/{userId}/{followedUserId}")
    public BaseResponse<List<GetUserFollowerListRes>> getUserFollower(@PathVariable("userId") Long userId, @PathVariable("followedUserId") Long followedUserId)  {
        try {
            Long userIdxByJwt = jwtService.getUserIdx();
            if (userId != userIdxByJwt) {
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            GetUserFollowListReq getUserFollowReq = new GetUserFollowListReq(userId, followedUserId);
            List<GetUserFollowerListRes> getUserFollowerListRes = userProvider.getUserFollower(getUserFollowReq);
            return new BaseResponse<>(getUserFollowerListRes);
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

    /**
     * 팔로잉 조회 API
     * [GET] /following/{userId}/{followedUserId}
     *
     * @return BaseResponse<GetFollowerListRes>
     */
    @ResponseBody
    @GetMapping("/following/{userId}/{followedUserId}")
    public BaseResponse<List<GetUserFollowerListRes>> getUserFollowing(@PathVariable("userId") Long userId, @PathVariable("followedUserId") Long followedUserId)
    {
        try {
            Long userIdxByJwt = jwtService.getUserIdx();
            if (userId != userIdxByJwt) {
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            GetUserFollowListReq getUserFollowReq = new GetUserFollowListReq(userId, followedUserId);
            List<GetUserFollowerListRes> getUserFollowerListRes = userProvider.getUserFollowing(getUserFollowReq);
            return new BaseResponse<>(getUserFollowerListRes);
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }
    /**
     * 프로필 조회 API
     * [GET] /profile/{userId}/{followedUserId}
     *
     * @return BaseResponse<GetUserProfileRes>
     */
    @ResponseBody
    @GetMapping("/profile/{userId}/{profileUserId}")
    public BaseResponse<List<GetUserProfileRes>> getUserProfile(@PathVariable("userId") Long userId, @PathVariable("profileUserId") Long profileUserId){
        try {
            Long userIdxByJwt = jwtService.getUserIdx();
            if (userId != userIdxByJwt) {
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            GetUserProfileReq getUserProfileReq = new GetUserProfileReq(userId, profileUserId);
            List<GetUserProfileRes> getUserProfileRes = userProvider.getUserProfile(getUserProfileReq);
            return new BaseResponse<>(getUserProfileRes);
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }
    /**
     * 유저 이메일수정 API
     * [PATCH] /profile_img//{userId}
     *
     * @return BaseResponse<String>
     */
    @ResponseBody
    @PatchMapping("/profile_img/{userId}")
    public BaseResponse<String> patchUserProfileImg(@PathVariable("userId")Long userId,@RequestBody PatchUserProfileImgReq patchUserProfileImgReq){
        try {

            Long userIdxByJwt = jwtService.getUserIdx();
            if (userId != userIdxByJwt) {
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            userService.patchUserProfileImg(userId, patchUserProfileImgReq);
            String result="수정 성공";
            return new BaseResponse<>(result);
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }
    /**
     * 유저 이름 수정 API
     * [PATCH] /name/{userId}
     *
     * @return BaseResponse<String>
     */
    @ResponseBody
    @PatchMapping("/name/{userId}")
    public BaseResponse<String> patchUserName(@PathVariable("userId")Long userId,@RequestBody PatchUserNameReq patchUserNameReq){
        try {
            Long userIdxByJwt = jwtService.getUserIdx();
            if (userId != userIdxByJwt) {
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            userService.patchUserName(userId,patchUserNameReq);
            String result="수정 성공";
            return new BaseResponse<>(result);
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }
    /**
     * 유저 이메일수정 API
     * [PATCH] /email/{userId}
     *
     * @return BaseResponse<String>
     */
    @ResponseBody
    @PatchMapping("/email/{userId}")
    public BaseResponse<String> patchUserEmial(@PathVariable("userId")Long userId,@RequestBody PatchUserEmailReq patchUserEmailReq){
        try {
            if (!isRegexEmail(patchUserEmailReq.getEmail())) {
                return new BaseResponse<>(POST_USERS_INVALID_EMAIL);
            }
            Long userIdxByJwt = jwtService.getUserIdx();
            if (userId != userIdxByJwt) {
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            userService.patchUserEmail(userId, patchUserEmailReq);
            String result="수정 성공";
            return new BaseResponse<>(result);
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }
    //전화번호 인증 오스틴
    @GetMapping("/sms")
        public String mySms(){
        return "order/sms";
    }

    //전화번호 인증 오스틴
    @ResponseBody
    @GetMapping("/check/sendSMS")
    public BaseResponse<String> sendSMS(@RequestParam(value="to")String to)throws CoolsmsException {
        String result = userService.PhoneNumberCheck(to);
        return new BaseResponse<>(result);
    }


    /**
     * 유저 전화번호 수정 API
     * [PATCH] /phone_number/{userId}
     *
     * @return BaseResponse<String>
     */
    @ResponseBody
    @PatchMapping("/phone_number/{userId}")
    public BaseResponse<String> patchUserPhoneNumber(@PathVariable("userId")Long userId,@RequestBody PatchUserPhoneNumberReq patchUserPhoneNumberReq){
        try {
            Long userIdxByJwt = jwtService.getUserIdx();
            if (userId != userIdxByJwt) {
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            userService.patchUserPhoneNumber(userId,patchUserPhoneNumberReq);
            String result="수정 성공";
            return new BaseResponse<>(result);
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }
    /**
     * 유저 정보 수정을 위한 GET API
     * [GET] /my_profile/{userId}
     *
     * @return BaseResponse<GetUserNameRes>
     */
    @ResponseBody
    @GetMapping("/my_profile/{userId}")
    public BaseResponse<List<GetMyProfileRes>> getUserMyProfile(@PathVariable("userId")Long userId){
        try {
            Long userIdxByJwt = jwtService.getUserIdx();
            if (userId != userIdxByJwt) {
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            List<GetMyProfileRes> getMyProfileRes=userProvider.getMyProfile(userId);
            return new BaseResponse<>(getMyProfileRes);
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

    /**
     * 유저 이름 수정을 위한 GET API
     * [GET] /name/{userId}
     *
     * @return BaseResponse<GetUserNameRes>
     */
    @ResponseBody
    @GetMapping("/name/{userId}")
    public BaseResponse<List<GetUserNameRes>> getUserName(@PathVariable("userId")Long userId){
        try {
            Long userIdxByJwt = jwtService.getUserIdx();
            if (userId != userIdxByJwt) {
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            List<GetUserNameRes> getUserNameRes=userProvider.getUserName(userId);
            return new BaseResponse<>(getUserNameRes);
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }
    /**
     * 유저 이메일수정을 위한 GET API
     * [GET] /email/{userId}
     *
     * @return BaseResponse<GetUserEmailRes>
     */
    @ResponseBody
    @GetMapping("/email/{userId}")
    public BaseResponse<List<GetUserEmailRes>> getUserEmial(@PathVariable("userId")Long userId){
        try {

            Long userIdxByJwt = jwtService.getUserIdx();
            if (userId != userIdxByJwt) {
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            List<GetUserEmailRes> getUserEmailRes=userProvider.getUserEmail(userId);

            return new BaseResponse<>(getUserEmailRes);
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

    /**
     * 유저의 리뷰 조회
     *
     */
    @ResponseBody
    @GetMapping("/reviews/{userId}/{profileUserId}")
    public BaseResponse<List<GetReviewRes>> getUserReview(@PathVariable("userId") Long userId,@PathVariable("profileUserId") Long profileUserId,@RequestParam(required = false,defaultValue = "1") List<Integer> evaluation,
                                                          @RequestParam(required = false,defaultValue = "1") int order, @RequestParam(required = false,defaultValue = "all") List<String> region,
                                                          @RequestParam(required = false,defaultValue = "0") List<Integer> category, @RequestParam(required = false,defaultValue = "0") List<Integer> priceRange,
                                                          @RequestParam(required = false,defaultValue = "1")int parking){
        try {
            Long userIdxByJwt = jwtService.getUserIdx();
            if (userId != userIdxByJwt) {
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            List<String> evaluationList = new ArrayList<>(evaluation.size());
            for (int i = 0; i < evaluation.size(); i++) {
                if (evaluation.get(i) == 1) {
                    evaluationList.add("맛있다!");
                } else if (evaluation.get(i) == 2) {
                    evaluationList.add("괜찮다");
                } else {
                    evaluationList.add("별로");
                }
            }

            String filter="";
            if(order==1){
                filter="recent";
            }
            else if(order==2){
                filter="distance";
            }
            else{
                return new BaseResponse<>(NON_EXIST_FILTER);
            }

            List<String> parkingInfo=new ArrayList<>(2);
            if(parking==2){
                parkingInfo.add("가능");
            }else if(parking==1) {
                parkingInfo.add("가능");
                parkingInfo.add("불가");
            }
            else{
                return new BaseResponse<>(NON_EXIST_FILTER);
            }


            List<String> foodCategory=new ArrayList(category.size());
            for(int i=0;i<category.size();i++){
                if(category.get(i)==1){
                    foodCategory.add("한식");
                }
                else if(category.get(i)==2){
                    foodCategory.add("일식");
                }
                else if(category.get(i)==3){
                    foodCategory.add("중식");
                }
                else if(category.get(i)==4){
                    foodCategory.add("양식");
                }
                else if(category.get(i)==5){
                    foodCategory.add("세계음식");
                }
                else if(category.get(i)==6){
                    foodCategory.add("뷔페");
                }
                else if(category.get(i)==7){
                    foodCategory.add("카페");
                }else if(category.get(i)==8){
                    foodCategory.add("주점");
                }
                else if (category.get(i)==0){
                    foodCategory.add("all");
                }
            }

            List<String> price=new ArrayList<>(priceRange.size());
            for (int i=0;i<priceRange.size();i++){
                if(priceRange.get(i) >4){
                    return new BaseResponse<>(NON_EXIST_FILTER);
                }
            }

            for (int i=0;i<priceRange.size();i++){
                if(priceRange.get(i)==1){
                    price.add("만원 미만/1인");
                }
                else if(priceRange.get(i)==2){
                    price.add("만원~2만원/1인");
                }
                else if(priceRange.get(i)==3){
                    price.add("2만원~3만원/1인");
                }
                else if(priceRange.get(i)==4){
                    price.add("3만원 이상");
                }
                else if(priceRange.get(i)==0){
                    price.add("all");
                }
            }
            System.out.println(price);
            GetUserReviewReq getUserReviewReq =new GetUserReviewReq(userId, profileUserId, evaluationList,filter,region,foodCategory, price, parkingInfo);
            List<GetReviewRes> getNewsRes=userProvider.getUserReview(getUserReviewReq);
            return new BaseResponse<>(getNewsRes);
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }


    /**
     * 유저의 방문 조회
     *
     */
    @ResponseBody
    @GetMapping("/visited/{userId}/{profileUserId}")
    public BaseResponse<List<GetUserVisitedRes>> getUserVisited(@PathVariable("userId") Long userId,@PathVariable("profileUserId") Long profileUserId,@RequestParam(required = false,defaultValue = "1") int order,
                                                         @RequestParam(required = false,defaultValue = "all") List<String> region,@RequestParam(required = false,defaultValue = "0") List<Integer> category,
                                                         @RequestParam(required = false,defaultValue = "0") List<Integer> priceRange,@RequestParam(required = false,defaultValue = "1")int parking
                                                         ){

        try {
            Long userIdxByJwt = jwtService.getUserIdx();
            if (userId != userIdxByJwt) {
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            String filter="";
            if(order==1){
                filter="recent";
            }
            else if(order==2){
                filter="distance";
            }
            else{
                return new BaseResponse<>(NON_EXIST_FILTER);
            }

            List<String> parkingInfo=new ArrayList<>(2);
            if(parking==2){
                parkingInfo.add("가능");
            }else if(parking==1) {
                parkingInfo.add("가능");
                parkingInfo.add("불가");
            }
            else{
                return new BaseResponse<>(NON_EXIST_FILTER);
            }


            List<String> foodCategory=new ArrayList(category.size());
            for(int i=0;i<category.size();i++){
                if(category.get(i)==1){
                    foodCategory.add("한식");
                }
                else if(category.get(i)==2){
                    foodCategory.add("일식");
                }
                else if(category.get(i)==3){
                    foodCategory.add("중식");
                }
                else if(category.get(i)==4){
                    foodCategory.add("양식");
                }
                else if(category.get(i)==5){
                    foodCategory.add("세계음식");
                }
                else if(category.get(i)==6){
                    foodCategory.add("뷔페");
                }
                else if(category.get(i)==7){
                    foodCategory.add("카페");
                }else if(category.get(i)==8){
                    foodCategory.add("주점");
                }
                else if (category.get(i)==0){
                    foodCategory.add("all");
                }
            }

            List<String> price=new ArrayList<>(priceRange.size());
            for (int i=0;i<priceRange.size();i++){
                if(priceRange.get(i) >4){
                    return new BaseResponse<>(NON_EXIST_FILTER);
                }
            }

            for (int i=0;i<priceRange.size();i++){
                if(priceRange.get(i)==1){
                    price.add("만원 미만/1인");
                }
                else if(priceRange.get(i)==2){
                    price.add("만원~2만원/1인");
                }
                else if(priceRange.get(i)==3){
                    price.add("2만원~3만원/1인");
                }
                else if(priceRange.get(i)==4){
                    price.add("3만원 이상");
                }
                else if(priceRange.get(i)==0){
                    price.add("all");
                }
            }
            GetTimeLineReq getTimeLineReq =new GetTimeLineReq(userId, profileUserId, filter,region,foodCategory, price, parkingInfo);
            List<GetUserVisitedRes> getVisitedRes=userProvider.getUserVisited(getTimeLineReq);
            return new BaseResponse<>(getVisitedRes);
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }
    /**
     * 유저의 업로드사진 조회
     *
     */
    @ResponseBody
    @GetMapping("/photos/{userId}/{profileUserId}")
    public BaseResponse<List<GetUserPhotoRes>> getUserPhotos(@PathVariable("userId") Long userId,@PathVariable("profileUserId") Long profileUserId,@RequestParam(required = false,defaultValue = "1") int order,
                                                        @RequestParam(required = false,defaultValue = "all") List<String> region,@RequestParam(required = false,defaultValue = "0") List<Integer> category,
                                                        @RequestParam(required = false,defaultValue = "0") List<Integer> priceRange,@RequestParam(required = false,defaultValue = "1")int parking){

        try{
            Long userIdxByJwt = jwtService.getUserIdx();
            if (userId != userIdxByJwt) {
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            String filter="";
            if(order==1){
                filter="recent";
            }
            else if(order==2){
                filter="distance";
            }
            else{
                return new BaseResponse<>(NON_EXIST_FILTER);
            }

            List<String> parkingInfo=new ArrayList<>(2);
            if(parking==2){
                parkingInfo.add("가능");
            }else if(parking==1) {
                parkingInfo.add("가능");
                parkingInfo.add("불가");
            }
            else{
                return new BaseResponse<>(NON_EXIST_FILTER);
            }


            List<String> foodCategory=new ArrayList(category.size());
            for(int i=0;i<category.size();i++){
                if(category.get(i)==1){
                    foodCategory.add("한식");
                }
                else if(category.get(i)==2){
                    foodCategory.add("일식");
                }
                else if(category.get(i)==3){
                    foodCategory.add("중식");
                }
                else if(category.get(i)==4){
                    foodCategory.add("양식");
                }
                else if(category.get(i)==5){
                    foodCategory.add("세계음식");
                }
                else if(category.get(i)==6){
                    foodCategory.add("뷔페");
                }
                else if(category.get(i)==7){
                    foodCategory.add("카페");
                }else if(category.get(i)==8){
                    foodCategory.add("주점");
                }
                else if (category.get(i)==0){
                    foodCategory.add("all");
                }
            }

            List<String> price=new ArrayList<>(priceRange.size());
            for (int i=0;i<priceRange.size();i++){
                if(priceRange.get(i) >4){
                    return new BaseResponse<>(NON_EXIST_FILTER);
                }
            }

            for (int i=0;i<priceRange.size();i++){
                if(priceRange.get(i)==1){
                    price.add("만원 미만/1인");
                }
                else if(priceRange.get(i)==2){
                    price.add("만원~2만원/1인");
                }
                else if(priceRange.get(i)==3){
                    price.add("2만원~3만원/1인");
                }
                else if(priceRange.get(i)==4){
                    price.add("3만원 이상");
                }
                else if(priceRange.get(i)==0){
                    price.add("all");
                }
            }
            GetTimeLineReq getTimeLineReq =new GetTimeLineReq(userId, profileUserId, filter,region,foodCategory, price, parkingInfo);
            List<GetUserPhotoRes> getUserPhotoRes=userProvider.getUserPhotos(getTimeLineReq);
            return new BaseResponse<>(getUserPhotoRes);

        }catch (BaseException e){
            return new BaseResponse<>(e.getStatus());
        }
    }

    //유저 가고싶어요 조회
    @ResponseBody
    @GetMapping("/wishes/{userId}/{profileUserId}")
    public BaseResponse<List<GetUserWishesRes>> getUserWishes(@PathVariable("userId") Long userId,@PathVariable("profileUserId") Long profileUserId,@RequestParam(required = false,defaultValue = "1") int order,
                                                              @RequestParam(required = false,defaultValue = "all") List<String> region,@RequestParam(required = false,defaultValue = "0") List<Integer> category,
                                                              @RequestParam(required = false,defaultValue = "0") List<Integer> priceRange,@RequestParam(required = false,defaultValue = "1")int parking){
        try{
            Long userIdxByJwt = jwtService.getUserIdx();
            if (userId != userIdxByJwt) {
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            String filter="";
            if(order==1){
                filter="recent";
            }
            else if(order==2){
                filter="distance";
            }
            else{
                return new BaseResponse<>(NON_EXIST_FILTER);
            }

            List<String> parkingInfo=new ArrayList<>(2);
            if(parking==2){
                parkingInfo.add("가능");
            }else if(parking==1) {
                parkingInfo.add("가능");
                parkingInfo.add("불가");
            }
            else{
                return new BaseResponse<>(NON_EXIST_FILTER);
            }


            List<String> foodCategory=new ArrayList(category.size());
            for(int i=0;i<category.size();i++){
                if(category.get(i)==1){
                    foodCategory.add("한식");
                }
                else if(category.get(i)==2){
                    foodCategory.add("일식");
                }
                else if(category.get(i)==3){
                    foodCategory.add("중식");
                }
                else if(category.get(i)==4){
                    foodCategory.add("양식");
                }
                else if(category.get(i)==5){
                    foodCategory.add("세계음식");
                }
                else if(category.get(i)==6){
                    foodCategory.add("뷔페");
                }
                else if(category.get(i)==7){
                    foodCategory.add("카페");
                }else if(category.get(i)==8){
                    foodCategory.add("주점");
                }
                else if (category.get(i)==0){
                    foodCategory.add("all");
                }
            }

            List<String> price=new ArrayList<>(priceRange.size());
            for (int i=0;i<priceRange.size();i++){
                if(priceRange.get(i) >4){
                    return new BaseResponse<>(NON_EXIST_FILTER);
                }
            }

            for (int i=0;i<priceRange.size();i++){
                if(priceRange.get(i)==1){
                    price.add("만원 미만/1인");
                }
                else if(priceRange.get(i)==2){
                    price.add("만원~2만원/1인");
                }
                else if(priceRange.get(i)==3){
                    price.add("2만원~3만원/1인");
                }
                else if(priceRange.get(i)==4){
                    price.add("3만원 이상");
                }
                else if(priceRange.get(i)==0){
                    price.add("all");
                }
            }
            GetTimeLineReq getTimeLineReq =new GetTimeLineReq(userId, profileUserId, filter,region,foodCategory, price, parkingInfo);
            List<GetUserWishesRes> getUserWishes=userProvider.getUserWishes(getTimeLineReq);
            return new BaseResponse<>(getUserWishes);

        }catch (BaseException e){
            return new BaseResponse<>(e.getStatus());
        }
    }

    /**
     * 내정보 조회 API
     * [GET] /profile/{userId}
     *
     * @return BaseResponse<GetMyInfoRes>
     */
    @ResponseBody
    @GetMapping("/profile/{userId}")
    public BaseResponse<GetMyInfoRes> getMyInfo(@PathVariable("userId") Long userId){
        try {
            Long userIdxByJwt = jwtService.getUserIdx();
            if (userId != userIdxByJwt) {
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            GetMyInfoRes getMyInfoRes = userProvider.getMyInfo(userId);
            return new BaseResponse<>(getMyInfoRes);
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

    /**
     * 마이리스트 생성 API
     * [POST] /mylists/{userId}
     *
     * @return BaseResponse<Long>
     */
    @ResponseBody
    @PostMapping("/mylists/{userId}")
    public BaseResponse<Long> createMylist(@PathVariable("userId") Long userId, @RequestBody PostMylistReq postMylistReq) {
        try {
            Long userIdxByJwt = jwtService.getUserIdx();
            if (userId != userIdxByJwt) {
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            if (postMylistReq.getMylistName() == null) {
                return new BaseResponse<>(EMPTY_MYLIST_NAME);
            }
            Long myListId = userService.createMylist(userId, postMylistReq);
            return new BaseResponse<>(myListId);
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

    /**
     * 유저의 마이리스트 조회 API
     * [GET] /mylists/:userId/:profileUserId
     *
     * @return BaseResponse<List<GetUserMylistsRes>>
     */
    @ResponseBody
    @GetMapping("/mylists/{userId}/{profileUserId}")
    public BaseResponse<List<GetUserMylistsRes>> getUserMylists(@PathVariable("userId") Long userId, @PathVariable("profileUserId") Long profileUserId) {
        try {
            Long userIdxByJwt = jwtService.getUserIdx();
            if (userId != userIdxByJwt) {
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            List<GetUserMylistsRes> getUserMylistsRes = userProvider.getUserMylists(userId, profileUserId);
            return new BaseResponse<>(getUserMylistsRes);
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

    /**
     * 마이리스트 상세 조회 API
     * [GET] /mylists/detail/:userId/:mylistId
     *
     * @return BaseResponse<GetMylistDetailRes>
     */
    @ResponseBody
    @GetMapping("/mylists/detail/{userId}/{mylistId}")
    public BaseResponse<GetMylistRes> getMylistDetail(@PathVariable("userId") Long userId, @PathVariable("mylistId") Long mylistId) {
        try {
            Long userIdxByJwt = jwtService.getUserIdx();
            if (userId != userIdxByJwt) {
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            GetMylistRes getMylistRes = userProvider.getMylist(userId, mylistId);
            return new BaseResponse<>(getMylistRes);
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

    /**
     * 유저의 북마크 조회 API
     * [GET] /bookmarks/:userId/:profileUserId
     *
     * @return BaseResponse<List<GetUserBookmarksRes>>
     */
    @ResponseBody
    @GetMapping("/bookmarks/{userId}/{profileUserId}")
    public BaseResponse<List<GetUserBookmarksRes>> getUserBookmarks(@PathVariable("userId") Long userId, @PathVariable("profileUserId") Long profileUserId) {
        try {
            Long userIdxByJwt = jwtService.getUserIdx();
            if (userId != userIdxByJwt) {
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            List<GetUserBookmarksRes> getUserBookmarksRes = userProvider.getUserBookmarks(userId, profileUserId);
            return new BaseResponse<>(getUserBookmarksRes);
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

    // 문자 인증 (CoolSMS) 우디

    @ResponseBody
    @GetMapping("/sendMessage")
    public BaseResponse<String> sendMessage(@RequestParam(value="to") String to) throws CoolsmsException {
        String result =  userService.sendRandomNumber(to);
        return new BaseResponse<>(result);
    }
}
