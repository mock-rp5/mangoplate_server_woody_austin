package com.example.demo.src.review;

import com.example.demo.config.BaseException;
import com.example.demo.src.review.model.PostCommentReq;
import com.example.demo.src.review.model.PostReviewImgReq;
import com.example.demo.src.review.model.PostReviewListReq;
import com.example.demo.src.review.model.PostReviewReq;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;

import static com.example.demo.config.BaseResponseStatus.*;

@Service
public class ReviewService {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final ReviewDao reviewDao;
    private final ReviewProvider reviewProvider;
    private final JwtService jwtService;


    @Autowired
    public ReviewService(ReviewDao reviewDao, ReviewProvider reviewProvider, JwtService jwtService) {
        this.reviewDao = reviewDao;
        this.reviewProvider = reviewProvider;
        this.jwtService = jwtService;
    }

    @Transactional(rollbackFor = SQLException.class)
    public Long createReview(PostReviewListReq postReviewListReq) throws BaseException {
        try {
            Long lastInsertId = reviewDao.createReview(postReviewListReq);
            return lastInsertId;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    @Transactional(rollbackFor = SQLException.class)
    public void createReviewImg(PostReviewImgReq postReviewImgReq) throws BaseException {
        try {
            reviewDao.createReviewImg(postReviewImgReq);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }

    }
    public void createReviewLike(Long reviewId, Long userId) throws BaseException{
        if(reviewProvider.checkReviewLike(reviewId,userId)==1){
            throw new BaseException(EXISTS_REVIEW_LIKE);
        }
        if(reviewProvider.checkReviewExists(reviewId)==0){
            throw new BaseException(NON_EXIST_REVIEW);
        }try {
            reviewDao.createReviewLike(reviewId, userId);
        }catch (Exception e){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public void deleteReviewLike(Long reviewId, Long userId) throws BaseException{
        if(reviewProvider.checkReviewLike(reviewId,userId)==0){
            throw new BaseException(NON_EXIST_REViEW_LIKE);
        }
        if(reviewProvider.checkReviewExists(reviewId)==0){
            throw new BaseException(NON_EXIST_REVIEW);
        }try {
            reviewDao.deleteReviewLike(reviewId, userId);
        }catch (Exception e){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public void createReviewComment(Long reviewId, Long userId, PostCommentReq postCommentReq) throws BaseException {
        if(reviewProvider.checkReviewExists(reviewId)==0){
            throw new BaseException(NON_EXIST_REVIEW);
        }try {
            reviewDao.createReviewComment(reviewId, userId,postCommentReq);
        }catch (Exception e){
            throw new BaseException(DATABASE_ERROR);
        }
    }
}
