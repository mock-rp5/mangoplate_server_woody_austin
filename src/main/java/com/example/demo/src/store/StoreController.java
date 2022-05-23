package com.example.demo.src.store;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.store.model.GetStoreListReq;
import com.example.demo.src.store.model.GetStoreListRes;
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

    public StoreController(StoreProvider storeProvider, StoreService storeService,JwtService jwtService){
        this.storeProvider = storeProvider;
        this.storeService = storeService;
        this.jwtService = jwtService;
    }

    @ResponseBody
    @GetMapping("/{userId}")
    public BaseResponse<List<GetStoreListRes>> getStoresList(@PathVariable("userId") Long userId, @RequestParam(value="region") String region){

        try{
            Long userIdxByJwt=jwtService.getUserIdx();
            if(userId != userIdxByJwt){
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            GetStoreListReq getStoreListReq = new GetStoreListReq(userId,region);
            List<GetStoreListRes> getStoreListRes = storeProvider.getStoreList(getStoreListReq);
            return new BaseResponse<>(getStoreListRes);
        }catch (BaseException e)
        {
            return new BaseResponse<>(e.getStatus());
        }
    }
}
