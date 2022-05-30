package com.example.demo.src.review;

import com.example.demo.src.review.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
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

    public GetReviewRes getReview(Long reviewId, Long userId) {
        String getReviewDetailQuery = "SELECT U.id AS userId, U.profileImgUrl, U.name AS userName, U.isHolic,\n" +
                "                (select count(Review.id) from Review where Review.userId = R.userId) as userReviewCount,\n" +
                "                (select count(Following.id) from Following where Following.follwedUserId = R.userId) as UserFollowCount,\n" +
                "                R.evaluation, S.id AS storeId, S.name AS storeName, R.review, R.updatedAt,\n" +
                "                (SELECT COUNT(DISTINCT L.id) FROM ReviewLikes L, Review R WHERE L.reviewId = R.id) AS likeCount,\n" +
                "                (SELECT COUNT(DISTINCT C.id) FROM ReviewComments C, Review R WHERE C.reviewId = R.id) AS commentCount,\n" +
                "                (select exists(select Wishes.id from Wishes, Users U2 where Wishes.userId=U2.id && U2.id = ? && Wishes.storeId=R.storeId))'wishCheck',\n" +
                "                (select exists(select Visited.id from Visited, Users U2 where Visited.userId=U2.id && U2.id = ? && Visited.storeId=R.storeId))'visitedCheck',\n" +
                "                (select exists(select ReviewLikes.id from ReviewLikes, Users U2 where ReviewLikes.userId=U2.id and R.id=ReviewLikes.reviewId))'likeCheck'\n" +
                "                FROM Review R, Stores S, Users U\n" +
                "                WHERE  R.id = ? && R.userId = U.id && R.storeId = S.id ";
        String getReviewImgQuery = "SELECT I.imgUrl\n" +
                "FROM ReviewImg I\n" +
                "WHERE I.reviewId = ?";
        String getReviewCommentQuery = "SELECT U.id AS userId, U.profileImgUrl, U.name AS userName, U.isHolic,\n" +
                "       U2.name AS tagUserName, C.comment, C.updatedAt\n" +
                "FROM Users U, ReviewComments C\n" +
                "LEFT JOIN Users U2 ON U2.id = C.tagUserId\n" +
                "WHERE C.userId = U.id && C.reviewId = ?";
        Object[] getReviewDetailParams=new Object[]{userId, userId, reviewId};
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
                                rs.getInt("commentCount"),
                                rs.getInt("wishCheck"),
                                rs.getInt("visitedCheck"),
                                rs.getInt("likeCheck")
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

    public List<GetReviewStoreRes> getReviewStore(String keyWord) {
        String getReviewStoreQuery="select id as 'storeId',name,subRegion from Stores where name like ?";
        String searchKeyword="%"+keyWord+"%";
        return this.jdbcTemplate.query(getReviewStoreQuery,
                (rs,rowNum)->new GetReviewStoreRes(
                        rs.getLong("storeId"),
                        rs.getString("name"),
                        rs.getString("subRegion")
        ),searchKeyword);
    }

    public int createReviewLike(Long reviewId, Long userId) {
        String createReviewLikeQuery="insert into ReviewLikes(reviewId,userId) values(?,?)";
        Object[] createReviewLikeParams=new Object[]{
                reviewId,userId
        };

        return this.jdbcTemplate.update(createReviewLikeQuery,createReviewLikeParams);
    }

    public int chckReviewLike(Long reviewId, Long userId) {
        String checkReviewLikeQuery="select exists(select userId from ReviewLikes where reviewId=? and userId=?)";
        Object[] checkReviewLikeParams=new Object[]{
                reviewId,userId
        };
        return this.jdbcTemplate.queryForObject(checkReviewLikeQuery,int.class,checkReviewLikeParams);
    }

    public int checkReviewExists(Long reviewId) {
        String checkReviewExists="select exists(select id from Review where id=?)";
        return this.jdbcTemplate.queryForObject(checkReviewExists,int.class,reviewId);

    }

    public int deleteReviewLike(Long reviewId, Long userId) {
        String deleteReviewLikeQuery="delete from ReviewLikes where reviewId =? and userId=?";
        Object[] checkReviewLikeParams=new Object[]{
                reviewId,userId
        };
        return this.jdbcTemplate.update(deleteReviewLikeQuery,checkReviewLikeParams);
    }

    public int createReviewComment(Long reviewId, Long userId, PostCommentReq postCommentReq) {
        String createReviewLikeQuery="insert into ReviewComments(reviewId,userId,comment,tagUserId) values(?,?,?,?)";
        Object[] createReviewLikeParams=new Object[]{
                reviewId,userId,postCommentReq.getComment(),postCommentReq.getTagUserId()
        };

        return this.jdbcTemplate.update(createReviewLikeQuery,createReviewLikeParams);
    }

    public int deleteReviewComment(Long commentId, Long userId) {
        String deleteCommentQuery="delete from ReviewComments where id=? and userId=? ";
        Object[] deleteCommentParams = new Object[]{
                commentId,userId
        };
        return this.jdbcTemplate.update(deleteCommentQuery,deleteCommentParams);
    }

    public int checkReviewCommentExists(Long commentId) {
        String checkReviewCommentExistsQuery="select exists(select id from ReviewComments where id=?)";
        return this.jdbcTemplate.queryForObject(checkReviewCommentExistsQuery,int.class,commentId);
    }

    public Long selectReviewId(Long commentId, Long userId) {
        String selectReviewIdQuery="select reviewId from ReviewComments where id=? and userId=? ";
        Object[] selectReviewIdParmas=new Object[]{
                commentId,userId
        };
        try {
            return this.jdbcTemplate.queryForObject(selectReviewIdQuery, Long.class, selectReviewIdParmas);
        }catch(EmptyResultDataAccessException e){
            return Long.valueOf(0);
        }
    }
    public Long selectUserId(Long commentId, Long reviewId) {
        String selectReviewIdQuery="select userId from ReviewComments where id=? and reviewId=? ";
        Object[] selectReviewIdParmas=new Object[]{
                commentId,reviewId
        };
        try {
            return this.jdbcTemplate.queryForObject(selectReviewIdQuery, Long.class, selectReviewIdParmas);
        }catch(EmptyResultDataAccessException e){
            return Long.valueOf(0);
        }
    }

    public int putReviewComment(Long commentId, Long userId, PutCommentsReq putCommentsReq) {
        String putReviewCommentQuery="update ReviewComments set comment=?,tagUserId=? where id=? and userId=?";
        Object[] putReviewCommentParams=new Object[]{
                putCommentsReq.getComment(),putCommentsReq.getTagUserId(),commentId,userId
        };

        return this.jdbcTemplate.update(putReviewCommentQuery,putReviewCommentParams);
    }


    public List<GetReviewLikeUserRes> getReviewLikesUser(Long reviewId, Long userId) {
        String getReviewLikesUserQuery="select Users.id as 'userId',profileImgUrl,Users.name,isHolic,\n" +
                "       (select count(userId) from Review join Users on Users.id=Review.userId where Review.userId=ReviewLikes.userId)'reviewCount',\n" +
                "       (select count(follwedUserId) from Following where follwedUserId=ReviewLikes.userId)'followCount',\n" +
                "       (select exists(select Following.id from Following where userId=? and ReviewLikes.userId=follwedUserId ))'followCheck'\n" +
                "    from Users,Stores\n" +
                "    join Review on storeId=Stores.id\n" +
                "    join ReviewLikes on ReviewLikes.reviewId = Review.id\n" +
                "where Review.id=? and ReviewLikes.userId=Users.id";
        Object[] getReviewLikesUserParams=new Object[]{
                userId,reviewId
        };
        return this.jdbcTemplate.query(getReviewLikesUserQuery,
                (rs,rowNum)->new GetReviewLikeUserRes(
                        rs.getLong("userId"),
                        rs.getString("profileImgUrl"),
                        rs.getString("name"),
                        rs.getString("isHolic"),
                        rs.getInt("reviewCount"),
                        rs.getInt("followCount"),
                        rs.getInt("followCheck")

                ),getReviewLikesUserParams
        );
    }

    public int checkCreateUser(Long reviewId) {
        String checkCreateUserQuery="select userId from Review where id = ? ";
        return this.jdbcTemplate.queryForObject(checkCreateUserQuery,int.class,reviewId);
    }

    public void deleteReview(Long reviewId) {
        String deleteVisitedQuery = "delete from Review where id = ?";
        this.jdbcTemplate.update(deleteVisitedQuery, reviewId);
    }

    public void deleteAllImg(Long reviewId) {
        String deleteAllCommentsQuery="delete from ReviewImg where reviewId = ?";
        this.jdbcTemplate.update(deleteAllCommentsQuery, reviewId);
    }

    public void deleteAllComments(Long reviewId) {
        String deleteAllCommentsQuery="delete from ReviewComments where reviewId = ?";
        this.jdbcTemplate.update(deleteAllCommentsQuery, reviewId);
    }

    public void deleteAllLikes(Long reviewId) {
        String deleteAllLikesQuery="delete from ReviewLikes where reviewId = ?";
        this.jdbcTemplate.update(deleteAllLikesQuery, reviewId);
    }
}

