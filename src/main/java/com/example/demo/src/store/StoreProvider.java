package com.example.demo.src.store;

import com.example.demo.config.BaseException;
import com.example.demo.src.store.model.GetStoreListReq;
import com.example.demo.src.store.model.GetStoreListRes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.example.demo.config.BaseResponseStatus.DATABASE_ERROR;
import static com.example.demo.config.BaseResponseStatus.NO_REGION_VALUE;

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
}
