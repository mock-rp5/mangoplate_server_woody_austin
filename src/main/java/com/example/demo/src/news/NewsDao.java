package com.example.demo.src.news;

import com.example.demo.src.news.model.GetNewsByFollowingReq;
import com.example.demo.src.news.model.GetNewsImgRes;
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

    @Autowired
    public void setDataSource(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public List<GetNewsRes> getNews(List<String> evaluation,int page) {
        String inSql = String.join(",", evaluation.stream().map(evaluationFilter -> "'" + evaluationFilter + "'").collect(Collectors.toList()));
        char quotes='"';
        String getNewsQuery = String.format("select Review.id as 'reviewId',Users.profileImgUrl,Users.name,isHolic,(select count(Re.review) from Review Re where Re.userId=Users.id)'reviewCount',\n" +
                "       (select count(follwedUserId) from Following)'followCount',evaluation,\n" +
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
                "       (select exists(select Wishes.id from Wishes where Wishes.userId=4 and Wishes.storeId=Stores.id))'wishCheck'\n" +
                "       ,(select exists(select ReviewLikes.id from ReviewLikes where ReviewLikes.userId=4 and Review.id=ReviewLikes.reviewId))'likeCheck'\n" +
                "    from Users\n" +
                "    join Review on Review.userId=Users.id\n" +
                "    join Stores on Stores.id = Review.storeId where evaluation IN(%s) order by Review.createdAt desc limit ?,10  ", inSql);
        int param=(page-1)*10;
        List<GetNewsRes> getNewsRes;
        return getNewsRes=this.jdbcTemplate.query(getNewsQuery,
                (rs, rowNum) -> new GetNewsRes(
                        rs.getLong("reviewId"),
                        rs.getString("profileImgUrl"),
                        rs.getString("name"),
                        rs.getString("isHolic"),
                        rs.getInt("reviewCount"),
                        rs.getInt("followCount"),
                        rs.getString("evaluation"),
                        rs.getString("storeName"),
                        rs.getString("review"),
                        rs.getString("reviewCreated"),
                        rs.getString("imgUrl"),
                        rs.getInt("reviewLikes"),
                        rs.getInt("reviewComments"),
                        rs.getInt("wishCheck"),
                        rs.getInt("likeCheck")
                ),param
        );




    }

    public List<GetNewsRes> getNewsByFollowing(GetNewsByFollowingReq getNewsByFollowingReq) {
        String inSql=String.join(",",getNewsByFollowingReq.getEvaluation().stream().map(evaluationFilter->"'"+evaluationFilter+"'").collect(Collectors.toList()));
        String getNewsQuery=String.format("select Review.id as 'reviewId' ,Users.profileImgUrl,Users.name,isHolic,(select count(Re.review) from Review Re where Re.userId=Users.id)'reviewCount',\n" +
                "       (select count(follwedUserId) from Following)'followCount',evaluation,\n" +
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
                "(select exists(select Wishes.id from Wishes where Wishes.userId=4 and Wishes.storeId=Stores.id))'wishCheck'\n" +
                ",(select exists(select ReviewLikes.id from ReviewLikes where ReviewLikes.userId=4 and Review.id=ReviewLikes.reviewId))'likeCheck'\n" +
                "    from Users\n" +
                "    join Review on Review.userId=Users.id\n" +
                "    join Stores on Stores.id = Review.storeId  " +
                "    join Following on Users.id = Following.follwedUserId where Following.userid=? and evaluation IN(%s)" +
                "   order by Review.createdAt desc limit ?,10",inSql);
        Object[] getNewsParams= new Object[]{
                getNewsByFollowingReq.getUserid(),(getNewsByFollowingReq.getPage()-1)*10
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
                        rs.getString("storeName"),
                        rs.getString("review"),
                        rs.getString("reviewCreated"),
                        rs.getString("imgUrl"),
                        rs.getInt("reviewLikes"),
                        rs.getInt("reviewComments"),
                        rs.getInt("wishCheck"),
                        rs.getInt("likeCheck")
                ),getNewsParams);

    }

    public List<GetNewsImgRes> getNewsImg(Long[] reviewId) {
        System.out.println(reviewId.length);
        System.out.println(reviewId[1]);
        String getNewsImgQuery="select ReviewImg.imgUrl'reviewImg' from ReviewImg left join Review on reviewId=Review.id where Review.Id=?";

        List<GetNewsImgRes> getNewsImgRes;
        for(int i=0; i<reviewId.length;i++){
            return this.jdbcTemplate.query(getNewsImgQuery,
                    (rs,rowNum)->new GetNewsImgRes(
                            rs.getString("reviewImg")
                    ),reviewId[i]);
        }


        return null;
    }
}
