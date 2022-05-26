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
}
