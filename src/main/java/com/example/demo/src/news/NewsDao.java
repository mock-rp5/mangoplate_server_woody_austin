package com.example.demo.src.news;

import com.example.demo.src.news.model.GetImgRes;
import com.example.demo.src.news.model.GetNewsByFollowingReq;
import com.example.demo.src.news.model.GetNewsMainRes;
import com.example.demo.src.news.model.GetNewsRes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class NewsDao {
    private JdbcTemplate jdbcTemplate;

    List<GetImgRes> ImgList;
    @Autowired
    public void setDataSource(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public List<GetNewsMainRes> getNews(Long userId, List<String> evaluation, List<String> region) {
        String inSql = String.join(",", evaluation.stream().map(evaluationFilter -> "'" + evaluationFilter + "'").collect(Collectors.toList()));
        String regionList=String.join(",", region.stream().map(regionFilter -> "'" + regionFilter + "'").collect(Collectors.toList()));
        String regionSql="";
        if(region.contains("all")){
            regionSql="";
        }
        else{
            regionSql="and"+" subRegion IN"+"("+regionList+")";
        }
        String getNewsQuery = String.format("select Review.id as 'reviewId',Users.profileImgUrl,Users.name,isHolic," +
                "       (select count(Re.review) from Review Re where Re.userId=Users.id)'reviewCount',\n" +
                "       (select count(follwedUserId) from Following where follwedUserId=Review.userId)'followCount',evaluation,Stores.id as 'storeId',\n" +
                "       concat('@ ',Stores.name,' - ',Stores.subRegion)'storeName',review,\n" +
                "       case when YEAR(Review.createdAt)<YEAR(now())\n" +
                "                    then concat(YEAR(Review.createdAt),'년 ',MONTH(Review.createdAt),'월 ',DAY(Review.createdAt),'일')\n" +
                "                                 when YEAR(Review.createdAt)=YEAR(now()) then\n" +
                "                                       case\n" +
                "                                           when TIMESTAMPDIFF(minute,Review.createdAt,now())<1\n" +
                "                                                then concat(TIMESTAMPDIFF(second,Review.createdAt,now()),' 초 전')\n" +
                "                                           when TIMESTAMPDIFF(hour,Review.createdAt,now())<1\n" +
                "                                                then concat(TIMESTAMPDIFF(minute,Review.createdAt,now()),'분 전')\n" +
                "                                            when TIMESTAMPDIFF(hour,Review.createdAt,now())<24\n" +
                "                                                then concat(TIMESTAMPDIFF(hour,Review.createdAt,now()),' 시간 전')\n" +
                "                                              when (TIMESTAMPDIFF(DAY,Review.createdAt,now()))>7\n" +
                "                                               then concat(year(Review.createdAt),'년 ',month(Review.createdAt),'월 ',DAY(Review.createdAt),'일')\n" +
                "                                            when TIMESTAMPDIFF(minute,Review.createdAt,now())<1\n" +
                "                                                then concat(TIMESTAMPDIFF(second,Review.createdAt,now()))\n" +
                "                                            when TIMESTAMPDIFF(hour,Review.createdAt,now())>24\n" +
                "                                                then concat(TIMESTAMPDIFF(DAY,Review.createdAt,now()),' 일 전')\n" +
                "                                        end end as reviewCreated,(SELECT CONCAT('[', jsonarray, ']') AS emails FROM\n" +
                "     (SELECT GROUP_CONCAT('{', jsonitem, '}' SEPARATOR ',') AS jsonarray FROM\n" +
                "    (\n" +
                "      SELECT\n" +
                "        CONCAT\n" +
                "        (\n" +
                "          '\"imgUrlList\":'   , '\"', ReviewImg.imgUrl   , '\"'\n" +
                "        ) AS jsonitem\n" +
                "      from ReviewImg left join Review ImgByReview on ReviewImg.reviewId=ImgByReview.id where ImgByReview.id=Review.id\n" +
                "    ) AS singlejson\n" +
                ") AS alljsonas )imgUrl,\n" +
                "       (select count(*) from ReviewLikes where ReviewLikes.reviewId= Review.id)'reviewLikes',\n" +
                "        (select count(*) from ReviewComments where ReviewComments.reviewId=Review.id)'reviewComments'," +
                "       (select exists(select Wishes.id from Wishes where Wishes.userId=? and Wishes.storeId=Stores.id))'wishCheck'\n" +
                "       ,(select exists(select ReviewLikes.id from ReviewLikes where ReviewLikes.userId=? and Review.id=ReviewLikes.reviewId))'likeCheck'," +
                "        (select imgUrl from ReviewImg where ReviewImg.reviewId=Review.id limit 1) as 'reviewImgUrl'\n" +
                "    from Users\n" +
                "    join Review on Review.userId=Users.id\n" +
                "    join Stores on Stores.id = Review.storeId where evaluation IN(%s) %s order by Review.createdAt desc  ", inSql,regionSql);
        String getImgQuery="select Review.id as 'ReviewId',imgUrl from Stores\n" +
                "    join Review on Review.storeId=Stores.id\n" +
                "    left join ReviewImg on ReviewImg.reviewId=Review.id where Review.id=? order by Review.createdAt ";
        Object[] getNewsParams=new Object[]{
                userId,userId};
        List<GetNewsMainRes> getNewsRes;
        return getNewsRes=this.jdbcTemplate.query(getNewsQuery,
                (rs, rowNum) -> new GetNewsMainRes(
                        rs.getLong("reviewId"),
                        rs.getString("profileImgUrl"),
                        rs.getString("name"),
                        rs.getString("isHolic"),
                        rs.getInt("reviewCount"),
                        rs.getInt("followCount"),
                        rs.getString("evaluation"),
                        rs.getLong("storeId"),
                        rs.getString("storeName"),
                        rs.getString("review"),
                        rs.getString("reviewCreated"),
                        rs.getInt("reviewLikes"),
                        rs.getInt("reviewComments"),
                        rs.getInt("wishCheck"),
                        rs.getInt("likeCheck"),
                        rs.getString("reviewImgUrl"),
                        ImgList=this.jdbcTemplate.query(getImgQuery,
                                (rk,rownum)->new GetImgRes(
                                        rk.getLong("reviewId"),
                                        rk.getString("imgUrl")
                                ),rs.getLong("reviewId"))
                ),getNewsParams
        );




    }

    public List<GetNewsRes> getNewsByFollowing(GetNewsByFollowingReq getNewsByFollowingReq) {
        String inSql=String.join(",",getNewsByFollowingReq.getEvaluation().stream().map(evaluationFilter->"'"+evaluationFilter+"'").collect(Collectors.toList()));
        String regionList=String.join(",", getNewsByFollowingReq.getRegion().stream().map(regionFilter -> "'" + regionFilter + "'").collect(Collectors.toList()));
        String regionSql="";
        if(getNewsByFollowingReq.getRegion().contains("all")){
            regionSql="";
        }
        else{
            regionSql="and"+" subRegion IN"+"("+regionList+")";
        }
        String getNewsQuery=String.format("select Review.id as 'reviewId' ,Users.profileImgUrl,Users.name,isHolic,(select count(Re.review) from Review Re where Re.userId=Users.id)'reviewCount',\n" +
                "       (select count(follwedUserId) from Following where follwedUserId=Review.userId)'followCount',evaluation,Stores.id as 'storeId',\n" +
                "       concat('@ ',Stores.name,' - ',Stores.subRegion)'storeName',review,\n" +
                "       case when YEAR(Review.createdAt)<YEAR(now())\n" +
                "                    then concat(YEAR(Review.createdAt),'년 ',MONTH(Review.createdAt),'월 ',DAY(Review.createdAt),'일')\n" +
                "                                 when YEAR(Review.createdAt)=YEAR(now()) then\n" +
                "                                       case\n" +
                "                                           when TIMESTAMPDIFF(minute,Review.createdAt,now())<1\n" +
                "                                                then concat(TIMESTAMPDIFF(second,Review.createdAt,now()),' 초 전')\n" +
                "                                           when TIMESTAMPDIFF(hour,Review.createdAt,now())<1\n" +
                "                                                then concat(TIMESTAMPDIFF(minute,Review.createdAt,now()),'분 전')\n" +
                "                                            when TIMESTAMPDIFF(hour,Review.createdAt,now())<24\n" +
                "                                                then concat(TIMESTAMPDIFF(hour,Review.createdAt,now()),' 시간 전')\n" +
                "                                              when (TIMESTAMPDIFF(DAY,Review.createdAt,now()))>7\n" +
                "                                               then concat(year(Review.createdAt),'년 ',month(Review.createdAt),'월 ',DAY(Review.createdAt),'일')\n" +
                "                                            when TIMESTAMPDIFF(minute,Review.createdAt,now())<1\n" +
                "                                                then concat(TIMESTAMPDIFF(second,Review.createdAt,now()))\n" +
                "                                            when TIMESTAMPDIFF(hour,Review.createdAt,now())>24\n" +
                "                                                then concat(TIMESTAMPDIFF(DAY,Review.createdAt,now()),' 일 전')\n" +
                "                                        end end as reviewCreated," +
                "     (SELECT CONCAT('[', jsonarray, ']') AS emails FROM(SELECT GROUP_CONCAT('{', jsonitem, '}' SEPARATOR ',') AS jsonarray FROM\n" +
                "    (\n" +
                "      SELECT\n" +
                "        CONCAT\n" +
                "        (\n" +
                "          '\"imgUrlList\":'   , '\"', ReviewImg.imgUrl   , '\"'\n" +
                "        ) AS jsonitem\n" +
                "      from ReviewImg left join Review ImgByReview on ReviewImg.reviewId=ImgByReview.id where ImgByReview.id=Review.id\n" +
                "    ) AS singlejson\n" +
                ") AS alljsonas )imgUrl,\n" +
                "       (select count(*) from ReviewLikes where ReviewLikes.reviewId= Review.id)'reviewLikes',\n" +
                "        (select count(*) from ReviewComments where ReviewComments.reviewId=Review.id)'reviewComments'," +
                "(select exists(select Wishes.id from Wishes where Wishes.userId=? and Wishes.storeId=Stores.id))'wishCheck',\n" +
                "(select exists(select Visited.id from Visited where Visited.userId=? and Visited.storeId=Stores.id))'visitedCheck',\n" +
                "(select exists(select ReviewLikes.id from ReviewLikes where ReviewLikes.userId=? and Review.id=ReviewLikes.reviewId))'likeCheck'" +
                "    from Users\n" +
                "    join Review on Review.userId=Users.id\n" +
                "    join Stores on Stores.id = Review.storeId  " +
                "    join Following on Users.id = Following.follwedUserId where Following.userid=? and evaluation IN(%s) %s" +
                "   order by Review.createdAt desc",inSql,regionSql);
        String getImgQuery="select Review.id as 'ReviewId',imgUrl from Stores\n" +
                "    join Review on Review.storeId=Stores.id\n" +
                "    left join ReviewImg on ReviewImg.reviewId=Review.id " +
                "    join Users on Review.userId=Users.id " +
                "    join Following on Users.id = Following.follwedUserId  " +
                "where Review.id=? and Following.userid=?  order by Review.createdAt ";
        Object[] getNewsParams= new Object[]{
                getNewsByFollowingReq.getUserid(), getNewsByFollowingReq.getUserid(), getNewsByFollowingReq.getUserid(), getNewsByFollowingReq.getUserid()
        };
        return this.jdbcTemplate.query(getNewsQuery,
                (rs,rowNum)->new GetNewsRes(
                        rs.getLong("reviewId"),
                        rs.getString("profileImgUrl"),
                        rs.getString("name"),
                        rs.getString("isHolic"),
                        rs.getInt("reviewCount"),
                        rs.getInt("followCount"),
                        rs.getString("evaluation"),
                        rs.getLong("storeId"),
                        rs.getString("storeName"),
                        rs.getString("review"),
                        rs.getString("reviewCreated"),
                        rs.getInt("reviewLikes"),
                        rs.getInt("reviewComments"),
                        rs.getInt("wishCheck"),
                        rs.getInt("visitedCheck"),
                        rs.getInt("likeCheck"),
                        ImgList=this.jdbcTemplate.query(getImgQuery,
                                (rk,rownum)->new GetImgRes(
                                        rk.getLong("reviewId"),
                                        rk.getString("imgUrl")
                                ),rs.getLong("reviewId"),getNewsByFollowingReq.getUserid())
                ),getNewsParams
        );

    }


    public List<GetNewsRes> getNewsHolic(Long userId, List<String> evaluation, List<String> region) {
        String inSql = String.join(",", evaluation.stream().map(evaluationFilter -> "'" + evaluationFilter + "'").collect(Collectors.toList()));
        String regionList=String.join(",", region.stream().map(regionFilter -> "'" + regionFilter + "'").collect(Collectors.toList()));
        String regionSql="";
        if(region.contains("all")){
            regionSql="";
        }
        else{
            regionSql="and"+" subRegion IN"+"("+regionList+")";
        }
        String getNewsQuery=String.format("select Review.id as 'reviewId' ,Users.profileImgUrl,Users.name,isHolic,(select count(Re.review) from Review Re where Re.userId=Users.id)'reviewCount',\n" +
                "       (select count(follwedUserId) from Following where follwedUserId=Review.userId)'followCount',evaluation,Stores.id as 'storeId',\n" +
                "       concat('@ ',Stores.name,' - ',Stores.subRegion)'storeName',review,\n" +
                "       case when YEAR(Review.createdAt)<YEAR(now())\n" +
                "                    then concat(YEAR(Review.createdAt),'년 ',MONTH(Review.createdAt),'월 ',DAY(Review.createdAt),'일')\n" +
                "                                 when YEAR(Review.createdAt)=YEAR(now()) then\n" +
                "                                       case\n" +
                "                                           when TIMESTAMPDIFF(minute,Review.createdAt,now())<1\n" +
                "                                                then concat(TIMESTAMPDIFF(second,Review.createdAt,now()),' 초 전')\n" +
                "                                           when TIMESTAMPDIFF(hour,Review.createdAt,now())<1\n" +
                "                                                then concat(TIMESTAMPDIFF(minute,Review.createdAt,now()),'분 전')\n" +
                "                                            when TIMESTAMPDIFF(hour,Review.createdAt,now())<24\n" +
                "                                                then concat(TIMESTAMPDIFF(hour,Review.createdAt,now()),' 시간 전')\n" +
                "                                              when (TIMESTAMPDIFF(DAY,Review.createdAt,now()))>7\n" +
                "                                               then concat(year(Review.createdAt),'년 ',month(Review.createdAt),'월 ',DAY(Review.createdAt),'일')\n" +
                "                                            when TIMESTAMPDIFF(minute,Review.createdAt,now())<1\n" +
                "                                                then concat(TIMESTAMPDIFF(second,Review.createdAt,now()))\n" +
                "                                            when TIMESTAMPDIFF(hour,Review.createdAt,now())>24\n" +
                "                                                then concat(TIMESTAMPDIFF(DAY,Review.createdAt,now()),' 일 전')\n" +
                "                                        end end as reviewCreated," +
                "     (SELECT CONCAT('[', jsonarray, ']') AS emails FROM(SELECT GROUP_CONCAT('{', jsonitem, '}' SEPARATOR ',') AS jsonarray FROM\n" +
                "    (\n" +
                "      SELECT\n" +
                "        CONCAT\n" +
                "        (\n" +
                "          '\"imgUrlList\":'   , '\"', ReviewImg.imgUrl   , '\"'\n" +
                "        ) AS jsonitem\n" +
                "      from ReviewImg left join Review ImgByReview on ReviewImg.reviewId=ImgByReview.id where ImgByReview.id=Review.id\n" +
                "    ) AS singlejson\n" +
                ") AS alljsonas )imgUrl,\n" +
                "       (select count(*) from ReviewLikes where ReviewLikes.reviewId= Review.id)'reviewLikes',\n" +
                "        (select count(*) from ReviewComments where ReviewComments.reviewId=Review.id)'reviewComments'," +
                "(select exists(select Wishes.id from Wishes where Wishes.userId=? and Wishes.storeId=Stores.id))'wishCheck'," +
                "(select exists(select Visited.id from Visited where Visited.userId=? and Visited.storeId=Stores.id))'visitedCheck'\n" +
                ",(select exists(select ReviewLikes.id from ReviewLikes where ReviewLikes.userId=? and Review.id=ReviewLikes.reviewId))'likeCheck'\n" +
                "    from Users\n" +
                "    join Review on Review.userId=Users.id\n" +
                "    join Stores on Stores.id = Review.storeId  " +
                "    where isHolic='True' and evaluation IN(%s) %s" +
                "   order by Review.createdAt desc ",inSql,regionSql);
        String getImgQuery="select Review.id as 'ReviewId',imgUrl from Stores\n" +
                "    join Review on Review.storeId=Stores.id\n" +
                "    left join ReviewImg on ReviewImg.reviewId=Review.id " +
                "    join Users on Review.userId=Users.id " +
                "where Review.id=? and isHolic='TRUE' order by Review.createdAt ";
        Object[] getNewsParams=new Object[]{
                userId,userId,userId
        };

        return this.jdbcTemplate.query(getNewsQuery,
                (rs,rowNum)->new GetNewsRes(
                        rs.getLong("reviewId"),
                        rs.getString("profileImgUrl"),
                        rs.getString("name"),
                        rs.getString("isHolic"),
                        rs.getInt("reviewCount"),
                        rs.getInt("followCount"),
                        rs.getString("evaluation"),
                        rs.getLong("storeId"),
                        rs.getString("storeName"),
                        rs.getString("review"),
                        rs.getString("reviewCreated"),
                        rs.getInt("reviewLikes"),
                        rs.getInt("reviewComments"),
                        rs.getInt("wishCheck"),
                        rs.getInt("visitedCheck"),
                        rs.getInt("likeCheck"),
                        ImgList=this.jdbcTemplate.query(getImgQuery,
                                (rk,rownum)->new GetImgRes(
                                        rk.getLong("reviewId"),
                                        rk.getString("imgUrl")
                                ),rs.getLong("reviewId"))
                ),getNewsParams);

    }


}
