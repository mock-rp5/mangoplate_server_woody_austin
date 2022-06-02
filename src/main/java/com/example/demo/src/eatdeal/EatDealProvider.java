package com.example.demo.src.eatdeal;

import com.example.demo.config.BaseException;
import com.example.demo.src.eatdeal.model.GetEatDealDetailRes;
import com.example.demo.src.eatdeal.model.GetEatDealRes;
import com.example.demo.src.news.NewsDao;
import com.example.demo.src.store.model.GetStoreListReq;
import com.example.demo.src.store.model.GetStoreMainRes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.example.demo.config.BaseResponseStatus.*;

@Service
public class EatDealProvider {

    private final EatDealDao eatDealDao;

    @Autowired
    public EatDealProvider(EatDealDao eatDealDao) {
        this.eatDealDao = eatDealDao;
    }

    public List<GetEatDealRes> getEatDealList(Long userId, List<String> region) throws BaseException {
        if(region.size()==0){
            throw new BaseException(NO_REGION_VALUE);
        }
        try {
            List<GetEatDealRes> getEatDealRes = eatDealDao.getEatDealList(region);
            return getEatDealRes;
        } catch (Exception e) {
            throw new BaseException(DATABASE_ERROR);
        }

    }

    public GetEatDealDetailRes getEatDealDetail(Long eatDealId, Long userId) throws BaseException {
        if (eatDealDao.checkEatDealId(eatDealId) == 0)
            throw new BaseException(NON_EXIST_EAT_DEAL);
        try {
            GetEatDealDetailRes getEatDealDetailRes = eatDealDao.getEatDealDetail(eatDealId, userId);
            return getEatDealDetailRes;
        } catch (Exception e) {
            throw new BaseException(DATABASE_ERROR);
        }
    }
}
