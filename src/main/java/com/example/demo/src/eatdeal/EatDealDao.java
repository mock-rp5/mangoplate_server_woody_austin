package com.example.demo.src.eatdeal;

import com.example.demo.src.eatdeal.model.GetEatDealDetailRes;
import com.example.demo.src.eatdeal.model.GetEatDealRes;
import com.example.demo.src.news.model.GetImgRes;
import com.example.demo.src.store.model.GetStoreListReq;
import com.example.demo.src.store.model.GetStoreMainRes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class EatDealDao {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public List<GetEatDealRes> getEatDealList(List<String> region) {
        String inSql = String.join(",", region.stream().map(region_ -> "'" + region_ + "'").collect(Collectors.toList()));
        String getEatDealListQuery = String.format("SELECT E.id AS eatDealId, S.subRegion, S.name AS storeName, E.menuName,\n" +
                "       (select I.imgUrl from EatDealImg I where I.eatDealId = E.id LIMIT 1) AS imgUrl,\n" +
                "       E.discountPercent, E.originalPrice, E.finalPrice, E.memo\n" +
                "FROM EatDeals E, Stores S\n" +
                "WHERE S.subRegion IN (%s) && E.storeId = S.id", inSql);
        return this.jdbcTemplate.query(getEatDealListQuery,
                (rs, rowNum) -> new GetEatDealRes(
                        rs.getLong("eatDealId"),
                        rs.getString("subRegion"),
                        rs.getString("storeName"),
                        rs.getString("menuName"),
                        rs.getString("imgUrl"),
                        rs.getInt("discountPercent"),
                        rs.getInt("originalPrice"),
                        rs.getInt("finalPrice"),
                        rs.getString("memo")
                ));
    }

    public int checkEatDealId(Long eatDealId){
        String checkEatDealIdQuery = "select exists(select id from EatDeals where id = ?)";
        Long checkEatDealIdParams = eatDealId;
        return this.jdbcTemplate.queryForObject(checkEatDealIdQuery, int.class, checkEatDealIdParams);
    }

    public GetEatDealDetailRes getEatDealDetail(Long eatDealId, Long userId) {
        String getEatDealQuery = "SELECT E.id AS eatDealId, S.subRegion, S.name AS storeName, E.menuName,\n" +
                "       (select I.imgUrl from EatDealImg I where I.eatDealId = E.id LIMIT 1) AS imgUrl,\n" +
                "       E.discountPercent, E.originalPrice, E.finalPrice, E.memo,\n" +
                "       S.id AS storeId, S.latitude, S.longitude,\n" +
                "       E.storeDescription, E.menuDescription,\n" +
                "       DATE(now())AS startDate, DATE (ADDDATE(now(),93)) AS endDate\n" +
                "FROM EatDeals E, Stores S\n" +
                "WHERE E.storeId = S.id && E.id = ?";
        return this.jdbcTemplate.queryForObject(getEatDealQuery,
                (rs, rowNum) -> new GetEatDealDetailRes(
                        rs.getLong("eatDealId"),
                        rs.getString("imgUrl"),
                        rs.getString("subRegion"),
                        rs.getString("storeName"),
                        rs.getString("menuName"),
                        rs.getString("startDate"),
                        rs.getString("endDate"),
                        rs.getInt("discountPercent"),
                        rs.getInt("originalPrice"),
                        rs.getInt("finalPrice"),
                        rs.getLong("storeId"),
                        rs.getDouble("latitude"),
                        rs.getDouble("longitude"),
                        rs.getString("memo"),
                        rs.getString("storeDescription"),
                        rs.getString("menuDescription")
                ), eatDealId);
    }

    public void buyEatDeal(Long eatDealId, Long userId, String payWay) {
        String buyEatDealQuery = "INSERT INTO EatDealPayment (userId, eatDealId, paymentWay) VALUES (?,?,?)";
        this.jdbcTemplate.update(buyEatDealQuery, userId, eatDealId, payWay);
    }
}
