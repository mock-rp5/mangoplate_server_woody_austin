package com.example.demo.src.store;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.news.model.GetNewsRes;
import com.example.demo.src.store.model.*;
import com.example.demo.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static com.example.demo.config.BaseResponseStatus.*;

@RestController
@RequestMapping("/stores")
public class StoreController {
    @Autowired
    private final StoreProvider storeProvider;
    @Autowired
    private final StoreService storeService;
    @Autowired
    private final JwtService jwtService;

    public StoreController(StoreProvider storeProvider, StoreService storeService, JwtService jwtService) {
        this.storeProvider = storeProvider;
        this.storeService = storeService;
        this.jwtService = jwtService;
    }

    /**
     * 지역별 가게 리스트 조회 API
     * [GET] /stores/{userid}?region=지역명,지역명&filter=1&page=1
     * * @return BaseResponse<GetStoreListRes>
     */
    @ResponseBody
    @GetMapping("/{userId}")
    public BaseResponse<List<GetStoreMainRes>> getStoresList(@PathVariable("userId") Long userId, @RequestParam List<String> region,@RequestParam(defaultValue = "1") int filter){
        try {
            String filtering="";
            if(filter==1){
                filtering="rating";
            }
            else if(filter==2){
                filtering="distance";
            }
            else if(filter==3){
                filtering="reviewCount";
            }
            else{
                return new BaseResponse<>(NON_EXIST_FILTER);
            }
            System.out.println(filtering);
            Long userIdxByJwt = jwtService.getUserIdx();
            if (userId != userIdxByJwt) {
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            GetStoreListReq getStoreListReq = new GetStoreListReq(userId, region,filtering);
            List<GetStoreMainRes> getStoreListRes=storeProvider.getStoreList(getStoreListReq);

            return new BaseResponse<>(getStoreListRes);

            }catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

    /**
     * 가게 검색 API
     * [GET] /stores/{userid}?keyword=가게이름?region=지역명,지역명,
     * * @return BaseResponse<GetStoreListRes>
     */
    @ResponseBody
    @GetMapping("/search/{userId}")
    public BaseResponse<List<GetStoreListRes>> getStoreListByKeyWord(@PathVariable("userId") Long userId,@RequestParam("keyword") String keyword,@RequestParam(required = false,defaultValue = "all") List<String> region)
      {
        try {
            Long userIdxByJwt = jwtService.getUserIdx();
            if (userId != userIdxByJwt) {
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            GetStoreListByKeyWordReq getStoreListByKeyWord=new GetStoreListByKeyWordReq(userId,region,keyword);
            List<GetStoreListRes> getStoreListRes=storeProvider.getStoreListByKeyWord(getStoreListByKeyWord);

            return new BaseResponse<>(getStoreListRes);

        }catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }


    /**
     * 특정 가게 조회 API
     * [GET] /stores/:storeId/:userId
     * * @return BaseResponse<GetStoreR es>
     */
    @ResponseBody
    @GetMapping("/detail/{storeId}/{userId}")
    public BaseResponse<GetStoreRes> getStore(@PathVariable("storeId") Long storeId, @PathVariable("userId") Long userId) {
        try{
            Long userIdxByJwt = jwtService.getUserIdx();
            if (userId != userIdxByJwt) {
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            GetStoreRes getStoreRes = storeProvider.getStore(storeId, userId);
            return new BaseResponse<>(getStoreRes);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 특정 가게 메뉴 상세 조회 API
     * [GET] /stores/menu/:storeId
     * * @return BaseResponse<GetMenuRes>
     */
    @ResponseBody
    @GetMapping("/menu/{storeId}")
    public BaseResponse<GetMenuRes> getMenu(@PathVariable("storeId") Long storeId) {
        try{
            GetMenuRes getMenuRes = storeProvider.getMenu(storeId);
            return new BaseResponse<>(getMenuRes);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 메인화면 거리별 가게 리스트 조회 API
     * [GET] /stores/distance/:userId?distance=500&page=1
     * * @return BaseResponse<GetStoreListRes>
     */
    @ResponseBody
    @GetMapping("/distance/{userId}")
    public BaseResponse<List<GetStoreListRes>> getStoreListByDistance(@PathVariable("userId") Long userId, @RequestParam int distance){
        try {
            if(userId == null){
                return new BaseResponse<>(USERS_EMPTY_USER_ID);
            }
            Long userIdxByJwt = jwtService.getUserIdx();
            if (userId != userIdxByJwt) {
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            int[] POSSIBLE_DISTANCE = { 100, 300, 500, 1000, 3000 };
            if (IntStream.of(POSSIBLE_DISTANCE).anyMatch(x -> x == distance) == false)
                return new BaseResponse<>(DISTANCE_VALUE_WRONG);

            List<GetStoreListRes> getStoreListRes=storeProvider.getStoreListByDistance(userId, distance);
            return new BaseResponse<>(getStoreListRes);

        }catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

    /**
     * 지역별 + 음식 카테고리별 가게 리스트 조회 API
     * [GET] /stores/foodCategory/{userid}?category=음식종류&region=지역명,지역명,
     * * @return BaseResponse<GetStoreListRes>
     */
    @ResponseBody
    @GetMapping("/foodCategory/{userId}")
    public BaseResponse<List<GetStoreListRes>> getStoresListByFood(@PathVariable("userId") Long userId, @RequestParam List<String> category, @RequestParam List<String> region){
        try {
            Long userIdxByJwt = jwtService.getUserIdx();
            if (userId != userIdxByJwt) {
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            GetStoreListByFoodReq getStoreListByFoodReq = new GetStoreListByFoodReq(userId, category, region);
            List<GetStoreListRes> getStoreListRes=storeProvider.getStoreListByFood(getStoreListByFoodReq);

            return new BaseResponse<>(getStoreListRes);

        }catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

    /**
     * 지역별 + 주차가능 가게 리스트 조회 API
     * [GET] /stores/parking/{userid}?region=지역명,지역명,
     * * @return BaseResponse<GetStoreListRes>
     */
    @ResponseBody
    @GetMapping("/parking/{userId}")
    public BaseResponse<List<GetStoreListRes>> getStoresListByParking(@PathVariable("userId") Long userId, @RequestParam List<String> region){
        try {
            Long userIdxByJwt = jwtService.getUserIdx();
            if (userId != userIdxByJwt) {
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            List<GetStoreListRes> getStoreListRes=storeProvider.getStoreListByParking(userId, region);

            return new BaseResponse<>(getStoreListRes);

        }catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

    /**
     * 가게별 리뷰 조회 API
     * [GET] /stores/reviews/:storeId/:userId
     * * @return BaseResponse<GetStoreReviewRes>
     */
    @ResponseBody
    @GetMapping("/reviews/{storeId}/{userId}")
    public BaseResponse<List<GetStoreReviewRes>> getStoreReviews(@PathVariable("storeId") Long storeId, @PathVariable("userId") Long userId, @RequestParam(defaultValue = "1") List<Integer> filter){
        try {
            Long userIdxByJwt = jwtService.getUserIdx();
            if (userId != userIdxByJwt) {
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            List<String> evaluation = new ArrayList<>(filter.size());
            for (int i = 0; i < filter.size(); i++) {
                if (filter.get(i) == 1) {
                    evaluation.add("맛있다!");
                } else if (filter.get(i) == 2) {
                    evaluation.add("괜찮다");
                } else if (filter.get(i) == 3){
                    evaluation.add("별로");
                } else {
                    return new BaseResponse<>(WRONG_FILTER_VALUE);
                }
            }
            List<GetStoreReviewRes> getStoreReviewRes = storeProvider.getStoreReviews(storeId,userId, evaluation);
            return new BaseResponse<>(getStoreReviewRes);
        }catch(BaseException e){
            return new BaseResponse<>(e.getStatus());
        }
    }

    /**
     * 가게 가고싶다 생성 API
     * [POST] /Stores/wishes/:storeId/:userId
     * * @return BaseResponse<String>
     */
    @ResponseBody
    @PostMapping("/wishes/{storeId}/{userId}")
    public BaseResponse<String> createWish(@PathVariable("storeId") Long storeId,@PathVariable("userId") Long userId){
        try {
            Long userIdxByJwt = jwtService.getUserIdx();
            if (userId != userIdxByJwt) {
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            String result="가고싶다 생성 성공";
            storeService.createWish(storeId,userId);
            return new BaseResponse<>(result);
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }

    }

    /**
     * 가게 가고싶다 삭제 API
     * [DELETE] /Stores/wishes/:storeId/:userId
     * * @return BaseResponse<String>
     */
    @ResponseBody
    @DeleteMapping("/wishes/{storeId}/{userId}")
    public BaseResponse<String> deleteWish(@PathVariable("storeId") Long storeId,@PathVariable("userId") Long userId){
        try {
            Long userIdxByJwt = jwtService.getUserIdx();
            if (userId != userIdxByJwt) {
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            String result="가고싶다 취소 성공";
            storeService.deleteWish(storeId,userId);
            return new BaseResponse<>(result);
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

    /**
     * 가게 마이리스트에 추가 API
     * [POST] /stores/mylists/:storeId/:mylistId/:userId
     * * @return BaseResponse<String>
     */
    @ResponseBody
    @PostMapping("/mylists/{storeId}/{mylistId}/{userId}")
    public BaseResponse<String> addStoreToMylist(@PathVariable("storeId") Long storeId,@PathVariable("mylistId") Long mylistId, @PathVariable("userId") Long userId){
        try {
            Long userIdxByJwt = jwtService.getUserIdx();
            if (userId != userIdxByJwt) {
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            String result="마이리스트에 가게 추가 성공";
            storeService.addStoreToMylist(storeId,mylistId,userId);
            return new BaseResponse<>(result);
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }

    }

}
