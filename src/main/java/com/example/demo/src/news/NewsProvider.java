package com.example.demo.src.news;

import com.example.demo.config.BaseException;
import com.example.demo.src.news.model.GetNewsByFollowingReq;
import com.example.demo.src.news.model.GetNewsImgRes;
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

    public List<GetNewsRes> getNews(Long userId, List<String> evaluation, int page) throws BaseException {

        try {
            List<GetNewsRes> getNewsRes = newsDao.getNews(userId,evaluation,page);
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

    public List<GetNewsRes> getNewsHolic(Long userId, List<String> evaluation, int page)throws BaseException {
        try{
            List<GetNewsRes> getNewsRes = newsDao.getNewsHolic(userId, evaluation,page);
            return getNewsRes;
        }catch (Exception e){
            throw new BaseException(DATABASE_ERROR);
        }
    }


}
