package com.example.demo.src.store;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.store.model.*;
import com.example.demo.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.IntStream;

import static com.example.demo.config.BaseResponseStatus.DISTANCE_VALUE_WRONG;
import static com.example.demo.config.BaseResponseStatus.INVALID_USER_JWT;

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

    @ResponseBody
    @GetMapping("/{userId}")
    public BaseResponse<List<GetStoreListRes>> getStoresList(@PathVariable("userId") Long userId, @RequestParam List<String> region,@RequestParam int page){
        try {
            Long userIdxByJwt = jwtService.getUserIdx();
            if (userId != userIdxByJwt) {
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            GetStoreListReq getStoreListReq = new GetStoreListReq(userId, region,page);
            List<GetStoreListRes> getStoreListRes=storeProvider.getStoreList(getStoreListReq);

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
    public BaseResponse<List<GetStoreListRes>> getStoreListByKeyWord(@PathVariable("userId") Long userId,@RequestParam("keyword") String keyword,@RequestParam List<String> region
            ,@RequestParam int page){
        try {
            Long userIdxByJwt = jwtService.getUserIdx();
            if (userId != userIdxByJwt) {
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            GetStoreListByKeyWordReq getStoreListByKeyWord=new GetStoreListByKeyWordReq(userId,region,page, keyword);
            List<GetStoreListRes> getStoreListRes=storeProvider.getStoreListByKeyWord(getStoreListByKeyWord);

            return new BaseResponse<>(getStoreListRes);

        }catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }


    /**
     * 특정 가게 조회 API
     * [GET] /stores/:storeId
     * * @return BaseResponse<GetStoreR es>
     */
    @ResponseBody
    @GetMapping("/detail/{storeId}")
    public BaseResponse<GetStoreRes> getStore(@PathVariable("storeId") Long storeId) {
        try{
            GetStoreRes getStoreRes = storeProvider.getStore(storeId);
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
    public BaseResponse<List<GetStoreListRes>> getStoreListByDistance(@PathVariable("userId") Long userId, @RequestParam int distance,@RequestParam int page){
        try {
            Long userIdxByJwt = jwtService.getUserIdx();
            if (userId != userIdxByJwt) {
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            int[] POSSIBLE_DISTANCE = { 100, 300, 500, 1000, 3000 };
            if (IntStream.of(POSSIBLE_DISTANCE).anyMatch(x -> x == distance) == false)
                return new BaseResponse<>(DISTANCE_VALUE_WRONG);

            List<GetStoreListRes> getStoreListRes=storeProvider.getStoreListByDistance(userId, distance, page);
            return new BaseResponse<>(getStoreListRes);

        }catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }




}
