package com.example.demo.src.visited;

import com.example.demo.src.review.model.*;
import com.example.demo.src.visited.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public class VisitedDao {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public void createVisited(PostVisitedReq postVisitedReq) {
        String createVisitedQuery="insert into Visited(storeId,userId,isPublic,description) values(?,?,?,?)";
        Object[] createVisitedParam=new Object[]{
                postVisitedReq.getStoreId(),postVisitedReq.getUserId(),postVisitedReq.getIsPublic(),postVisitedReq.getDescription()};

        this.jdbcTemplate.update(createVisitedQuery,createVisitedParam);
    }

    public int checkDate(Long storeId, Long userId) {
        String checkDateQuery = "select exists(select Visited.id from Visited where storeId=? && userId=? && createdAt > DATE_ADD(?, INTERVAL -1 DAY))";
        return this.jdbcTemplate.queryForObject(checkDateQuery, int.class, storeId, userId, LocalDateTime.now());
    }

    public int checkVisitedId(Long visitedId) {
        String checkVisitedQuery="select exists(select Visited.id from Visited where id = ?)";
        return this.jdbcTemplate.queryForObject(checkVisitedQuery,int.class,visitedId);
    }

    public int checkCreateUser(Long visitedId) {
        String checkVisitedQuery="select userId from Visited where id = ? ";
        return this.jdbcTemplate.queryForObject(checkVisitedQuery,int.class,visitedId);
    }

    public void deleteVisited(Long visitedId) {
        String deleteVisitedQuery = "delete from Visited where id = ?";
        this.jdbcTemplate.update(deleteVisitedQuery,visitedId);
    }

    public void deleteAllComments(Long visitedId) {
        String deleteAllCommentsQuery="delete from VisitedComments where visitedId = ?";
        this.jdbcTemplate.update(deleteAllCommentsQuery, visitedId);
    }

    public void deleteAllLikes(Long visitedId) {
        String deleteAllLikesQuery="delete from VisitedLikes where visitedId = ?";
        this.jdbcTemplate.update(deleteAllLikesQuery, visitedId);
    }

    public void modifyVisited(Long visitedId, PatchVisitedReq patchVisitedReq) {
        String modifyVisitedQuery = "update Visited set isPublic = ?, description = ? where id = ?";
        this.jdbcTemplate.update(modifyVisitedQuery,patchVisitedReq.getIsPublic(), patchVisitedReq.getDescription(),visitedId);
    }

    public GetVisitedRes getVisited(Long visitedId, Long userId) {
        String getVisitedDetailQuery = "SELECT V.id AS visitedId, U.id AS userId, U.profileImgUrl, U.name AS userName, U.isHolic,\n" +
                "(select count(Review.id) from Review where Review.userId = V.userId) as userReviewCount,\n" +
                "(select count(Following.id) from Following where Following.follwedUserId = V.userId) as UserFollowCount,\n" +
                "S.id AS storeId, S.name AS storeName,(select ReviewImgSelect.imgurl from ReviewImg ReviewImgSelect\n" +
                "left join Review on Review.id=reviewId where ReviewImgSelect.reviewId=Review.id and V.storeId=Review.storeId limit 1)as 'storeImgUrl',\n" +
                "S.subRegion, V.description, S.foodCategory, S.viewCount AS storeViewCount, (SELECT count(Review.id) FROM Review WHERE Review.storeId=V.storeId) AS storeReviewCount, V.updatedAt,\n" +
                "(SELECT COUNT(DISTINCT L.id) FROM VisitedLikes L, Visited V WHERE L.visitedId = V.id) AS likeCount,\n" +
                "(SELECT COUNT(DISTINCT C.id) FROM VisitedComments C, Visited V WHERE C.visitedId = V.id) AS commentCount,\n" +
                "(select exists(select Wishes.id from Wishes, Users U2 where Wishes.userId=U2.id && U2.id = ? && Wishes.storeId=V.storeId))'wishCheck',\n" +
                " (select exists(select Visited.id from Visited, Users U2 where Visited.userId=U2.id  && U2.id = ? && Visited.storeId=V.storeId))'visitedCheck',\n" +
                "(select exists(select ReviewLikes.id from ReviewLikes, Users U2 where ReviewLikes.userId=U2.id and V.id=ReviewLikes.reviewId))'likeCheck'\n" +
                "FROM Visited V, Users U, Stores S\n" +
                "WHERE  V.id = ? && V.userId = U.id && V.storeId = S.id ";
        String getVisitedCommentsQuery = "SELECT C.id AS commentId, U.id AS userId, U.profileImgUrl, U.name AS userName, U.isHolic,\n" +
                "                       U2.name AS tagUserName, C.comment, C.updatedAt\n" +
                "                FROM Users U, VisitedComments C\n" +
                "                LEFT JOIN Users U2 ON U2.id = C.tagUserId\n" +
                "                WHERE C.userId = U.id && C.visitedId = ?";

        GetVisitedDetailRes getVisitedDetailRes;
        List<GetVisitedCommentsRes> getVisitedCommentsResList;
        return new GetVisitedRes(
                getVisitedDetailRes = this.jdbcTemplate.queryForObject(getVisitedDetailQuery,
                        (rs,rowNum) -> new GetVisitedDetailRes(
                                rs.getLong("visitedId"),
                                rs.getLong("userId"),
                                rs.getString("profileImgUrl"),
                                rs.getString("userName"),
                                rs.getString("isHolic"),
                                rs.getInt("userReviewCount"),
                                rs.getInt("userFollowCount"),
                                rs.getLong("storeId"),
                                rs.getString("storeName"),
                                rs.getString("storeImgUrl"),
                                rs.getString("subRegion"),
                                rs.getString("description"),
                                rs.getString("foodCategory"),
                                rs.getInt("storeViewCount"),
                                rs.getInt("storeReviewCount"),
                                rs.getString("updatedAt"),
                                rs.getInt("likeCount"),
                                rs.getInt("commentCount"),
                                rs.getInt("wishCheck"),
                                rs.getInt("visitedCheck"),
                                rs.getInt("likeCheck")
                        ), userId, userId, visitedId),
                getVisitedCommentsResList = this.jdbcTemplate.query(getVisitedCommentsQuery,
                        (rs,rowNum) -> new GetVisitedCommentsRes(
                                rs.getLong("commentId"),
                                rs.getLong("userId"),
                                rs.getString("profileImgUrl"),
                                rs.getString("userName"),
                                rs.getString("isHolic"),
                                rs.getString("tagUserName"),
                                rs.getString("comment"),
                                rs.getString("updatedAt")
                        ), visitedId)
        );
    }

    public int checkVisitedLike(Long visitedId, Long userId) {
        String checkVisitedLikeQuery="select exists(select id from VisitedLikes where visitedId=? and userId=?)";
        return this.jdbcTemplate.queryForObject(checkVisitedLikeQuery,int.class, visitedId, userId);
    }

    public void createVisitedLike(Long visitedId, Long userId) {
        String createVisitedLikeQuery="insert into VisitedLikes(visitedId,userId) values(?,?)";
        this.jdbcTemplate.update(createVisitedLikeQuery, visitedId, userId);
    }

    public int checkTagUserId(Long userId) {
        String checkTagUserIdQuery = "select exists(select id from Users where id = ?)";
        return this.jdbcTemplate.queryForObject(checkTagUserIdQuery,int.class,userId);
    }

    public int deleteVisitedLike(Long visitedId, Long userId) {
        String createVisitedLikeQuery="delete from VisitedLikes where visitedId = ? && userId = ? ";
        return this.jdbcTemplate.update(createVisitedLikeQuery, visitedId, userId);
    }

    public void createVisitedComment(PostVisitedCommentsReq postVisitedCommentsReq) {
        String postCommentQuery="insert into VisitedComments(visitedId, userId, tagUserId, comment) values(?,?,?,?)";
        Object[] postCommentParams = new Object[] {
                postVisitedCommentsReq.getVisitedId(), postVisitedCommentsReq.getUserId(),
                postVisitedCommentsReq.getTagUserId(), postVisitedCommentsReq.getComment()
        };
        this.jdbcTemplate.update(postCommentQuery,postCommentParams);
    }

    public int checkCommentId(Long commentId) {
        String checkCommentIdQuery = "select exists(select id from VisitedComments where id = ?)";
        return this.jdbcTemplate.queryForObject(checkCommentIdQuery,int.class,commentId);
    }

    public int checkCommentCreateUser(Long commentId) {
        String checkCommentCreateUserQuery="select userId from VisitedComments where id = ? ";
        return this.jdbcTemplate.queryForObject(checkCommentCreateUserQuery,int.class,commentId);
    }

    public void deleteVisitedComment(Long commentId) {
        String deleteVisitedCommentQuery="delete from VisitedComments where id = ?";
        this.jdbcTemplate.update(deleteVisitedCommentQuery, commentId);
    }

    public void modifyVisitedComment(Long commentId, PatchVisitedCommentsReq patchVisitedCommentsReq) {
        String modifyVisitedCommentQuery = "update VisitedComments set tagUserId = ?, comment = ? where id = ?";
        this.jdbcTemplate.update(modifyVisitedCommentQuery,patchVisitedCommentsReq.getTagUserId(), patchVisitedCommentsReq.getComment(), commentId);
    }

    public List<GetVisitedLikeUserRes> getVisitedLikeUser(Long visitedId, Long userId) {
        String getVisitedLikesUserQuery="select Users.id as 'userId', profileImgUrl, Users.name as userName, isHolic,\n" +
                "       (select count(userId) from Review join Users on Users.id=Review.userId where Review.userId=VisitedLikes.userId)'reviewCount',\n" +
                "       (select count(follwedUserId) from Following where follwedUserId=VisitedLikes.userId)'followerCount',\n" +
                "       (select exists(select Following.id from Following where userId=? and VisitedLikes.userId=follwedUserId ))'followCheck'\n" +
                "    from Users, Visited, VisitedLikes\n" +
                "where Visited.id=? && Visited.id = VisitedLikes.visitedId && VisitedLikes.userId=Users.id";

        return this.jdbcTemplate.query(getVisitedLikesUserQuery,
                (rs,rowNum)->new GetVisitedLikeUserRes(
                        rs.getLong("userId"),
                        rs.getString("profileImgUrl"),
                        rs.getString("userName"),
                        rs.getString("isHolic"),
                        rs.getInt("reviewCount"),
                        rs.getInt("followerCount"),
                        rs.getInt("followCheck")
                ),userId, visitedId);
    }


}
