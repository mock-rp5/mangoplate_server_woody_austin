package com.example.demo.src.store;


import com.example.demo.src.store.model.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class StoreDao {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }
    public List<GetStoreListRes> getStoreList(GetStoreListReq getStoreListReq) {
        String inSql=String.join(",",getStoreListReq.getRegion().stream().map(region -> "'"+region+"'").collect(Collectors.toList()));
        String getStoreListQuery = String.format("SELECT (select ReviewImgSelect.imgurl from ReviewImg ReviewImgSelect where ReviewImgSelect.reviewId=Review.id limit 1) as 'reviewImg',concat(subRegion,' ',ROUND((6371*acos(cos(radians(Users.Latitude))*cos(radians(Stores.Latitude))\n" +
                "                      *cos(radians(Stores.longitude) -radians(Users.longitude))\n" +
                "                      +sin(radians(Users.Latitude))*sin(radians(Stores.Latitude)))),3),'km')\n" +
                "    AS distance,concat(Stores.name)'storeName',Stores.foodCategory,rating,concat('(','리뷰 ',(select count(Review.id) from Review where Review.storeId=Stores.id),')')'reviewCount'\n" +
                "FROM Users,Stores\n" +
                "    left join Review on Review.storeId=Stores.id left join ReviewImg on ReviewImg.reviewId=Review.id\n" +
                "where Users.id=? and Stores.subRegion IN (%s) LIMIT ?,10",inSql);
        Object[] getStoreListParams=new Object[]{
                getStoreListReq.getUserId(),(getStoreListReq.getPage()-1)*10
        };
        return this.jdbcTemplate.query(getStoreListQuery,
                (rs,rowNum)-> new GetStoreListRes(
                        rs.getString("reviewImg"),
                        rs.getString("distance"),
                        rs.getString("storeName"),
                        rs.getString("foodCategory"),
                        rs.getFloat("rating"),
                        rs.getString("reviewCount")
                ),getStoreListParams);
    }

    public GetStoreRes getStore(int storeId){
        String getStoreQuery = "SELECT DISTINCT S.*, COUNT(R.id) AS reviewCount, COUNT(W.id) AS wishCount FROM Stores S\n" +
                "LEFT JOIN Review R on S.id = R.storeId\n" +
                "LEFT JOIN Wishes W ON S.id = W.storeId\n" +
                "WHERE S.id = ?";
        int getStoreParam = storeId;
        return this.jdbcTemplate.queryForObject(getStoreQuery,
                (rs, rowNum) -> new GetStoreRes(
                        rs.getString("name"),
                        rs.getLong("viewCount"),
                        rs.getLong("reviewCount"),
                        rs.getLong("wishCount"),
                        rs.getFloat("rating"),
                        rs.getString("address"),
                        rs.getDouble("latitude"),
                        rs.getDouble("longitude"),
                        rs.getString("telephone"),
                        rs.getString("openTime"),
                        rs.getString("breakTime"),
                        rs.getString("dayOff"),
                        rs.getString("priceInfo"),
                        rs.getString("foodCategory"),
                        rs.getString("parkingInfo"),
                        rs.getString("website"),
                        rs.getLong("creatorId"),
                        rs.getString("updatedAt")),
                getStoreParam);
    }

    public void increaseViewCount(int storeId){
        String increaseQuery = "UPDATE Stores SET viewCount = viewCount + 1 WHERE id = ?";
        int increaseParam = storeId;
        this.jdbcTemplate.update(increaseQuery, increaseParam);
    }

    public GetMenuRes getMenu(int storeId){
        String getMenuListQuery = "SELECT * FROM Menu WHERE storeId = ?";
        String getImgListQuery = "SELECT imgUrl FROM MenuImg WHERE storeId = ?";
        int Param = storeId;
        List<GetMenuDetailRes> getMenuDetailRes;
        List<GetMenuImgRes> getMenuImgRes;

        return new GetMenuRes(
                getMenuDetailRes = this.jdbcTemplate.query(getMenuListQuery,
                        (rs,rowNum) -> new GetMenuDetailRes(
                                rs.getString("name"),
                                rs.getInt("price"),
                                rs.getString("updatedAt")), Param),
                getMenuImgRes = this.jdbcTemplate.query(getImgListQuery,
                        (rs,rowNum) -> new GetMenuImgRes(
                                rs.getString("imgUrl")), Param)
        );
    }

    public List<GetStoreListRes> getStoreListByKeyWord(GetStoreListByKeyWordReq getStoreListByKeyWordReq) {
        String inSql=String.join(",",getStoreListByKeyWordReq.getRegion().stream().map(region -> "'"+region+"'").collect(Collectors.toList()));
        String getStoreListQuery = String.format("SELECT (select ReviewImgSelect.imgurl from ReviewImg ReviewImgSelect where ReviewImgSelect.reviewId=Review.id limit 1) as 'reviewImg',concat(subRegion,' ',ROUND((6371*acos(cos(radians(Users.Latitude))*cos(radians(Stores.Latitude))\n" +
                "                      *cos(radians(Stores.longitude) -radians(Users.longitude))\n" +
                "                      +sin(radians(Users.Latitude))*sin(radians(Stores.Latitude)))),3),'km')\n" +
                "    AS distance,concat(Stores.name)'storeName',Stores.foodCategory,rating,concat('(','리뷰 ',(select count(Review.id) from Review where Review.storeId=Stores.id),')')'reviewCount'\n" +
                "FROM Users,Stores\n" +
                "    left join Review on Review.storeId=Stores.id left join ReviewImg on ReviewImg.reviewId=Review.id\n" +
                "where Users.id=? and Stores.name like ? and Stores.subRegion IN (%s) LIMIT ?,10",inSql);
        String keyword="%"+getStoreListByKeyWordReq.getKeyword()+"%";
        Object[] getStoreListParams=new Object[]{
                getStoreListByKeyWordReq.getUserId(),keyword,(getStoreListByKeyWordReq.getPage()-1)*10
        };
        return this.jdbcTemplate.query(getStoreListQuery,
                (rs,rowNum)-> new GetStoreListRes(
                        rs.getString("reviewImg"),
                        rs.getString("distance"),
                        rs.getString("storeName"),
                        rs.getString("foodCategory"),
                        rs.getFloat("rating"),
                        rs.getString("reviewCount")
                ),getStoreListParams);
    }
}

