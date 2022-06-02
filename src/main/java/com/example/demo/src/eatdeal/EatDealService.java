package com.example.demo.src.eatdeal;

import com.example.demo.config.BaseException;
import com.example.demo.src.eatdeal.model.GetEatDealRes;
import com.example.demo.src.store.StoreDao;
import com.example.demo.src.store.StoreProvider;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.example.demo.config.BaseResponseStatus.*;

@Service
public class EatDealService {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final EatDealDao eatDealDao;
    private final EatDealProvider eatDealProvider;
    private final JwtService jwtService;


    @Autowired
    public EatDealService(EatDealDao eatDealDao, EatDealProvider eatDealProvider, JwtService jwtService) {
        this.eatDealDao = eatDealDao;
        this.eatDealProvider = eatDealProvider;
        this.jwtService = jwtService;
    }

    public void buyEatDeal(Long eatDealId, Long userId, int paymentWay) throws BaseException {
        if (eatDealDao.checkEatDealId(eatDealId) == 0)
            throw new BaseException(NON_EXIST_EAT_DEAL);
        String payWay;
        if(paymentWay == 1) {
            payWay = "신용카드";
        }
        else if(paymentWay == 2) {
            payWay = "카카오페이";
        }
        else{
            throw new BaseException(WRONG_PAYMENT_WAY);
        }
        try {
            eatDealDao.buyEatDeal(eatDealId, userId, payWay);
        } catch (Exception e) {
            throw new BaseException(DATABASE_ERROR);
        }

    }

}
