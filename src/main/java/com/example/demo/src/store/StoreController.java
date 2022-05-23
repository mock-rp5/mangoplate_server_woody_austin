package com.example.demo.src.store;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.store.model.GetStoreListReq;
import com.example.demo.src.store.model.GetStoreListRes;
import com.example.demo.src.store.model.GetMenuRes;
import com.example.demo.src.store.model.GetStoreRes;
import com.example.demo.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
            String regionName="";
            for(int i=0;i<region.size();i++){
                regionName +="'"+region.get(i)+"'"+",";
            }
            regionName = regionName.replaceAll(",$","");
            System.out.println(regionName);

            GetStoreListReq getStoreListReq = new GetStoreListReq(userId, region,page);
            List<GetStoreListRes> getStoreListRes=storeProvider.getStoreList(getStoreListReq);

            return new BaseResponse<>(getStoreListRes);

        }catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }


    /**
     * 특정 가게 조회 API
     * [GET] /stores/:storeId
     * * @return BaseResponse<GetStoreRes>
     */
    @ResponseBody
    @GetMapping("/detail/{storeId}")
    public BaseResponse<GetStoreRes> getStore(@PathVariable("storeId") int storeId) {
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
    public BaseResponse<GetMenuRes> getMenu(@PathVariable("storeId") int storeId) {
        try{
            GetMenuRes getMenuRes = storeProvider.getMenu(storeId);
            return new BaseResponse<>(getMenuRes);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

}
