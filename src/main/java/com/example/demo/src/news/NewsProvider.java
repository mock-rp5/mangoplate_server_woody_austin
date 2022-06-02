package com.example.demo.src.news;

import com.example.demo.config.BaseException;
import com.example.demo.src.news.model.GetNewsByFollowingReq;
import com.example.demo.src.news.model.GetNewsMainRes;
import com.example.demo.src.news.model.GetNewsRes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.example.demo.config.BaseResponseStatus.DATABASE_ERROR;

@Service
public class NewsProvider {

    private final NewsDao newsDao;

    @Autowired
    public NewsProvider(NewsDao newsDao){
        this.newsDao=newsDao;
    }

    public List<GetNewsMainRes> getNews(Long userId, List<String> evaluation, List<String> region) throws BaseException {

        try {
            List<GetNewsMainRes> getNewsRes = newsDao.getNews(userId,evaluation,region);
            return getNewsRes;
        } catch(Exception e){
            throw new BaseException(DATABASE_ERROR);
    }
    }

    public List<GetNewsRes> getNewsByFollowing(GetNewsByFollowingReq getNewsByFollowingReq) throws BaseException {
        try{
            List<GetNewsRes> getNewsRes =newsDao.getNewsByFollowing(getNewsByFollowingReq);
            return getNewsRes;
        }catch(Exception e){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public List<GetNewsRes> getNewsHolic(Long userId, List<String> evaluation, List<String> region)throws BaseException {
        try{
            List<GetNewsRes> getNewsRes = newsDao.getNewsHolic(userId, evaluation,region);
            return getNewsRes;
        }catch (Exception e){
            throw new BaseException(DATABASE_ERROR);
        }
    }


}
