package com.example.demo.src.visited;

import com.example.demo.config.BaseException;
import com.example.demo.src.review.ReviewDao;
import com.example.demo.src.review.model.GetReviewLikeUserRes;
import com.example.demo.src.store.StoreDao;
import com.example.demo.src.visited.model.GetVisitedLikeUserRes;
import com.example.demo.src.visited.model.GetVisitedRes;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.example.demo.config.BaseResponseStatus.*;

@Service
public class VisitedProvider {
    private final VisitedDao visitedDao;
    private final JwtService jwtService;


    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public VisitedProvider(VisitedDao visitedDao, JwtService jwtService) {
        this.visitedDao = visitedDao;
        this.jwtService = jwtService;
    }

    public GetVisitedRes getVisited(Long visitedId, Long userId) throws BaseException {
        if(visitedDao.checkVisitedId(visitedId) == 0){
            throw new BaseException(NON_EXIST_VISITED);
        }
        try {
            return visitedDao.getVisited(visitedId, userId);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public List<GetVisitedLikeUserRes> getVisitedLikesUser(Long visitedId, Long userId) throws BaseException {
        if(visitedDao.checkVisitedId(visitedId) == 0){
            throw new BaseException(NON_EXIST_VISITED);
        }
        try {
            List<GetVisitedLikeUserRes> getVisitedikeUserRes = visitedDao.getVisitedLikeUser(visitedId,userId);
            return getVisitedikeUserRes;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }

    }
}
