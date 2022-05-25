package com.example.demo.src.review;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.review.model.GetReviewRes;
import com.example.demo.src.store.model.GetStoreRes;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
}