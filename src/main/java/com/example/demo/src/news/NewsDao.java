package com.example.demo.src.news;

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

    public List<GetNewsRes> getNews(List<String> evaluation) {
        String inSql=String.join(",",evaluation.stream().map(evaluationFilter->"'"+evaluationFilter+"'").collect(Collectors.toList()));
        String getNewsQuery=String.format("select Users.profileImgUrl,Users.name,isHolic,(select count(Re.review) from Review Re where Re.userId=Users.id)'reviewCount',\n" +
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
                "                                        end end as reviewCreated,(select GROUP_CONCAT(ReviewImg.imgUrl) from ReviewImg left join Review ImgByReview on ReviewImg.reviewId=ImgByReview.id where ImgByReview.id=Review.id)'imgUrl',\n" +
                "       (select count(*) from ReviewLikes where ReviewLikes.reviewId= Review.id)'reviewLikes',\n" +
                "        (select count(*) from ReviewComments where ReviewComments.reviewId=Review.id)'reviewComments'\n" +
                "    from Users\n" +
                "    join Review on Review.userId=Users.id\n" +
                "    join Stores on Stores.id = Review.storeId where evaluation IN(%s) order by Review.createdAt desc  ",inSql);

        return this.jdbcTemplate.query(getNewsQuery,
                (rs,rowNum)->new GetNewsRes(
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
                        rs.getInt("reviewComments")
                ));
    }
}
