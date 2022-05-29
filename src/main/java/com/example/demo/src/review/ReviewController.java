package com.example.demo.src.review;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.review.model.*;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.example.demo.config.BaseResponseStatus.INVALID_USER_JWT;

@RestController
@RequestMapping("/review")
public class ReviewController {
    final Logger  logger = LoggerFactory.getLogger(this.getClass());

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
     * [GET] /review/:reviewId/:userId
     * * @return BaseResponse<GetReviewRes>
     */
    @ResponseBody
    @GetMapping("/{reviewId}/{userId}")
    public BaseResponse<GetReviewRes> getReview(@PathVariable("reviewId") Long reviewId, @PathVariable("userId") Long userId) {
        try{
            Long userIdxByJwt = jwtService.getUserIdx();
            if (userId != userIdxByJwt) {
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            GetReviewRes getReviewRes = reviewProvider.getReview(reviewId, userId);
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

    /**
     * 리뷰 삭제 API
     * [DELETE] /review/:reviewId/:userId
     * * @return BaseResponse<String>
     */
    @ResponseBody
    @DeleteMapping("/{reviewId}/{userId}")
    public BaseResponse<String> deleteReview(@PathVariable("reviewId") Long reviewId,@PathVariable("userId") Long userId){
        try {
            Long userIdxByJwt = jwtService.getUserIdx();
            if (userId != userIdxByJwt) {
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            String result="리뷰 삭제 성공";
            reviewService.deleteReview(reviewId,userId);
            return new BaseResponse<>(result);
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

    /**
     * 리뷰 좋아요 생성 API
     * [POST] /review/like/:reviewId/:userId
     * * @return BaseResponse<String>
     */

        @ResponseBody
        @PostMapping("/like/{reviewId}/{userId}")
        public BaseResponse<String> createReviewLike(@PathVariable("reviewId") Long reviewId,@PathVariable("userId") Long userId){
            try {
                Long userIdxByJwt = jwtService.getUserIdx();
                if (userId != userIdxByJwt) {
                    return new BaseResponse<>(INVALID_USER_JWT);
                }
                String result="좋아요 성공";
                reviewService.createReviewLike(reviewId,userId);
                return new BaseResponse<>(result);
            } catch (BaseException e) {
                return new BaseResponse<>(e.getStatus());
            }

        }

    /**
     * 리뷰 좋아요 삭제 API
     * [DELETE] /review/like/:reviewId/:userId
     * * @return BaseResponse<String>
     */
        @ResponseBody
        @DeleteMapping("/like/{reviewId}/{userId}")
        public BaseResponse<String> deleteReviewLike(@PathVariable("reviewId") Long reviewId,@PathVariable("userId") Long userId){
            try {
                Long userIdxByJwt = jwtService.getUserIdx();
                if (userId != userIdxByJwt) {
                    return new BaseResponse<>(INVALID_USER_JWT);
                }
                String result="좋아요 취소 성공";
                reviewService.deleteReviewLike(reviewId,userId);
                return new BaseResponse<>(result);
            } catch (BaseException e) {
                return new BaseResponse<>(e.getStatus());
            }
        }

    /**
     * 리뷰 댓글 생성 API
     * [POST] /comments/:reviewId/:userId
     * * @return BaseResponse<String>
     */
        @ResponseBody
        @PostMapping("/comments/{reviewId}/{userId}")
        public BaseResponse<String> createReviewComment(@PathVariable("reviewId") Long reviewId,@PathVariable("userId") Long userId,@RequestBody PostCommentReq postCommentReq){
            try {
                Long userIdxByJwt = jwtService.getUserIdx();
                if (userId != userIdxByJwt) {
                    return new BaseResponse<>(INVALID_USER_JWT);
                }
                String result="리뷰 댓글 달기 성공";
                reviewService.createReviewComment(reviewId,userId,postCommentReq);
                return new BaseResponse<>(result);
            } catch (BaseException e) {
                return new BaseResponse<>(e.getStatus());
            }
        }
    /**
     * 리뷰 댓글 삭제 API
     * [DELETE] /comments/:commentId/:userId
     * * @return BaseResponse<String>
     */
    @ResponseBody
    @DeleteMapping("/comments/{commentId}/{userId}")
    public BaseResponse<String> deleteReviewComment(@PathVariable("commentId") Long commentId,@PathVariable("userId") Long userId){
        try {
            Long userIdxByJwt = jwtService.getUserIdx();
            if (userId != userIdxByJwt) {
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            String result="리뷰 댓글 삭제 성공";
            reviewService.deleteReviewComment(commentId,userId);
            return new BaseResponse<>(result);
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

    /**
     * 리뷰 댓글 수정 API
     * [Patch] /comments/:commentId/:userId
     * * @return BaseResponse<String>
     */
    @ResponseBody
    @PatchMapping("/comments/{commentId}/{userId}")
    public BaseResponse<String> putReviewComment(@PathVariable("commentId") Long commentId,@PathVariable("userId") Long userId,@RequestBody PutCommentsReq putCommentsReq) {
        try {
            Long userIdxByJwt = jwtService.getUserIdx();
            if (userId != userIdxByJwt) {
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            String result="댓글 수정 성공";
            reviewService.putReviewComment(commentId, userId, putCommentsReq);
            return new BaseResponse<>(result);
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }
    /**
    * 리뷰 좋아요 한 사람 조회 API
    * [GET] /like/:reviewId/:userId
    * * @return BaseResponse<String>
    */
    @ResponseBody
    @GetMapping("/like/{reviewId}/{userId}")
    public BaseResponse<List<GetReviewLikeUserRes>> getReviewLikesUser(@PathVariable("reviewId") Long reviewId,@PathVariable("userId") Long userId){
        try {
            Long userIdxByJwt = jwtService.getUserIdx();
            if (userId != userIdxByJwt) {
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            List<GetReviewLikeUserRes> getReviewLikesUserRes=reviewProvider.getReviewLikesUser(reviewId,userId);
            return new BaseResponse<>(getReviewLikesUserRes);
        }catch(BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }

    }



}
