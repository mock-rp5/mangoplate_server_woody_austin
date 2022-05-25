package com.example.demo.src.review;

import com.example.demo.src.review.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class ReviewDao {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public int checkReviewId(Long reviewId){
        String checkReviewIdQuery = "select exists(select id from Review where id = ?)";
        Long checkReviewIdParams = reviewId;
        return this.jdbcTemplate.queryForObject(checkReviewIdQuery,
                int.class,
                checkReviewIdParams);
    }

    public GetReviewRes getReview(Long reviewId) {
        String getReviewDetailQuery = "SELECT U.id AS userId, U.profileImgUrl, U.name AS userName, U.isHolic, COUNT(DISTINCT R2.id) AS userReviewCount, COUNT(DISTINCT F.id) AS userFollowCount,\n" +
                "       R.evaluation, S.id AS storeId, S.name AS storeName, R.review, R.updatedAt,\n" +
                "       (SELECT COUNT(DISTINCT L.id) FROM ReviewLikes L, Review R WHERE L.reviewId = ?) AS likeCount,\n" +
                "       (SELECT COUNT(DISTINCT C.id) FROM ReviewComments C, Review R WHERE C.reviewId = ?) AS commentCount\n" +
                "FROM Review R, Users U, Stores S, Review R2, Following F\n" +
                "WHERE  R.id = ? && R.userId = U.id && R.storeId = S.id && R2.userId = U.id && F.follwedUserId = U.id";
        String getReviewImgQuery = "SELECT I.imgUrl\n" +
                "FROM ReviewImg I\n" +
                "WHERE I.reviewId = ?";
        String getReviewCommentQuery = "SELECT U.id AS userId, U.profileImgUrl, U.name AS userName, U.isHolic,\n" +
                "       U2.name AS tagUserName, C.comment, C.updatedAt\n" +
                "FROM Users U, ReviewComments C\n" +
                "LEFT JOIN Users U2 ON U2.id = C.tagUserId\n" +
                "WHERE C.userId = U.id && C.reviewId = ?";
        Object[] getReviewDetailParams=new Object[]{reviewId, reviewId, reviewId};
        Long param = reviewId;

        GetReviewDetailRes getReviewDetailRes;
        List<String> reviewImgList;
        List<GetCommentRes> getCommentRes;

        return new GetReviewRes(
                getReviewDetailRes = this.jdbcTemplate.queryForObject(getReviewDetailQuery,
                        (rs,rowNum) -> new GetReviewDetailRes(
                                rs.getLong("userId"),
                                rs.getString("profileImgUrl"),
                                rs.getString("userName"),
                                rs.getString("isHolic"),
                                rs.getInt("userReviewCount"),
                                rs.getInt("userFollowCount"),
                                rs.getString("evaluation"),
                                rs.getLong("storeId"),
                                rs.getString("storeName"),
                                rs.getString("review"),
                                rs.getString("updatedAt"),
                                rs.getInt("likeCount"),
                                rs.getInt("commentCount")
                        ), getReviewDetailParams),
                reviewImgList = this.jdbcTemplate.query(getReviewImgQuery,
                        (rs,rowNum) -> new String(
                                rs.getString("imgUrl")), param),
                getCommentRes = this.jdbcTemplate.query(getReviewCommentQuery,
                        (rs,rowNum) -> new GetCommentRes(
                                rs.getLong("userId"),
                                rs.getString("profileImgUrl"),
                                rs.getString("userName"),
                                rs.getString("isHolic"),
                                rs.getString("tagUserName"),
                                rs.getString("comment"),
                                rs.getString("updatedAt")
                        ), param)
        );
    }

    public Long createReview(PostReviewListReq postReviewListReq) {
        String createReviewQuery="insert into Review(storeId,userId,review,evaluation) values(?,?,?,?)";
        Object[] createReviewParam=new Object[]{
                postReviewListReq.getStoreId(),postReviewListReq.getUserId(),postReviewListReq.getReview(),postReviewListReq.getEvaluation()
        };

        this.jdbcTemplate.update(createReviewQuery,createReviewParam);

        String lastInsertIdQuery="select last_insert_id()";
        return this.jdbcTemplate.queryForObject(lastInsertIdQuery,Long.class);
    }

    public int createReviewImg(PostReviewImgReq postReviewImgReq) {
        String createReviewImgQuery="insert into ReviewImg(reviewId,ImgUrl) values(?,?)";
        Object[] createReviewImgParam=new Object[]{
                postReviewImgReq.getReviewId(),postReviewImgReq.getImgUrl()
        };
        return this.jdbcTemplate.update(createReviewImgQuery,createReviewImgParam);

    }
}

