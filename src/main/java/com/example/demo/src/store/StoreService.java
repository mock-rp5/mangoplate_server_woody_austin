package com.example.demo.src.store;

import com.example.demo.config.BaseException;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

import static com.example.demo.config.BaseResponseStatus.*;

@Service
public class StoreService {

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final StoreDao storeDao;
    private final StoreProvider storeProvider;
    private final JwtService jwtService;


    @Autowired
    public StoreService(StoreDao storeDao, StoreProvider storeProvider, JwtService jwtService) {
        this.storeDao = storeDao;
        this.storeProvider = storeProvider;
        this.jwtService = jwtService;
    }

    public void createWish(Long storeId, Long userId) throws BaseException {
        if(storeDao.checkStoreId(storeId) == 0){
            throw new BaseException(NON_EXIST_STORE);
        }
        if(storeDao.checkWish(storeId,userId)==1){
            throw new BaseException(EXISTS_WISH);
        }
        if(storeDao.checkVisited(storeId,userId)==1){
            throw new BaseException(EXISTS_VISITED);
        }
        try {
            storeDao.createWish(storeId, userId);
        }catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public void deleteWish(Long storeId, Long userId) throws BaseException {
        if(storeDao.checkStoreId(storeId) == 0){
            throw new BaseException(NON_EXIST_STORE);
        }
        if(storeDao.checkWish(storeId,userId)==0){
            throw new BaseException(NON_EXISTS_WISH);
        }
        if(storeDao.checkVisited(storeId,userId)==1){
            throw new BaseException(EXISTS_VISITED);
        }
        try {
            storeDao.deleteWish(storeId, userId);
        }catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public void addStoreToMylist(Long storeId, Long mylistId, Long userId) throws BaseException {
        if(storeDao.checkStoreId(storeId) == 0){
            throw new BaseException(NON_EXIST_STORE);
        }
        if(storeDao.checkMylistId(mylistId) == 0){
            throw new BaseException(NON_EXIST_MYLIST);
        }
        if(storeDao.checkMylistUser(mylistId) != userId){
            throw new BaseException(WRONG_MYLIST_USER);
        }
        try {
            storeDao.addStoreToMylist(storeId, mylistId);
        }catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

}
