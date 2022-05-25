package com.example.demo.src.review;

import com.example.demo.config.BaseException;
import com.example.demo.src.review.model.PostReviewImgReq;
import com.example.demo.src.review.model.PostReviewListReq;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;

import static com.example.demo.config.BaseResponseStatus.DATABASE_ERROR;

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
}
