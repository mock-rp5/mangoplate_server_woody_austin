package com.example.demo.src.eatdeal;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.eatdeal.model.GetEatDealDetailRes;
import com.example.demo.src.eatdeal.model.GetEatDealRes;
import com.example.demo.src.store.model.GetStoreListReq;
import com.example.demo.src.store.model.GetStoreMainRes;
import com.example.demo.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.example.demo.config.BaseResponseStatus.INVALID_USER_JWT;


@RestController
@RequestMapping("/eat_deals")
public class EatDealController {
    @Autowired
    private final EatDealProvider eatDealProvider;
    @Autowired
    private final EatDealService eatDealService;
    @Autowired
    private final JwtService jwtService;

    public EatDealController(EatDealProvider eatDealProvider, EatDealService eatDealService, JwtService jwtService) {
        this.eatDealProvider = eatDealProvider;
        this.eatDealService = eatDealService;
        this.jwtService = jwtService;
    }

    /**
     * 잇딜 전체조회 API
     * [GET] /:userId?region=
     * * @return BaseResponse<List<GetEatDealRes>>
     */
    @ResponseBody
    @GetMapping("/{userId}")
    public BaseResponse<List<GetEatDealRes>> getEatDealList(@PathVariable("userId") Long userId, @RequestParam List<String> region) {
        try {
            Long userIdxByJwt = jwtService.getUserIdx();

            if (userId != userIdxByJwt) {
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            List<GetEatDealRes> getEatDealRes = eatDealProvider.getEatDealList(userId, region);

            return new BaseResponse<>(getEatDealRes);

        } catch (
                BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

    /**
     * 잇딜 상세조회 API
     * [GET] /:eatDealId/:userId
     * * @return BaseResponse<GetEatDealDetailRes>
     */
    @ResponseBody
    @GetMapping("/{eatDealId}/{userId}")
    public BaseResponse<GetEatDealDetailRes> getEatDealDetail(@PathVariable("eatDealId") Long eatDealId, @PathVariable("userId") Long userId) {
        try {
            Long userIdxByJwt = jwtService.getUserIdx();

            if (userId != userIdxByJwt) {
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            GetEatDealDetailRes getEatDealDetailRes = eatDealProvider.getEatDealDetail(eatDealId, userId);

            return new BaseResponse<>(getEatDealDetailRes);

        } catch (
                BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

    /**
     * 잇딜 구매하기 API
     * [POST] /:eatDealId/:userId?paymentWay=1
     * * @return BaseResponse<String>
     */
    @ResponseBody
    @PostMapping("/{eatDealId}/{userId}")
    public BaseResponse<String> buyEatDeal(@PathVariable("eatDealId") Long eatDealId, @PathVariable("userId") Long userId, @RequestParam int paymentWay) {
        try {
            Long userIdxByJwt = jwtService.getUserIdx();

            if (userId != userIdxByJwt) {
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            eatDealService.buyEatDeal(eatDealId, userId,paymentWay);
            String result = "잇딜 구매에 성공하였습니다.";
            return new BaseResponse<>(result);

        } catch (
                BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }


}