package com.example.demo.config;

import lombok.Getter;

/**
 * 에러 코드 관리
 */
@Getter
public enum BaseResponseStatus {
    /**
     * 1000 : 요청 성공
     */
    SUCCESS(true, 1000, "요청에 성공하였습니다."),


    /**
     * 2000 : Request 오류
     */
    // Common
    REQUEST_ERROR(false, 2000, "입력값을 확인해주세요."),
    EMPTY_JWT(false, 2001, "JWT를 입력해주세요."),
    INVALID_JWT(false, 2002, "유효하지 않은 JWT입니다."),
    INVALID_USER_JWT(false,2003,"권한이 없는 유저의 접근입니다."),

    // users
    USERS_EMPTY_USER_ID(false, 2010, "유저 아이디 값을 확인해주세요."),


    // [POST] /users
    POST_USERS_EMPTY_EMAIL(false, 2015, "이메일을 입력해주세요."),
    POST_USERS_INVALID_EMAIL(false, 2016, "이메일 형식을 확인해주세요."),
    POST_USERS_EXISTS_EMAIL(false,2017,"중복된 이메일입니다."),

    POST_USERS_EMPTY_NAME(false, 2018, "이름을 입력해주세요."),
    POST_USERS_EMPTY_PASSWORD(false, 2019, "비밀번호를 입력해주세요."),
    POST_USERS_EMPTY_PHONENUMBER(false, 2020, "전화번호를 입력해주세요."),

    // [GET] /stores
    DISTANCE_VALUE_WRONG(false, 2030, "필터링할 거리값을 알맞게 입력해주세요."),


    // [PATCH] /users
    FAIL_TO_UPDATE_LOCATION(false, 2040, "위치정보 업데이트에 실패하였습니다."),
    WRONG_LATITUDE_VALUE(false, 2041, "위도값을 알맞게 입력해주세요."),
    WRONG_LONGITUDE_VALUE(false, 2042, "경도값을 알맞게 입력해주세요."),


    /**
     * 3000 : Response 오류
     */
    // Common
    RESPONSE_ERROR(false, 3000, "값을 불러오는데 실패하였습니다."),

    // [POST] /users
    DUPLICATED_EMAIL(false, 3013, "중복된 이메일입니다."),
    FAILED_TO_LOGIN(false,3014,"비밀번호가 틀렸습니다."),
    NON_EXIST_EMAIL(false, 3015, "없는 이메일 입니다."),
    EXIST_FOLLOW(false,3016,"이미 해당 유저를 팔로우 하고 있습니다."),
    NON_EXIST_USER(false,3017,"해당 유저가 존재하지 않습니다."),

     // [GET] /stores
    NO_REGION_VALUE(false,3020,"지역을 선택해 주세요"),
    NO_CATEGORY_VALUE(false,3021,"음식종류를 선택해 주세요"),
    WRONG_FILTER_VALUE(false,3022,"평가 필터를 1,2,3중에 입력해 주세요"),
    // [POST] /stores
    UPDATE_VIEW_COUNT_FAIL(false, 3050, "조회수 업데이트에 실패하였습니다."),

    // [GET] /stores
    NON_EXIST_STORE(false, 3070, "존재하지 않는 가게 아이디 입니다."),
    EXISTS_WISH(false,3071,"이미 가고싶다를 생성한 가게입니다."),
    NON_EXISTS_WISH(false,3071,"가고싶다 한 적 없는 가게입니다."),
    EXISTS_VISITED(false,3072,"이미 방문한 가게입니다."),

    // [GET] /review
    NON_EXIST_REVIEW(false, 3080, "존재하지 않는 리뷰 아이디 입니다."),
    EXISTS_REVIEW_LIKE(false,3001,"이미 존재하는 리뷰 좋아요 입니다."),
    NON_EXIST_REViEW_LIKE(false,3002,"존재 하지 않는 리뷰 좋아요 입니다."),


    NON_EXIST_COMMENT(false,3003,"존재하지 않는 댓글입니다"),

    //[DELETE] /comment

    INVALID_USER(false,3004,"권한이 없는 유저의 접근입니다."),



    /**
     * 4000 : Database, Server 오류
     */
    DATABASE_ERROR(false, 4000, "데이터베이스 연결에 실패하였습니다."),
    SERVER_ERROR(false, 4001, "서버와의 연결에 실패하였습니다."),

    //[PATCH] /users/{userIdx}
    MODIFY_FAIL_USERNAME(false,4014,"유저네임 수정 실패"),

    PASSWORD_ENCRYPTION_ERROR(false, 4011, "비밀번호 암호화에 실패하였습니다."),
    PASSWORD_DECRYPTION_ERROR(false, 4012, "비밀번호 복호화에 실패하였습니다.");


    // 5000 : 필요시 만들어서 쓰세요
    // 6000 : 필요시 만들어서 쓰세요


    private final boolean isSuccess;
    private final int code;
    private final String message;

    private BaseResponseStatus(boolean isSuccess, int code, String message) {
        this.isSuccess = isSuccess;
        this.code = code;
        this.message = message;
    }
}
