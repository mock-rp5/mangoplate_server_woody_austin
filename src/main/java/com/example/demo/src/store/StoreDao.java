package com.example.demo.src.store;

import com.example.demo.src.store.model.GetStoreListReq;
import com.example.demo.src.store.model.GetStoreListRes;
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
        String getStoreListQuery = String.format("SELECT imgurl as 'reviewImg',concat(subRegion,' ',ROUND((6371*acos(cos(radians(Users.Latitude))*cos(radians(Stores.Latitude))\n" +
                "                      *cos(radians(Stores.longitude) -radians(Users.longitude))\n" +
                "                      +sin(radians(Users.Latitude))*sin(radians(Stores.Latitude)))),3),'km')\n" +
                "    AS distance,concat(Stores.name)'storeName',Stores.foodCategory,rating,concat('(','리뷰 ',(select count(Review.id) from Review where Review.storeId=Stores.id),')')'reviewCount'\n" +
                "FROM Users,Stores\n" +
                "    left join Review on Review.storeId=Stores.id left join ReviewImg on ReviewImg.reviewId=Review.id\n" +
                "where Users.id=? and Stores.subRegion IN (%s) LIMIT ?,10",inSql);
        Object[] getStoreListParams=new Object[]{
                getStoreListReq.getUserId(),(getStoreListReq.getPage()-1)*10
        };
        System.out.println(inSql);
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
