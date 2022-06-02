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

    List<GetStoreReviewImgRes> imgList;

    @Autowired
    public void setDataSource(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }


    public List<GetStoreMainRes> getStoreList(GetStoreListReq getStoreListReq) {
        String inSql=String.join(",",getStoreListReq.getRegion().stream().map(region -> "'"+region+"'").collect(Collectors.toList()));
        String getStoreListQuery="";
        if(getStoreListReq.getFiltering()=="distance") {
            getStoreListQuery = String.format("SELECT Stores.id as 'storeId',(select ReviewImgSelect.imgurl from ReviewImg ReviewImgSelect\n" +
                    "        left join Review on Review.id=reviewId where ReviewImgSelect.reviewId=Review.id and Stores.id=Review.storeId limit 1)as 'reviewImg'," +
                    "(select exists(select Wishes.id from Wishes where Wishes.userId=Users.id and Wishes.storeId=Stores.id))'wishCheck'," +
                    "(select exists(select Visited.id from Visited where Visited.userId=Users.id and Visited.storeId=Stores.id))'visitedCheck',\n" +
                    "      subRegion,ROUND((6371*acos(cos(radians(Users.Latitude))*cos(radians(Stores.Latitude))\n" +
                    "                      *cos(radians(Stores.longitude) -radians(Users.longitude))\n" +
                    "                      +sin(radians(Users.Latitude))*sin(radians(Stores.Latitude)))),2)'distance',\n" +
                    "    concat(Stores.name)'storeName',Stores.foodCategory,rating,viewCount,\n" +
                    "        (select count(Review.id) from Review where Review.storeId=Stores.id limit 1)'reviewCount'\n" +
                    "FROM Users,Stores\n" +
                    "where Users.id=? and Stores.subRegion IN (%s) order by distance asc", inSql);
        }
        else if(getStoreListReq.getFiltering()=="rating"){
            getStoreListQuery = String.format("SELECT Stores.id as 'storeId',(select ReviewImgSelect.imgurl from ReviewImg ReviewImgSelect\n" +
                    "        left join Review on Review.id=reviewId where ReviewImgSelect.reviewId=Review.id and Stores.id=Review.storeId limit 1)as 'reviewImg'," +
                    "(select exists(select Wishes.id from Wishes where Wishes.userId=Users.id and Wishes.storeId=Stores.id))'wishCheck'," +
                    "(select exists(select Visited.id from Visited where Visited.userId=Users.id and Visited.storeId=Stores.id))'visitedCheck',\n" +
                    "      subRegion,ROUND((6371*acos(cos(radians(Users.Latitude))*cos(radians(Stores.Latitude))\n" +
                    "                      *cos(radians(Stores.longitude) -radians(Users.longitude))\n" +
                    "                      +sin(radians(Users.Latitude))*sin(radians(Stores.Latitude)))),2)'distance',\n" +
                    "    concat(Stores.name)'storeName',Stores.foodCategory,rating,viewCount,\n" +
                    "        (select count(Review.id) from Review where Review.storeId=Stores.id limit 1)'reviewCount'\n" +
                    "FROM Users,Stores\n" +
                    "where Users.id=? and Stores.subRegion IN (%s) order by rating desc ", inSql);
        }
        else if(getStoreListReq.getFiltering()=="reviewCount"){
            getStoreListQuery = String.format("SELECT Stores.id as 'storeId',(select ReviewImgSelect.imgurl from ReviewImg ReviewImgSelect\n" +
                    "        left join Review on Review.id=reviewId where ReviewImgSelect.reviewId=Review.id and Stores.id=Review.storeId limit 1)as 'reviewImg'," +
                    "(select exists(select Wishes.id from Wishes where Wishes.userId=Users.id and Wishes.storeId=Stores.id))'wishCheck'," +
                    "(select exists(select Visited.id from Visited where Visited.userId=Users.id and Visited.storeId=Stores.id))'visitedCheck',\n" +
                    "      subRegion,ROUND((6371*acos(cos(radians(Users.Latitude))*cos(radians(Stores.Latitude))\n" +
                    "                      *cos(radians(Stores.longitude) -radians(Users.longitude))\n" +
                    "                      +sin(radians(Users.Latitude))*sin(radians(Stores.Latitude)))),2)'distance',\n" +
                    "    concat(Stores.name)'storeName',Stores.foodCategory,rating,viewCount,\n" +
                    "        (select count(Review.id) from Review where Review.storeId=Stores.id limit 1)'reviewCount'\n" +
                    "FROM Users,Stores\n" +
                    "where Users.id=? and Stores.subRegion IN (%s) order by reviewCount desc ", inSql);
        }


        Object[] getStoreListParams=new Object[]{
                getStoreListReq.getUserId()
        };
        return this.jdbcTemplate.query(getStoreListQuery,
                (rs,rowNum)-> new GetStoreMainRes(
                        rs.getLong("storeId"),
                        rs.getString("reviewImg"),
                        rs.getInt("wishCheck"),
                        rs.getInt("visitedCheck"),
                        rs.getString("subRegion"),
                        rs.getFloat("distance"),
                        rs.getString("storeName"),
                        rs.getFloat("rating"),
                        rs.getInt("viewCount"),
                        rs.getInt("reviewCount")
                ),getStoreListParams);
    }

    public GetStoreRes getStore(Long storeId, Long userId){
        String getStoreQuery = "SELECT DISTINCT S.id AS storeId, S.*,  COUNT(R.id) AS reviewCount, COUNT(W.id) AS wishCount,\n" +
                "                (select exists(select Wishes.id from Wishes where Wishes.userId=U.id && Wishes.storeId=S.id))'wishCheck',\n" +
                "                (select COUNT(Visited.id) from Visited where Visited.userId = U.id && Visited.storeId = S.id)'visitedCount'\n" +
                "                FROM Users U, Stores S LEFT JOIN Review R on S.id = R.storeId\n" +
                "                LEFT JOIN Wishes W ON S.id = W.storeId\n" +
                "                WHERE S.id = ? && U.id = ?";

        return this.jdbcTemplate.queryForObject(getStoreQuery,
                (rs, rowNum) -> new GetStoreRes(
                        rs.getLong("storeId"),
                        rs.getString("name"),
                        rs.getLong("viewCount"),
                        rs.getLong("reviewCount"),
                        rs.getLong("wishCount"),
                        rs.getFloat("rating"),
                        rs.getInt("wishCheck"),
                        rs.getInt("visitedCount"),
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
                storeId, userId);
    }

    public void increaseViewCount(Long storeId){
        String increaseQuery = "UPDATE Stores SET viewCount = viewCount + 1 WHERE id = ?";
        Long increaseParam = storeId;
        this.jdbcTemplate.update(increaseQuery, increaseParam);
    }

    public GetMenuRes getMenu(Long storeId){
        String getMenuListQuery = "SELECT * FROM Menu WHERE storeId = ?";
        String getImgListQuery = "SELECT imgUrl FROM MenuImg WHERE storeId = ?";
        Long Param = storeId;
        List<GetMenuDetailRes> getMenuDetailRes;
        List<GetMenuImgRes> getMenuImgRes;

        return new GetMenuRes(
                getMenuDetailRes = this.jdbcTemplate.query(getMenuListQuery,
                        (rs,rowNum) -> new GetMenuDetailRes(
                                rs.getLong("storeId"),
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
        String regionSql="";
        if(getStoreListByKeyWordReq.getRegion().contains("all")){
            regionSql="";
        }
        else{
            regionSql = "and subRegion IN "+"("+inSql+")";
        }
        String getStoreListQuery = String.format("SELECT Stores.id as 'storeId',(select ReviewImgSelect.imgurl from ReviewImg ReviewImgSelect\n" +
                "        left join Review on Review.id=reviewId where ReviewImgSelect.reviewId=Review.id and Stores.id=Review.storeId limit 1)as 'reviewImg'," +
                "       (select exists(select Wishes.id from Wishes where Wishes.userId=Users.id and Wishes.storeId=Stores.id))'wishCheck'," +
                "(select exists(select Visited.id from Visited where Visited.userId=Users.id and Visited.storeId=Stores.id))'visitedCheck',\n" +
                "       subRegion,ROUND((6371*acos(cos(radians(Users.Latitude))*cos(radians(Stores.Latitude))\n" +
                "                      *cos(radians(Stores.longitude) -radians(Users.longitude))\n" +
                "                      +sin(radians(Users.Latitude))*sin(radians(Stores.Latitude)))),2)'distance'" +
                "                   ,concat(Stores.name)'storeName',Stores.foodCategory,rating,viewCount,\n" +
                "        (select count(Review.id) from Review where Review.storeId=Stores.id limit 1)'reviewCount'\n" +
                "FROM Users,Stores\n" +
                "where Users.id=? %s and Stores.name like ? ",regionSql);
        String keyword="%"+getStoreListByKeyWordReq.getKeyword()+"%";
        Object[] getStoreListParams=new Object[]{
                getStoreListByKeyWordReq.getUserId(),keyword
        };
        return this.jdbcTemplate.query(getStoreListQuery,
                (rs,rowNum)-> new GetStoreListRes(
                        rs.getLong("storeId"),
                        rs.getString("reviewImg"),
                        rs.getInt("wishCheck"),
                        rs.getInt("visitedCheck"),
                        rs.getString("subRegion"),
                        rs.getFloat("distance"),
                        rs.getString("storeName"),
                        rs.getFloat("rating"),
                        rs.getInt("viewCount"),
                        rs.getInt("reviewCount")
                ),getStoreListParams);
    }

    public int checkStoreId(Long storeId){
        String checkStoreIdQuery = "select exists(select id from Stores where id = ?)";
        Long checkStoreIdParams = storeId;
        return this.jdbcTemplate.queryForObject(checkStoreIdQuery, int.class, checkStoreIdParams);
    }

    public int checkMylistId(Long mylistId){
        String checkMylistIdQuery = "select exists(select id from Mylists where id = ?)";
        Long checkMylistIdParams = mylistId;
        return this.jdbcTemplate.queryForObject(checkMylistIdQuery, int.class, checkMylistIdParams);
    }

    public int checkMylistUser(Long mylistId) {
        String checkUserQuery="select userId from Mylists where id = ? ";
        return this.jdbcTemplate.queryForObject(checkUserQuery,int.class,mylistId);
    }

    public int checkWish(Long storeId, Long userId) {
        String checkWishQuery="select exists(select Wishes.id from Wishes where storeId=? and userId=?)";
        return this.jdbcTemplate.queryForObject(checkWishQuery,int.class,storeId, userId);
    }

    public int checkVisited(Long storeId, Long userId) {
        String checkVisitedQuery="select exists(select Visited.id from Visited where storeId=? and userId=?)";
        return this.jdbcTemplate.queryForObject(checkVisitedQuery,int.class,storeId, userId);
    }

    public int createWish(Long storeId, Long userId) {
        String createWishQuery="insert into Wishes(storeId,userId) values(?,?)";
        return this.jdbcTemplate.update(createWishQuery, storeId, userId);
    }

    public int deleteWish(Long storeId, Long userId) {
        String createWishQuery="delete from Wishes where storeId = ? && userId = ?";
        return this.jdbcTemplate.update(createWishQuery, storeId, userId);
    }

    public List<GetStoreListRes> getStoreListByFood(GetStoreListByFoodReq getStoreListByFoodReq) {
        String categoryList=String.join(",",getStoreListByFoodReq.getCategory().stream().map(category -> "'"+category+"'").collect(Collectors.toList()));
        String regionList=String.join(",",getStoreListByFoodReq.getRegion().stream().map(region -> "'"+region+"'").collect(Collectors.toList()));

        String getStoreListQuery = String.format("SELECT Stores.id AS storeId, (select ReviewImgSelect.imgurl from ReviewImg ReviewImgSelect\n" +
                "        left join Review on Review.id=reviewId where ReviewImgSelect.reviewId=Review.id and Stores.id=Review.storeId limit 1)as 'reviewImg'," +
                "(select exists(select Wishes.id from Wishes where Wishes.userId=Users.id and Wishes.storeId=Stores.id))'wishCheck'," +
                "(select exists(select Visited.id from Visited where Visited.userId=Users.id and Visited.storeId=Stores.id))'visitedCheck',\n" +
                "       subRegion,ROUND((6371*acos(cos(radians(Users.Latitude))*cos(radians(Stores.Latitude))\n" +
                "                      *cos(radians(Stores.longitude) -radians(Users.longitude))\n" +
                "                      +sin(radians(Users.Latitude))*sin(radians(Stores.Latitude)))),2)'distance',\n" +
                "       Stores.name AS storeName, rating, viewCount, (SELECT count(Review.id) FROM Review WHERE Review.storeId=Stores.id) AS reviewCount\n" +
                "FROM Users,Stores\n" +
                "WHERE Users.id=? && Stores.foodCategory IN (%s) && Stores.subRegion IN (%s)",categoryList, regionList);
        return this.jdbcTemplate.query(getStoreListQuery,
                (rs,rowNum)-> new GetStoreListRes(
                        rs.getLong("storeId"),
                        rs.getString("reviewImg"),
                        rs.getInt("wishCheck"),
                        rs.getInt("visitedCheck"),
                        rs.getString("subRegion"),
                        rs.getFloat("distance"),
                        rs.getString("storeName"),
                        rs.getFloat("rating"),
                        rs.getInt("viewCount"),
                        rs.getInt("reviewCount")
                ),getStoreListByFoodReq.getUserId());
    }

    public List<GetStoreListRes> getStoreListByParking(Long userId, List<String> region) {
        String regionList=String.join(",",region.stream().map(region_ -> "'"+region_+"'").collect(Collectors.toList()));
        String getStoreListByParkingQuery = String.format("SELECT Stores.id AS storeId, (select ReviewImgSelect.imgurl from ReviewImg ReviewImgSelect\n" +
                "        left join Review on Review.id=reviewId where ReviewImgSelect.reviewId=Review.id and Stores.id=Review.storeId limit 1)as 'reviewImg'," +
                "(select exists(select Wishes.id from Wishes where Wishes.userId=Users.id and Wishes.storeId=Stores.id))'wishCheck'," +
                "(select exists(select Visited.id from Visited where Visited.userId=Users.id and Visited.storeId=Stores.id))'visitedCheck',\n" +
                "       subRegion,ROUND((6371*acos(cos(radians(Users.Latitude))*cos(radians(Stores.Latitude))\n" +
                "                      *cos(radians(Stores.longitude) -radians(Users.longitude))\n" +
                "                      +sin(radians(Users.Latitude))*sin(radians(Stores.Latitude)))),2)'distance',\n" +
                "       Stores.name AS storeName, rating, viewCount, (SELECT count(Review.id) FROM Review WHERE Review.storeId=Stores.id) AS reviewCount\n" +
                "FROM Users,Stores\n" +
                "WHERE Users.id=? && Stores.parkingInfo = '가능' && Stores.subRegion IN (%s)", regionList);
        return this.jdbcTemplate.query(getStoreListByParkingQuery,
                (rs,rowNum)-> new GetStoreListRes(
                        rs.getLong("storeId"),
                        rs.getString("reviewImg"),
                        rs.getInt("wishCheck"),
                        rs.getInt("visitedCheck"),
                        rs.getString("subRegion"),
                        rs.getFloat("distance"),
                        rs.getString("storeName"),
                        rs.getFloat("rating"),
                        rs.getInt("viewCount"),
                        rs.getInt("reviewCount")
                ),userId);
    }


    public List<GetStoreListRes> getStoreListByDistance(Long userId, int distance) {
        String getStoreListByDistanceQuery = "SELECT Stores.id AS storeId, (select ReviewImgSelect.imgurl from ReviewImg ReviewImgSelect \n" +
                "                            left join Review on Review.id=reviewId where ReviewImgSelect.reviewId=Review.id and Stores.id=Review.storeId limit 1)as 'reviewImg'," +
                "(select exists(select Wishes.id from Wishes where Wishes.userId=Users.id and Wishes.storeId=Stores.id))'wishCheck'," +
                "(select exists(select Visited.id from Visited where Visited.userId=Users.id and Visited.storeId=Stores.id))'visitedCheck',\n" +
                "      subRegion, ROUND((6371*acos(cos(radians(Users.Latitude))*cos(radians(Stores.Latitude))\n" +
                "           *cos(radians(Stores.longitude) -radians(Users.longitude))+sin(radians(Users.Latitude))*sin(radians(Stores.Latitude)))),2) AS distance,\n" +
                "       Stores.name AS storeName, rating, viewCount, (SELECT count(Review.id) FROM Review WHERE Review.storeId=Stores.id) AS reviewCount\n" +
                "FROM Users,Stores\n" +
                "WHERE Users.id=? && ROUND((6371*acos(cos(radians(Users.Latitude))*cos(radians(Stores.Latitude))\n" +
                "*cos(radians(Stores.longitude) -radians(Users.longitude))+sin(radians(Users.Latitude))*sin(radians(Stores.Latitude)))),2) <= ?";
        Object[] getStoreListParams=new Object[]{userId,distance * 0.001};
        return this.jdbcTemplate.query(getStoreListByDistanceQuery,
                (rs,rowNum)-> new GetStoreListRes(
                        rs.getLong("storeId"),
                        rs.getString("reviewImg"),
                        rs.getInt("wishCheck"),
                        rs.getInt("visitedCheck"),
                        rs.getString("subRegion"),
                        rs.getFloat("distance"),
                        rs.getString("storeName"),
                        rs.getFloat("rating"),
                        rs.getInt("viewCount"),
                        rs.getInt("reviewCount")
                ),getStoreListParams);
    }

    public List<GetStoreReviewRes> getStoreReviews(Long storeId, Long userId, List<String> evaluation) {
        String inSql = String.join(",", evaluation.stream().map(evaluationFilter -> "'" + evaluationFilter + "'").collect(Collectors.toList()));
        String getStoreReviewQuery = String.format("SELECT Review.id AS reviewId, Users.profileImgUrl, Users.id AS userId, Users.name AS userName, isHolic,\n" +
                "(SELECT COUNT(R.id) from Review R where R.userId=Users.id) reviewCount,\n" +
                "(SELECT COUNT(F.id) FROM Following F WHERE F.follwedUserId = Review.userId) followCount, evaluation, review, Review.updatedAt,\n" +
                "(select count(*) from ReviewLikes where ReviewLikes.reviewId= Review.id)'reviewLikes',\n" +
                "(select count(*) from ReviewComments where ReviewComments.reviewId=Review.id)'reviewComments'," +
                "(select exists(select ReviewLikes.id from ReviewLikes where ReviewLikes.userId=? and Review.id=ReviewLikes.reviewId))'likeCheck'\n" +
                "from Users, Review, Stores\n" +
                "WHERE Review.userId=Users.id && Stores.id = Review.storeId && Stores.id = ? &&Review.evaluation IN (%s)",inSql);
        String getImgQuery="select Review.id as 'ReviewId',imgUrl from Stores\n" +
                "    join Review on Review.storeId=Stores.id\n" +
                "    left join ReviewImg on ReviewImg.reviewId=Review.id where Review.id=? order by Review.createdAt ";
        return this.jdbcTemplate.query(getStoreReviewQuery,
                (rs, rowNum) -> new GetStoreReviewRes(
                        rs.getLong("reviewId"),
                        rs.getString("profileImgUrl"),
                        rs.getLong("userId"),
                        rs.getString("userName"),
                        rs.getString("isHolic"),
                        rs.getInt("reviewCount"),
                        rs.getInt("followCount"),
                        rs.getString("evaluation"),
                        rs.getString("review"),
                        rs.getString("updatedAt"),
                        rs.getInt("reviewLikes"),
                        rs.getInt("reviewComments"),
                        rs.getInt("likeCheck"),
                        imgList=this.jdbcTemplate.query(getImgQuery,
                                (rk,rownum)->new GetStoreReviewImgRes(
                                        rk.getLong("reviewId"),
                                        rk.getString("imgUrl")
                                ),rs.getLong("reviewId"))
                ),userId, storeId);
    }

    public void addStoreToMylist(Long storeId, Long mylistId) {
        String addQuery = "insert into MylistStores (mylistId, storeId) VALUES (?,?)";
        this.jdbcTemplate.update(addQuery, mylistId, storeId);
    }
}

