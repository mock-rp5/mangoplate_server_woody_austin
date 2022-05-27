package com.example.demo.src.user;


import com.example.demo.src.user.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class UserDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public List<GetUserRes> getUsers(){

        String getUsersQuery = "select * from Users";

        return this.jdbcTemplate.query(getUsersQuery,
                (rs,rowNum) -> new GetUserRes(
                        rs.getInt("userIdx"),
                        rs.getString("userName"),
                        rs.getString("ID"),
                        rs.getString("Email"),
                        rs.getString("password"))
                );
    }

    public List<GetUserRes> getUsersByEmail(String email){

        String getUsersByEmailQuery = "select * from Users where email =?";

        String getUsersByEmailParams = email;
        return this.jdbcTemplate.query(getUsersByEmailQuery,
                (rs, rowNum) -> new GetUserRes(
                        rs.getInt("userIdx"),
                        rs.getString("userName"),
                        rs.getString("ID"),
                        rs.getString("Email"),
                        rs.getString("password")),
                getUsersByEmailParams);
    }

    public int updateUserLocation(PatchUserLocationReq patchUserLocationReq, Long userId) {
        String updateUserLocationQuery = "UPDATE Users SET latitude = ?, longitude = ? WHERE id = ?";
        Object[] updateParams = new Object[]{patchUserLocationReq.getLatitude(), patchUserLocationReq.getLongitude(), userId};
        return this.jdbcTemplate.update(updateUserLocationQuery,updateParams);
    }

    public int postUserKakao(PostUserKakaoLoginReq postUserKakaoLoginReq) {
        String postUserKakaoQuery="insert into KakaoUsers(kakaoName, kakaoId, kakaoEmail) values(?,?,?)";
        Object[] postUserKakaoParams=new Object[]{
                postUserKakaoLoginReq.getKakaoName(), postUserKakaoLoginReq.getKakaoId(), postUserKakaoLoginReq.getKakaoEmail()
        };

        return this.jdbcTemplate.update(postUserKakaoQuery,postUserKakaoParams);
    }
    public int getUserKakaoExists(String email) {
        String getUserKakaoexistsQuery="select exists(select kakaoId from KakaoUsers where kakaoEmail=?)";
        String getUserKakaoexistsParams=email;
        return this.jdbcTemplate.queryForObject(getUserKakaoexistsQuery,int.class,getUserKakaoexistsParams);
    }
    

    public Long createUser(PostUserReq postUserReq){
        String createUserQuery = "insert into Users (name, email, password, phoneNumber, profileImgUrl, agreeLocation, agreeMarketing) VALUES (?,?,?,?,?,?,?)";
        Object[] createUserParams = new Object[]{postUserReq.getUserName(), postUserReq.getEmail(), postUserReq.getPassword(),
                postUserReq.getPhoneNumber(), postUserReq.getImageUrl(), postUserReq.getAgreeLocation(), postUserReq.getAgreeMarketing()};
        this.jdbcTemplate.update(createUserQuery, createUserParams);
        String lastInsertIdQuery = "select last_insert_id()";
        return this.jdbcTemplate.queryForObject(lastInsertIdQuery,Long.class);
    }

    public int checkEmail(String email){
        String checkEmailQuery = "select exists(select email from Users where email = ?)";
        String checkEmailParams = email;
        return this.jdbcTemplate.queryForObject(checkEmailQuery,
                int.class,
                checkEmailParams);

    }


    public Long getIdByEmail(String email){
        String getUserIdxByEmailQuery="select id from Users where email=?";
        String getUserIdxByEmailParams=email;
        return this.jdbcTemplate.queryForObject(getUserIdxByEmailQuery,Long.class,getUserIdxByEmailParams);
    }


    public User getPwd(PostLoginReq postLoginReq){

        String getPwdQuery = "select id, name, email, password, phoneNumber from Users where email = ?";
        String getPwdParams = postLoginReq.getEmail();

        return this.jdbcTemplate.queryForObject(getPwdQuery,
                (rs,rowNum)-> new User(
                        rs.getLong("id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("password"),
                        rs.getString("phoneNumber")
                ),
                getPwdParams
                );
    }


    public Long createUserByKakao(PostUserKakaoReq postUserKakaoReq) {
        String createUserByKakaoQuery = "insert into Users(name,email) values(?,?)";
        Object[] createUserByKakaoParams=new Object[]{
                postUserKakaoReq.getName(),postUserKakaoReq.getEmail()
        };

        this.jdbcTemplate.update(createUserByKakaoQuery,createUserByKakaoParams);

        String lastInsertIdQuery="select last_insert_id()";
        return this.jdbcTemplate.queryForObject(lastInsertIdQuery,Long.class);
    }

    public int createUserFollow(GetUserFollowReq getUserFollowReq) {
        String createUserFollowQuery="insert into Following(userId,follwedUserId) values(?,?)";
        Object[] createUserFollowParams = new Object[]{
                getUserFollowReq.getUserId(),getUserFollowReq.getFollowedUserId()
        };

        return this.jdbcTemplate.update(createUserFollowQuery,createUserFollowParams);
    }

    public int checkFollowExist(GetUserFollowReq getUserFollowReq) {
        String checkReviewExistQuery="select exists (select id from Following where userId=? and follwedUserId=?)";
        Object[] checkFollowExistParams = new Object[]{
                getUserFollowReq.getUserId(),getUserFollowReq.getFollowedUserId()
        };

        return this.jdbcTemplate.queryForObject(checkReviewExistQuery,int.class,checkFollowExistParams);
    }

    public int checkUserExist(Long followedUserId) {
        String checkUserExistQuery="select exists (select id from Users where id = ?)";
        return this.jdbcTemplate.queryForObject(checkUserExistQuery,int.class,followedUserId);
    }

    public int checkFollowExistToUnFollow(DeleteUserFollowReq deleteUserFollowReq) {
        String checkReviewExistQuery="select exists (select id from Following where userId=? and follwedUserId=?)";
        Object[] checkFollowExistParams = new Object[]{
                deleteUserFollowReq.getUserId(),deleteUserFollowReq.getFollowedUserId()
        };

        return this.jdbcTemplate.queryForObject(checkReviewExistQuery,int.class,checkFollowExistParams);
    }

    public int userUnFollow(DeleteUserFollowReq deleteUserFollowReq) {
        String deleteUserFollowReqQuery="delete from Following where userId=? and follwedUserId=?";
        Object[] deleteUserFollowReqParams = new Object[]{
                deleteUserFollowReq.getUserId(),deleteUserFollowReq.getFollowedUserId()
        };

        return this.jdbcTemplate.update(deleteUserFollowReqQuery,deleteUserFollowReqParams);
    }

    public List<GetUserFollowerListRes> getUserFollower(GetUserFollowListReq getUserFollowReq) {
        String getUserFollowerQuery="select Users.id as 'userId',profileImgUrl,name,isHolic,\n" +
                "       (select count(userId) from Review join Users on Users.id=Review.userId where Review.userId=Following.userId)'reviewCount',\n" +
                "        (select count(follwedUserId) from Following where Following.follwedUserId=Users.id)'followerCount',\n" +
                "        (select exists(select Following.id from Following where userId=? and follwedUserId=Users.id ))'followCheck'\n" +
                "from Users join Following on Users.id=Following.userId\n" +
                "where follwedUserId=? limit ?,10";
        Object[] getUserFollowerParams = new Object[]{
                getUserFollowReq.getUserId(),getUserFollowReq.getFollowedUserId(),(getUserFollowReq.getPage()-1)*20
        };

        return this.jdbcTemplate.query(getUserFollowerQuery,
                (rs,rowNum)->new GetUserFollowerListRes(
                        rs.getLong("userId"),
                        rs.getString("profileImgUrl"),
                        rs.getString("name"),
                        rs.getString("isHolic"),
                        rs.getInt("reviewCount"),
                        rs.getInt("followerCount"),
                        rs.getInt("followCheck")
                ),getUserFollowerParams
        );

    }

    public List<GetUserFollowerListRes> getUserFollowing(GetUserFollowListReq getUserFollowReq) {
        String getUserFollowingQuery ="select Users.id as 'userId', profileImgUrl,name,isHolic,\n" +
                "       (select count(userId) from Review join Users on Users.id=Review.userId where Review.userId=Following.follwedUserId)'reviewCount',\n" +
                "       (select count(follwedUserId) from Following where Following.userId=Users.id)'followerCount',\n" +
                "       (select exists(select Following.id from Following where userId=? and follwedUserId=Users.id ))'followCheck'\n" +
                "from Users join Following on Users.id=Following.follwedUserId where Following.userId=? limit ?,10";
        Object[] getUserFollowingParams = new Object[]{
                getUserFollowReq.getUserId(),getUserFollowReq.getFollowedUserId(),(getUserFollowReq.getPage()-1)*20
        };

        return this.jdbcTemplate.query(getUserFollowingQuery,
                (rs,rowNum)->new GetUserFollowerListRes(
                        rs.getLong("userId"),
                        rs.getString("profileImgUrl"),
                        rs.getString("name"),
                        rs.getString("isHolic"),
                        rs.getInt("reviewCount"),
                        rs.getInt("followerCount"),
                        rs.getInt("followCheck")
                ),getUserFollowingParams
        );

    }

    public List<GetUserProfileRes> getUserProfile(GetUserProfileReq getUserProfileReq) {
        String getUserProfileQuery="select Users.id as 'userId',profileImgUrl,count(Following.id)'followerCount',\n" +
                "       (select count(Following.id) from Following where Following.userId=Users.id)as'followingCount',\n" +
                "       (select exists(select Following.id from Following where Following.userId=? and Following.follwedUserId=Users.id))as'followCheck',name,isHolic,\n" +
                "       (select count(Review.id)from Review where Review.userId=Users.id)as 'reviewCount',\n" +
                "       (select count(Visited.id) from Visited where Visited.userId=Users.id)as 'visitedCount',\n" +
                "       (select count(ReviewImg.id)from ReviewImg join Review on Review.id=reviewId where Review.userId=Users.id) as 'imgCount',\n" +
                "       (select count(Wishes.id) from Wishes where Wishes.userId=Users.id) as 'wishesCount',\n" +
                "       (select count(Mylists.id) from Mylists where Mylists.userId=Users.id) as 'myListCount'\n" +
                "from Users join Following on Following.follwedUserId=Users.id where Users.id=?";
        Object[] getUserProfileParams = new Object[]{
                getUserProfileReq.getUserId(),getUserProfileReq.getProfileUserId()
        };

        return this.jdbcTemplate.query(getUserProfileQuery,
                (rs,rowNum) ->new GetUserProfileRes(
                        rs.getLong("userId"),
                        rs.getString("profileImgUrl"),
                        rs.getInt("followerCount"),
                        rs.getInt("followingCount"),
                        rs.getInt("followCheck"),
                        rs.getString("name"),
                        rs.getString("isHolic"),
                        rs.getInt("reviewCount"),
                        rs.getInt("visitedCount"),
                        rs.getInt("imgCount"),
                        rs.getInt("wishesCount"),
                        rs.getInt("myListCount")
                ),getUserProfileParams);
    }

    public int patchUserPhoneNumber(Long userId, PatchUserPhoneNumberReq patchUserPhoneNumberReq) {
        String patchUserPhoneNumberQuery="update Users set phoneNumber=? where id=?";
        Object[] params = new Object[]{
            patchUserPhoneNumberReq.getPhoneNumber(),userId
        };
        return this.jdbcTemplate.update(patchUserPhoneNumberQuery,params);
    }

    public int patchUserEmail(Long userId, PatchUserEmailReq patchUserEmailReq) {
        String patchUserEmailQuery="update Users set email=? where id=?";
        Object[] params = new Object[]{
            patchUserEmailReq.getEmail(),userId
        };
        return this.jdbcTemplate.update(patchUserEmailQuery,params);
    }

    public int patchUserName(Long userId, PatchUserNameReq patchUserNameReq) {
        String patchUserNameQuery="update Users set name=? where id=?";
        Object[] params = new Object[]{
                patchUserNameReq.getName(),userId

        };
        return this.jdbcTemplate.update(patchUserNameQuery,params);
    }

    public int patchUserProfileImg(Long userId, PatchUserProfileImgReq patchUserProfileImgReq) {
        String patchUserProfileImgQuery="update Users set profileImgUrl=? where id=?";
        Object[] params = new Object[]{
           patchUserProfileImgReq.getProfileImgUrl(),userId
        };
        return this.jdbcTemplate.update(patchUserProfileImgQuery,params);
    }
}
