package com.example.demo.src.store;

import com.example.demo.config.BaseException;
import com.example.demo.src.store.model.GetStoreListReq;
import com.example.demo.src.store.model.GetStoreListRes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import com.example.demo.src.store.model.GetMenuRes;
import com.example.demo.src.store.model.GetStoreRes;

import javax.transaction.Transactional;

import static com.example.demo.config.BaseResponseStatus.*;


@Service
public class StoreProvider {

    private final StoreDao storeDao;

    @Autowired
    public StoreProvider(StoreDao storeDao) {
        this.storeDao=storeDao;
    }

    public List<GetStoreListRes> getStoreList(GetStoreListReq getStoreListReq) throws BaseException {
        if(getStoreListReq.getRegion()==null){
            throw new BaseException(NO_REGION_VALUE);
        }
        try {
            List<GetStoreListRes> getStoreListRes=storeDao.getStoreList(getStoreListReq);
            return getStoreListRes;
        }catch (Exception e){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    @Transactional (rollbackOn = BaseException.class)
    public GetStoreRes getStore(int storeId) throws BaseException {
        // 조회수 늘리기
        try {
            storeDao.increaseViewCount(storeId);
        } catch (Exception exception) {
            throw new BaseException(UPDATE_VIEW_COUNT_FAIL);
        }
        // 가게 정보 GET
        try {
            GetStoreRes getStoreRes = storeDao.getStore(storeId);
            if(getStoreRes.getStoreName() == null)
                throw new BaseException(NON_EXIST_STORE);
            else
                return getStoreRes;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public GetMenuRes getMenu(int storeId) throws BaseException {
        try {
            GetMenuRes getMenuRes = storeDao.getMenu(storeId);
            return getMenuRes;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

}
