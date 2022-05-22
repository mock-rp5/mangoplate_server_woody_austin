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
<<<<<<< HEAD
        String getUsersQuery = "select * from Users";
=======
        String getUsersQuery = "select * from UserInfo";
>>>>>>> woody
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
<<<<<<< HEAD
        String getUsersByEmailQuery = "select * from Users where email =?";
=======
        String getUsersByEmailQuery = "select * from UserInfo where email =?";
>>>>>>> woody
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

<<<<<<< HEAD
    /*
    public GetUserRes getUser(int userIdx){
        String getUserQuery = "select * from Users where userIdx = ?";
=======
    public GetUserRes getUser(int userIdx){
        String getUserQuery = "select * from UserInfo where userIdx = ?";
>>>>>>> woody
        int getUserParams = userIdx;
        return this.jdbcTemplate.queryForObject(getUserQuery,
                (rs, rowNum) -> new GetUserRes(
                        rs.getInt("userIdx"),
                        rs.getString("userName"),
                        rs.getString("ID"),
                        rs.getString("Email"),
                        rs.getString("password")),
                getUserParams);
    }
<<<<<<< HEAD

     */
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
        String createUserQuery = "insert into Users (name, ID, password, email) VALUES (?,?,?,?)";
        Object[] createUserParams = new Object[]{postUserReq.getUserName(), postUserReq.getId(), postUserReq.getPassword(), postUserReq.getEmail()};
        this.jdbcTemplate.update(createUserQuery, createUserParams);

        String lastInserIdQuery = "select last_insert_id()";
        return this.jdbcTemplate.queryForObject(lastInserIdQuery,Long.class);
    }


=======
    

    public int createUser(PostUserReq postUserReq){
        System.out.println("aaa");
        String createUserQuery = "insert into Users (name, email, password, phoneNumber, profileImgUrl, agreeLocation, agreeMarketing) VALUES (?,?,?,?,?,?,?)";
        Object[] createUserParams = new Object[]{postUserReq.getUserName(), postUserReq.getEmail(), postUserReq.getPassword(),
                postUserReq.getPhoneNumber(), postUserReq.getImageUrl(), postUserReq.getAgreeLocation(), postUserReq.getAgreeMarketing()};
        this.jdbcTemplate.update(createUserQuery, createUserParams);
        String lastInsertIdQuery = "select last_insert_id()";
        return this.jdbcTemplate.queryForObject(lastInsertIdQuery,int.class);
    }

>>>>>>> woody
    public int checkEmail(String email){
        String checkEmailQuery = "select exists(select email from Users where email = ?)";
        String checkEmailParams = email;
        return this.jdbcTemplate.queryForObject(checkEmailQuery,
                int.class,
                checkEmailParams);

    }

<<<<<<< HEAD

    public Long getIdByEmail(String email){
        String getUserIdxByEmailQuery="select id from Users where email=?";
        String getUserIdxByEmailParams=email;
        return this.jdbcTemplate.queryForObject(getUserIdxByEmailQuery,Long.class,getUserIdxByEmailParams);
    }

    /*
    public int modifyUserName(PatchUserReq patchUserReq){
        String modifyUserNameQuery = "update Users set userName = ? where userIdx = ? ";
=======
    public int modifyUserName(PatchUserReq patchUserReq){
        String modifyUserNameQuery = "update UserInfo set userName = ? where userIdx = ? ";
>>>>>>> woody
        Object[] modifyUserNameParams = new Object[]{patchUserReq.getUserName(), patchUserReq.getUserIdx()};

        return this.jdbcTemplate.update(modifyUserNameQuery,modifyUserNameParams);
    }

    public User getPwd(PostLoginReq postLoginReq){
<<<<<<< HEAD
        String getPwdQuery = "select userIdx, password,email,userName,ID from UserInfo where ID = ?";
        String getPwdParams = postLoginReq.getId();

        return this.jdbcTemplate.queryForObject(getPwdQuery,
                (rs,rowNum)-> new User(
                        rs.getLong("userIdx"),
                        rs.getString("ID"),
                        rs.getString("userName"),
                        rs.getString("password"),
                        rs.getString("email")
=======
        String getPwdQuery = "select id, name, email, password, phoneNumber from Users where email = ?";
        String getPwdParams = postLoginReq.getEmail();

        return this.jdbcTemplate.queryForObject(getPwdQuery,
                (rs,rowNum)-> new User(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("password"),
                        rs.getString("phoneNumber")
>>>>>>> woody
                ),
                getPwdParams
                );

    }

<<<<<<< HEAD
     */


    public Long createUserByKakao(PostUserKakaoReq postUserKakaoReq) {
        String createUserByKakaoQuery = "insert into Users(name,email) values(?,?)";
        Object[] createUserByKakaoParams=new Object[]{
                postUserKakaoReq.getName(),postUserKakaoReq.getEmail()
        };

        this.jdbcTemplate.update(createUserByKakaoQuery,createUserByKakaoParams);

        String lastInsertIdQuery="select last_insert_id()";
        return this.jdbcTemplate.queryForObject(lastInsertIdQuery,Long.class);
    }
=======

>>>>>>> woody
}
