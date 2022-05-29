package com.example.demo.src.store;

import com.example.demo.config.BaseException;
import com.example.demo.src.news.model.GetNewsRes;
import com.example.demo.src.store.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import javax.transaction.Transactional;

import static com.example.demo.config.BaseResponseStatus.*;


@Service
public class StoreProvider {

    private final StoreDao storeDao;

    @Autowired
    public StoreProvider(StoreDao storeDao) {
        this.storeDao=storeDao;
    }

    public List<GetStoreMainRes> getStoreList(GetStoreListReq getStoreListReq) throws BaseException {
        if(getStoreListReq.getRegion().size()==0){
            throw new BaseException(NO_REGION_VALUE);
        }

            List<GetStoreMainRes> getStoreListRes=storeDao.getStoreList(getStoreListReq);
            return getStoreListRes;

    }

    @Transactional (rollbackOn = BaseException.class)
    public GetStoreRes getStore(Long storeId, Long userId ) throws BaseException {
        // 조회수 늘리기
        try {
            storeDao.increaseViewCount(storeId);
        } catch (Exception exception) {
            throw new BaseException(UPDATE_VIEW_COUNT_FAIL);
        }
        // 가게 정보 GET
        try {
            GetStoreRes getStoreRes = storeDao.getStore(storeId, userId);
            if(getStoreRes.getStoreName() == null)
                throw new BaseException(NON_EXIST_STORE);
            else
                return getStoreRes;
        } catch (Exception exception) {
            throw new BaseException(NON_EXIST_STORE);
        }
    }

    public GetMenuRes getMenu(Long storeId) throws BaseException {
        if(storeDao.checkStoreId(storeId) == 0){
            throw new BaseException(NON_EXIST_STORE);
        }
        try {
            GetMenuRes getMenuRes = storeDao.getMenu(storeId);
            return getMenuRes;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public List<GetStoreListRes> getStoreListByKeyWord(GetStoreListByKeyWordReq getStoreListByKeyWord)throws BaseException {
        try {
            List<GetStoreListRes> getStoreListRes=storeDao.getStoreListByKeyWord(getStoreListByKeyWord);
            return getStoreListRes;

        }catch(Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public List<GetStoreListRes> getStoreListByFood(GetStoreListByFoodReq getStoreListByFoodReq)throws BaseException {
        if(getStoreListByFoodReq.getCategory().size()==0){
            throw new BaseException(NO_REGION_VALUE);
        }
        if(getStoreListByFoodReq.getRegion().size()==0){
            throw new BaseException(NO_CATEGORY_VALUE);
        }
        try {
            List<GetStoreListRes> getStoreListRes=storeDao.getStoreListByFood(getStoreListByFoodReq);
            return getStoreListRes;

        }catch(Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public List<GetStoreListRes> getStoreListByParking(Long userId, List<String> region)throws BaseException{
        if(region.size()==0){
            throw new BaseException(NO_REGION_VALUE);
        }
        try {
            List<GetStoreListRes> getStoreListRes=storeDao.getStoreListByParking(userId, region);
            return getStoreListRes;

        }catch(Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public List<GetStoreListRes> getStoreListByDistance(Long userId, int distance) throws BaseException {
        try {
            List<GetStoreListRes> getStoreListRes=storeDao.getStoreListByDistance(userId, distance);
            return getStoreListRes;
        }catch (Exception e){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public List<GetStoreReviewRes> getStoreReviews(Long storeId, Long userId, List<String> evaluation) throws BaseException {
        if(storeDao.checkStoreId(storeId) == 0){
            throw new BaseException(NON_EXIST_STORE);
        }
        try {
            List<GetStoreReviewRes> getStoreReviewRes = storeDao.getStoreReviews(storeId, userId, evaluation);
            return getStoreReviewRes;
        } catch(Exception e){
            throw new BaseException(DATABASE_ERROR);
        }
    }
}
