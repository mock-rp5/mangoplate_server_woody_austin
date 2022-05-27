package com.example.demo.src.visited;

import com.example.demo.config.BaseException;
import com.example.demo.src.review.ReviewDao;
import com.example.demo.src.review.ReviewProvider;
import com.example.demo.src.review.model.PostReviewListReq;
import com.example.demo.src.store.StoreDao;
import com.example.demo.src.visited.model.PostVisitedReq;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;

import static com.example.demo.config.BaseResponseStatus.*;

@Service
public class VisitedService {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final VisitedDao visitedDao;
    private final VisitedProvider visitedProvider;
    private final JwtService jwtService;
    private final StoreDao storeDao;


    @Autowired
    public VisitedService(VisitedDao visitedDao, VisitedProvider visitedProvider,StoreDao storeDao, JwtService jwtService) {
        this.visitedDao = visitedDao;
        this.visitedProvider = visitedProvider;
        this.storeDao = storeDao;
        this.jwtService = jwtService;
    }

    @Transactional(rollbackFor = BaseException.class)
    public void createVisited(PostVisitedReq postVisitedReq) throws BaseException {
        if(storeDao.checkStoreId(postVisitedReq.getStoreId()) == 0){
            throw new BaseException(NON_EXIST_STORE);
        }
        // 하루에 한번만 가봤어요 생성 가능
       if (visitedDao.checkDate(postVisitedReq.getStoreId(), postVisitedReq.getUserId()) == 1) {
           throw new BaseException(ALREADY_VISITED_TODAY);
       }
        // 가고싶다 했던 가게일 경우 가고싶다 삭제
        try {
            if (storeDao.checkWish(postVisitedReq.getStoreId(), postVisitedReq.getUserId()) == 1) {
                storeDao.deleteWish(postVisitedReq.getStoreId(), postVisitedReq.getUserId());
            }
        } catch (Exception exception) {
            throw new BaseException(DELETE_WISH_FAIL);
        }
        try {
            visitedDao.createVisited(postVisitedReq);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }
}
