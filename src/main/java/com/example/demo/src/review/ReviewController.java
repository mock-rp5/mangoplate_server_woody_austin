package com.example.demo.src.review;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.review.model.*;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.example.demo.config.BaseResponseStatus.INVALID_USER_JWT;

@RestController
@RequestMapping("/review")
public class ReviewController {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private final ReviewProvider reviewProvider;
    @Autowired
    private final ReviewService reviewService;
    @Autowired
    private final JwtService jwtService;

    public ReviewController(ReviewProvider reviewProvider, ReviewService reviewService, JwtService jwtService) {
        this.reviewProvider = reviewProvider;
        this.reviewService = reviewService;
        this.jwtService = jwtService;
    }

    /**
     * 리뷰 상세 조회 API
     * [GET] /review/:reviewId
     * * @return BaseResponse<GetReviewRes>
     */
    @ResponseBody
    @GetMapping("/{reviewId}")
    public BaseResponse<GetReviewRes> getReview(@PathVariable("reviewId") Long reviewId) {
        try{
            GetReviewRes getReviewRes = reviewProvider.getReview(reviewId);
            return new BaseResponse<>(getReviewRes);
        } catch(BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }



    @ResponseBody
    @GetMapping("/stores_review")
    public BaseResponse<List<GetReviewStoreRes>> getReviewStore(@RequestParam String keyWord)throws BaseException{
        try {
            List<GetReviewStoreRes> getReviewStoreRes = reviewProvider.getReviewStore(keyWord);
            return new BaseResponse<>(getReviewStoreRes);
        }catch (BaseException e){
            return new BaseResponse<>(e.getStatus());
        }
    }
    /**
     * 리뷰 생성 API
     * [POST] /review/:userId
     * * @return BaseResponse<String>
     */
    @ResponseBody
    @PostMapping("/{userId}")
    public BaseResponse<String> createReview(@PathVariable("userId") Long userId, @RequestBody PostReviewReq postReviewReq) throws BaseException {
        try {
            Long userIdxByJwt = jwtService.getUserIdx();
            if (userId != userIdxByJwt) {
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            PostReviewListReq postReviewListReq = new PostReviewListReq(userId, postReviewReq.getStoreId(), postReviewReq.getReview(), postReviewReq.getEvaluation());
            Long lastInsertId = reviewService.createReview(postReviewListReq);
            for (ReviewImg reviewImg : postReviewReq.getReviewImg()) {
                PostReviewImgReq postReviewImgReq = new PostReviewImgReq(lastInsertId, reviewImg.getImgUrl());
                reviewService.createReviewImg(postReviewImgReq);
            }
            String result = "리뷰 등록 성공";
            return new BaseResponse<>(result);
        }catch (BaseException e){
            return new BaseResponse<>(e.getStatus());
        }
        }
}