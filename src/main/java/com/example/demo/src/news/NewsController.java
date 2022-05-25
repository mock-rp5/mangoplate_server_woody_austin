package com.example.demo.src.news;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.news.model.GetNewsByFollowingReq;
import com.example.demo.src.news.model.GetNewsDetailRes;
import com.example.demo.src.news.model.GetNewsRes;
import com.example.demo.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/news")
public class NewsController {
    @Autowired
    private final NewsProvider newsProvider;
    @Autowired
    private final JwtService jwtService;

    public NewsController(NewsProvider newsProvider, JwtService jwtService) {
        this.newsProvider=newsProvider;
        this.jwtService=jwtService;
    }

    @ResponseBody
    @GetMapping("")
    public BaseResponse<List<GetNewsRes>> getNews(@RequestParam(defaultValue = "1") List<Integer> filter,@RequestParam(defaultValue = "1") int page){
        try {
            List<String> evaluation = new ArrayList<>(filter.size());
            for (int i = 0; i < filter.size(); i++) {
                if (filter.get(i) == 1) {
                    evaluation.add("맛있다!");
                } else if (filter.get(i) == 2) {
                    evaluation.add("괜찮다");
                } else {
                    evaluation.add("별로");
                }
            }

            List<GetNewsRes> getNewsRes = newsProvider.getNews(evaluation,page);
            System.out.println(getNewsRes);
            return new BaseResponse<>(getNewsRes);
        }catch(BaseException e){
            return new BaseResponse<>(e.getStatus());
        }
    }

    @ResponseBody
    @GetMapping("/following/{userid}")
    public BaseResponse<List<GetNewsRes>> getNewsByFollowing(@PathVariable("userid") Long userid,
                                                             @RequestParam(defaultValue = "1") List<Integer> filter,@RequestParam(defaultValue = "1") int page){
        try {
            List<String> evaluation = new ArrayList<>(filter.size());
            for (int i = 0; i < filter.size(); i++) {
                if (filter.get(i) == 1) {
                    evaluation.add("맛있다!");
                } else if (filter.get(i) == 2) {
                    evaluation.add("괜찮다");
                } else {
                    evaluation.add("별로");
                }
            }
            GetNewsByFollowingReq getNewsByFollowingReq = new GetNewsByFollowingReq(userid,evaluation,page);
            List<GetNewsRes> getNewsRes=newsProvider.getNewsByFollowing(getNewsByFollowingReq);
            System.out.println(getNewsRes);
            return new BaseResponse<>(getNewsRes);
        }catch(BaseException e){
                return new BaseResponse<>(e.getStatus());
            }

    }



}
