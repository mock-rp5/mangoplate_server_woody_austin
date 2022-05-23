package com.example.demo.src.store;

import com.example.demo.src.store.model.GetMenuDetailRes;
import com.example.demo.src.store.model.GetMenuImgRes;
import com.example.demo.src.store.model.GetMenuRes;
import com.example.demo.src.store.model.GetStoreRes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class StoreDao {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public GetStoreRes getStore(int storeId){
        String getStoreQuery = "SELECT DISTINCT S.*, COUNT(R.id) AS reviewCount, COUNT(W.id) AS wishCount FROM Review R, Stores S\n" +
                "    LEFT JOIN Wishes W ON S.id = W.storeId\n" +
                "WHERE S.id = ? && S.id = R.storeId";
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
}