package com.example.demo.src.review;

import com.example.demo.config.BaseException;
import com.example.demo.src.review.model.GetReviewLikeUserRes;
import com.example.demo.src.review.model.GetReviewRes;
import com.example.demo.src.review.model.GetReviewStoreRes;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.example.demo.config.BaseResponseStatus.*;

@Service
public class ReviewProvider {

    private final ReviewDao reviewDao;
    private final JwtService jwtService;


    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public ReviewProvider(ReviewDao reviewDao, JwtService jwtService) {
        this.reviewDao = reviewDao;
        this.jwtService = jwtService;
    }

    public GetReviewRes getReview(Long reviewId, Long userId) throws BaseException {
        if(reviewDao.checkReviewId(reviewId) == 0){
            throw new BaseException(NON_EXIST_REVIEW);
        }
        try {
            GetReviewRes getReviewRes = reviewDao.getReview(reviewId, userId);
            return getReviewRes;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public List<GetReviewStoreRes> getReviewStore(String keyWord) throws BaseException{
        try {
            List<GetReviewStoreRes> getReviewStoreRes = reviewDao.getReviewStore(keyWord);
            return getReviewStoreRes;
    } catch (Exception exception) {
        throw new BaseException(DATABASE_ERROR);
    }
    }


    public int checkReviewLike(Long reviewId, Long userId) throws BaseException{
        try {
            return reviewDao.chckReviewLike(reviewId, userId);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
        }

    public int checkReviewExists(Long reviewId) throws BaseException{
        try {
            return reviewDao.checkReviewExists(reviewId);
        }catch(Exception e){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public int checkReviewCommentExists(Long commentId) throws BaseException{
        try {
        return reviewDao.checkReviewCommentExists(commentId);
        }catch(Exception e){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public List<GetReviewLikeUserRes> getReviewLikesUser(Long reviewId, Long userId) throws BaseException {
        List<GetReviewLikeUserRes> getReviewLikeUserRes=reviewDao.getReviewLikesUser(reviewId,userId);
        return getReviewLikeUserRes;
    }
}
