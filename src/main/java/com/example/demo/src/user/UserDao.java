package com.example.demo.src.user;


import com.example.demo.src.news.model.GetImgRes;
import com.example.demo.src.user.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class UserDao {

    List<GetVisitedStoreRes> storeInfo;
    List<GetImgRes> ImgList;
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

    public int postUserKakao(PostUserKakaoLoginReq postUserKakaoLoginReq, Long userId) {
        String postUserKakaoQuery="insert into KakaoUsers(userId,kakaoName, kakaoId, kakaoEmail) values(?,?,?,?)";
        Object[] postUserKakaoParams=new Object[]{
                userId,postUserKakaoLoginReq.getKakaoName(), postUserKakaoLoginReq.getKakaoId(), postUserKakaoLoginReq.getKakaoEmail()
        };

        return this.jdbcTemplate.update(postUserKakaoQuery,postUserKakaoParams);
    }
    public int getUserKakaoExists(String email) {
        String getUserKakaoexistsQuery="select exists(select kakaoId from KakaoUsers where kakaoEmail=?)";
        return this.jdbcTemplate.queryForObject(getUserKakaoexistsQuery,int.class, email);
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
    public int checkKakaoEmail(String email){
        String checkEmailQuery = "select exists(select kakaoEmail from KakaoUsers where kakaoEmail = ?)";
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
                "where follwedUserId=?";
        Object[] getUserFollowerParams = new Object[]{
                getUserFollowReq.getUserId(),getUserFollowReq.getFollowedUserId()
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
                "from Users join Following on Users.id=Following.follwedUserId where Following.userId=? ";
        Object[] getUserFollowingParams = new Object[]{
                getUserFollowReq.getUserId(),getUserFollowReq.getFollowedUserId()
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
        String getUserProfileQuery="select Users.id as 'userId',profileImgUrl,(select count(Following.id) from Following where Following.follwedUserId = Users.id)as'followerCount',\n" +
                "       (select count(Following.id) from Following where Following.userId=Users.id)as'followingCount',\n" +
                "       (select exists(select Following.id from Following where Following.userId=? and Following.follwedUserId=Users.id))as'followCheck',name,isHolic,\n" +
                "       (select count(Review.id)from Review where Review.userId=Users.id)as 'reviewCount',\n" +
                "       (select count(Visited.id) from Visited where Visited.userId=Users.id)as 'visitedCount',\n" +
                "       (select count(ReviewImg.id)from ReviewImg join Review on Review.id=reviewId where Review.userId=Users.id) as 'imgCount',\n" +
                "       (select count(Wishes.id) from Wishes where Wishes.userId=Users.id) as 'wishesCount',\n" +
                "       (select count(Mylists.id) from Mylists where Mylists.userId=Users.id) as 'myListCount'\n" +
                "from Users  where Users.id=?";
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

    public List<GetUserEmailRes> getUserEmail(Long userId) {
        String getUserEmailQuery="select id as 'userId', email from Users where Users.id=?";
        return this.jdbcTemplate.query(getUserEmailQuery,
                (rs,rowNum)->new GetUserEmailRes(
                        rs.getLong("userId"),
                        rs.getString("email")
                ),userId);
    }

    public List<GetUserNameRes> getUserName(Long userId) {
        String getUserEmailQuery="select id as 'userId', name from Users where Users.id=?";
        return this.jdbcTemplate.query(getUserEmailQuery,
                (rs,rowNum)->new GetUserNameRes(
                        rs.getLong("userId"),
                        rs.getString("name")
                ),userId);
    }

    public List<GetMyProfileRes> getMyProfile(Long userId) {
        String getUserEmailQuery="select id as 'userId',profileImgUrl,name, email,phoneNumber from Users where Users.id=?";
        return this.jdbcTemplate.query(getUserEmailQuery,
                (rs,rowNum)->new GetMyProfileRes(
                        rs.getLong("userId"),
                        rs.getString("profileImgUrl"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("phoneNumber")
                ),userId);
    }

    public Long getIdByKakaoEmail(String k_email) {
        String getIdByEmail="select userId from KakaoUsers where kakaoEmail=?";
        return this.jdbcTemplate.queryForObject(getIdByEmail,Long.class,k_email);
    }

    public List<GetReviewRes> getUserReview(GetUserReviewReq getUserReviewReq) {
        String evaluationList=String.join(",", getUserReviewReq.getEvaluation().stream().map(evaluation->"'"+evaluation+"'").collect(Collectors.toList()));
        String regionList = String.join(",", getUserReviewReq.getRegion().stream().map(region->"'"+region+"'").collect(Collectors.toList()));
        String categoryList=String.join(",", getUserReviewReq.getCategory().stream().map(category -> "'"+category+"'").collect(Collectors.toList()));
        String priceRangeList=String.join(",", getUserReviewReq.getPriceRange().stream().map(priceRange -> "'"+priceRange+"'").collect(Collectors.toList()));
        String parkingInfoList=String.join(",", getUserReviewReq.getParking().stream().map(parking -> "'"+parking+"'").collect(Collectors.toList()));

        String order=null;
        String regionAndOr = null;
        String categoryAndOr=null;
        String priceAndOr=null;

        if(getUserReviewReq.getOrder()=="recent"){
            order="Review.createdAt desc";
        }
        else if(getUserReviewReq.getOrder()=="distance"){
            order="distance asc";
        }

        String region="";
        if(getUserReviewReq.getRegion().contains("all")){
            regionAndOr="or";
        }
        else{
            regionAndOr = "and";
            region = regionAndOr+" subRegion IN "+"("+regionList+")";
        }

        String category="";
        if(getUserReviewReq.getCategory().contains("all")){
            categoryAndOr="or";
        }
        else{
            categoryAndOr = "and";
            category=categoryAndOr+" foodCategory IN ("+categoryList+")";
        }
        String price="";
        if(getUserReviewReq.getPriceRange().contains("all")){
            priceAndOr="or";
        }
        else{
            priceAndOr = "and";
            price=priceAndOr+" priceInfo IN ("+priceRangeList+")";
        }

        String getUserReviewQuery="";

        Object[] getReviewParams = new Object[]{
                getUserReviewReq.getUserId(), getUserReviewReq.getUserId(),
                getUserReviewReq.getUserId(), getUserReviewReq.getProfileUserId()
        };

        getUserReviewQuery = String.format("select (select (6371*acos(cos(radians(U.Latitude))*cos(radians(Stores.Latitude))\n" +
                "                      *cos(radians(Stores.longitude) -radians(U.longitude))\n" +
                "                      +sin(radians(U.Latitude))*sin(radians(Stores.Latitude)))) from Users U where U.id=?)'distance',Review.id as 'reviewId',Users.id as 'userId'," +
                "                       Users.profileImgUrl,Users.name,isHolic,(select count(Re.review) from Review Re where Re.userId=Users.id)'userReviewCount',\n" +
                    "       (select count(follwedUserId) from Following where follwedUserId=Users.id)'userFollowCount',evaluation,Stores.id as 'storeId',\n" +
                    "       concat('@ ',Stores.name,' - ',Stores.subRegion)'storeName',review,\n" +
                "       (select count(*) from ReviewLikes where ReviewLikes.reviewId= Review.id)'reviewLikes',\n" +
                "        (select count(*) from ReviewComments where ReviewComments.reviewId=Review.id)'reviewComments',\n" +
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
                    "                                        end end as reviewCreated \n" +
                    ",(select exists(select Wishes.id from Wishes where Wishes.userId=? and Wishes.storeId=Stores.id))'wishCheck'\n" +
                    ",(select exists(select ReviewLikes.id from ReviewLikes where ReviewLikes.userId=? and Review.id=ReviewLikes.reviewId))'likeCheck'\n" +
                    "from Users\n" +
                    "    join Review on Review.userId=Users.id\n" +
                    "    join Stores on Stores.id = Review.storeId \n" +
                    "    where Users.id=? and evaluation IN (%s) %s " +
                    "    %s %s and parkingInfo IN (%s) \n" +
                    "    order by %s ", evaluationList,region,category, price, parkingInfoList,order);
        String getImgQuery="select Review.id as 'ReviewId',imgUrl from Stores\n" +
                "    join Review on Review.storeId=Stores.id\n" +
                "    left join ReviewImg on ReviewImg.reviewId=Review.id " +
                "    join Users on Review.userId=Users.id " +
                "where Review.id=? order by Review.createdAt ";
        return this.jdbcTemplate.query(getUserReviewQuery,
                (rs,rowNum)->new GetReviewRes(
                        rs.getString("distance"),
                        rs.getLong("reviewId"),
                        rs.getLong("userId"),
                        rs.getString("profileImgUrl"),
                        rs.getString("name"),
                        rs.getString("isHolic"),
                        rs.getInt("userReviewCount"),
                        rs.getInt("userFollowCount"),
                        rs.getString("evaluation"),
                        rs.getLong("storeId"),
                        rs.getString("storeName"),
                        rs.getString("review"),
                        rs.getInt("reviewLikes"),
                        rs.getInt("reviewComments"),
                        rs.getString("reviewCreated"),
                        rs.getInt("wishCheck"),
                        rs.getInt("likeCheck"),
                        ImgList=this.jdbcTemplate.query(getImgQuery,
                                (rk,rownum)->new GetImgRes(
                                        rk.getLong("reviewId"),
                                        rk.getString("imgUrl")
                                ),rs.getLong("reviewId"))
                ),getReviewParams);

    }


    public List<GetUserVisitedRes> getUserVisited(GetTimeLineReq getTimeLineReq) {
        String regionList = String.join(",", getTimeLineReq.getRegion().stream().map(region->"'"+region+"'").collect(Collectors.toList()));
        String categoryList=String.join(",", getTimeLineReq.getCategory().stream().map(category -> "'"+category+"'").collect(Collectors.toList()));
        String priceRangeList=String.join(",", getTimeLineReq.getPriceRange().stream().map(priceRange -> "'"+priceRange+"'").collect(Collectors.toList()));
        String parkingInfoList=String.join(",", getTimeLineReq.getParking().stream().map(parking -> "'"+parking+"'").collect(Collectors.toList()));

        String order=null;
        String regionAndOr = null;
        String categoryAndOr=null;
        String priceAndOr=null;


        if(getTimeLineReq.getOrder()=="recent"){
            order="V.createdAt desc";
        }
        else if(getTimeLineReq.getOrder()=="distance"){
            order="distance asc";
        }

        String region="";
        if(getTimeLineReq.getRegion().contains("all")){
            regionAndOr="or";
        }
        else{
            regionAndOr = "and";
            region = regionAndOr+" subRegion IN "+"("+regionList+")";
        }

        String category="";
        if(getTimeLineReq.getCategory().contains("all")){
            categoryAndOr="or";
        }
        else{
            categoryAndOr = "and";
            category=categoryAndOr+" foodCategory IN ("+categoryList+")";
        }
        String price="";
        if(getTimeLineReq.getPriceRange().contains("all")){
            priceAndOr="or";
        }
        else{
            priceAndOr = "and";
            price=priceAndOr+" priceInfo IN ("+priceRangeList+")";
        }

        Object[] getVisitedParams = new Object[]{
                getTimeLineReq.getUserId(), getTimeLineReq.getUserId(), getTimeLineReq.getUserId(),
                getTimeLineReq.getUserId(), getTimeLineReq.getProfileUserId()
        };
        String getUserVisitedQuery=String.format("select (select (6371*acos(cos(radians(U.Latitude))*cos(radians(S.Latitude))   +\n" +
                "                \"                      *cos(radians(S.longitude) -radians(U.longitude))    +\n" +
                "                \"                      +sin(radians(U.Latitude))*sin(radians(S.Latitude)))) from Users U where U.id=?)'distance',V.id 'visitedId',Users.id as 'userId',profileImgUrl,Users.name as 'userName',isHolic," +
                "       (select count(Re.review) from Review Re where Re.userId=Users.id)'userReviewCount',\n" +
                "       (select count(follwedUserId) from Following where follwedUserId=Users.id)'userFollowCount',\n" +
                "           S.id as'storeId',\n" +
                "       concat('@',S.name,' - ',S.subRegion)as'storeTag',description,\n" +
                "       (select count(VL.id) from VisitedLikes VL where VL.visitedId=V.id)as'likeCount',\n" +
                "       (select count(VC.id) from VisitedComments VC where VC.visitedId=V.id)as'commentCount'," +
                "       case when YEAR(V.createdAt)<YEAR(now())\n" +
                "                    then concat(YEAR(V.createdAt),'년 ',MONTH(V.createdAt),'월 ',DAY(V.createdAt),'일')\n" +
                "                                 when YEAR(V.createdAt)=YEAR(now()) then\n" +
                "                                       case\n" +
                "                                           when TIMESTAMPDIFF(minute,V.createdAt,now())<1\n" +
                "                                                then concat(TIMESTAMPDIFF(second,V.createdAt,now()),' 초 전')\n" +
                "                                           when TIMESTAMPDIFF(hour,V.createdAt,now())<1\n" +
                "                                                then concat(TIMESTAMPDIFF(minute,V.createdAt,now()),'분 전')\n" +
                "                                            when TIMESTAMPDIFF(hour,V.createdAt,now())<24\n" +
                "                                                then concat(TIMESTAMPDIFF(hour,V.createdAt,now()),' 시간 전')\n" +
                "                                              when (TIMESTAMPDIFF(DAY,V.createdAt,now()))>7\n" +
                "                                               then concat(year(V.createdAt),'년 ',month(V.createdAt),'월 ',DAY(V.createdAt),'일')\n" +
                "                                            when TIMESTAMPDIFF(minute,V.createdAt,now())<1\n" +
                "                                                then concat(TIMESTAMPDIFF(second,V.createdAt,now()))\n" +
                "                                            when TIMESTAMPDIFF(hour,V.createdAt,now())>24\n" +
                "                                                then concat(TIMESTAMPDIFF(DAY,V.createdAt,now()),' 일 전')\n" +
                "                                        end end as visitedCreated,\n" +
                "       (select exists(select Wishes.id from Wishes, Users U2 where Wishes.userId=U2.id && U2.id = ? && Wishes.storeId=V.storeId))'wishCheck',\n" +
                "       (select exists(select Visited.id from Visited, Users U2 where Visited.userId=U2.id  && U2.id = ? && Visited.storeId=V.storeId))'visitedCheck',\n" +
                "       (select exists(select VisitedLikes.id from VisitedLikes, Users U2 where VisitedLikes.userId=U2.id and V.id=VisitedLikes.visitedId and U2.id=?))'likeCheck'\n" +
                "from Users\n" +
                "    join Visited V on Users.id = V.userId\n" +
                "    join Stores S on V.storeId = S.id\n" +
                "where Users.id=? %s " +
                "    %s %s and parkingInfo IN (%s) \n" +
                "    order by %s ", region,category, price, parkingInfoList,order);
        String getStoreInfoQuery="select S.id as'storeId',(select ReviewImgSelect.imgurl from ReviewImg ReviewImgSelect left join Review on Review.id=reviewId " +
                "       where ReviewImgSelect.reviewId=Review.id and S.id=Review.storeId limit 1)as 'storeImg',\n" +
                "       name as'storeName',subRegion,foodCategory,viewCount,(select count(Review.id) from Review where Review.storeId=S.id)'reviewCount'\n" +
                "from Stores S where S.id=?";
        return this.jdbcTemplate.query(getUserVisitedQuery,
                (rs,rowNum)->new GetUserVisitedRes(
                        rs.getString("distance"),
                        rs.getLong("visitedId"),
                        rs.getLong("userId"),
                        rs.getString("profileImgUrl"),
                        rs.getString("userName"),
                        rs.getString("isHolic"),
                        rs.getInt("userReviewCount"),
                        rs.getInt("userFollowCount"),
                        rs.getLong("storeId"),
                        rs.getString("storeTag"),
                        rs.getString("description"),
                        rs.getInt("likeCount"),
                        rs.getInt("commentCount"),
                        rs.getString("visitedCreated"),
                        rs.getInt("visitedCheck"),
                        rs.getInt("wishCheck"),
                        rs.getInt("likeCheck"),
                        storeInfo=this.jdbcTemplate.query(getStoreInfoQuery,
                                (rk,rownum)-> new GetVisitedStoreRes(
                                        rk.getLong("storeId"),
                                        rk.getString("storeImg"),
                                        rk.getString("storeName"),
                                        rk.getString("subRegion"),
                                        rk.getString("foodCategory"),
                                        rk.getInt("viewCount"),
                                        rk.getInt("reviewCount")
                                ),rs.getLong("storeId"))
                ),getVisitedParams );
    }

    public List<GetUserPhotoRes> getUserPhotos(GetTimeLineReq getTimeLineReq) {
        String regionList = String.join(",", getTimeLineReq.getRegion().stream().map(region->"'"+region+"'").collect(Collectors.toList()));
        String categoryList=String.join(",", getTimeLineReq.getCategory().stream().map(category -> "'"+category+"'").collect(Collectors.toList()));
        String priceRangeList=String.join(",", getTimeLineReq.getPriceRange().stream().map(priceRange -> "'"+priceRange+"'").collect(Collectors.toList()));
        String parkingInfoList=String.join(",", getTimeLineReq.getParking().stream().map(parking -> "'"+parking+"'").collect(Collectors.toList()));

        String order=null;
        String regionAndOr = null;
        String categoryAndOr=null;
        String priceAndOr=null;


        if(getTimeLineReq.getOrder()=="recent"){
            order="RI.createdAt desc";
        }
        else if(getTimeLineReq.getOrder()=="distance"){
            order="distance asc";
        }

        String region="";
        if(getTimeLineReq.getRegion().contains("all")){
            regionAndOr="or";
        }
        else{
            regionAndOr = "and";
            region = regionAndOr+" subRegion IN "+"("+regionList+")";
        }

        String category="";
        if(getTimeLineReq.getCategory().contains("all")){
            categoryAndOr="or";
        }
        else{
            categoryAndOr = "and";
            category=categoryAndOr+" foodCategory IN ("+categoryList+")";
        }
        String price="";
        if(getTimeLineReq.getPriceRange().contains("all")){
            priceAndOr="or";
        }
        else{
            priceAndOr = "and";
            price=priceAndOr+" priceInfo IN ("+priceRangeList+")";
        }
        Object[] getUserPhotoParmas=new Object[]{
            getTimeLineReq.getUserId(), getTimeLineReq.getProfileUserId()
        };
        String getUserPhotoQuery=String.format("select (select (6371*acos(cos(radians(U.Latitude))*cos(radians(S.Latitude))*cos(radians(S.longitude) -radians(U.longitude))\n" +
                "        +sin(radians(U.Latitude))*sin(radians(S.Latitude)))) from Users U where U.id=?)'distance',\n" +
                "       RI.id as'reviewImgId',imgUrl from ReviewImg RI\n" +
                "    join Review R on RI.reviewId = R.id\n" +
                "    join Users U on U.id=R.userId\n" +
                "    join Stores S on R.storeId=S.id\n" +
                "where U.id=? %s " +
                "    %s %s and parkingInfo IN (%s) \n" +
                "    order by %s ", region,category, price, parkingInfoList,order);

        return this.jdbcTemplate.query(getUserPhotoQuery,
                (rs,rowNum)->new GetUserPhotoRes(
                        rs.getString("distance"),
                        rs.getLong("reviewImgId"),
                        rs.getString("imgUrl")
                ),getUserPhotoParmas
                );
    }

    public List<GetUserWishesRes> getUserWishes(GetTimeLineReq getTimeLineReq) {
        String regionList = String.join(",", getTimeLineReq.getRegion().stream().map(region->"'"+region+"'").collect(Collectors.toList()));
        String categoryList=String.join(",", getTimeLineReq.getCategory().stream().map(category -> "'"+category+"'").collect(Collectors.toList()));
        String priceRangeList=String.join(",", getTimeLineReq.getPriceRange().stream().map(priceRange -> "'"+priceRange+"'").collect(Collectors.toList()));
        String parkingInfoList=String.join(",", getTimeLineReq.getParking().stream().map(parking -> "'"+parking+"'").collect(Collectors.toList()));

        String order=null;
        String regionAndOr = null;
        String categoryAndOr=null;
        String priceAndOr=null;


        if(getTimeLineReq.getOrder()=="recent"){
            order="W.createdAt desc";
        }
        else if(getTimeLineReq.getOrder()=="distance"){
            order="distance asc";
        }

        String region="";
        if(getTimeLineReq.getRegion().contains("all")){
            regionAndOr="or";
        }
        else{
            regionAndOr = "and";
            region = regionAndOr+" subRegion IN "+"("+regionList+")";
        }

        String category="";
        if(getTimeLineReq.getCategory().contains("all")){
            categoryAndOr="or";
        }
        else{
            categoryAndOr = "and";
            category=categoryAndOr+" foodCategory IN ("+categoryList+")";
        }
        String price="";
        if(getTimeLineReq.getPriceRange().contains("all")){
            priceAndOr="or";
        }
        else{
            priceAndOr = "and";
            price=priceAndOr+" priceInfo IN ("+priceRangeList+")";
        }
        Object[] getUserWishesParmas=new Object[]{
            getTimeLineReq.getUserId(),getTimeLineReq.getUserId(),getTimeLineReq.getUserId(),getTimeLineReq.getProfileUserId()
        };

        String getUserWishesQuery=String.format("select (select (6371*acos(cos(radians(U2.Latitude))*cos(radians(S.Latitude))\n" +
                "                      *cos(radians(S.longitude) -radians(U2.longitude))\n" +
                "                      +sin(radians(U2.Latitude))*sin(radians(S.Latitude)))) from Users U2 where U2.id=?)'distance',S.id as 'storeId',\n" +
                "       (select ReviewImgSelect.imgurl from ReviewImg ReviewImgSelect left join Review on Review.id=reviewId where ReviewImgSelect.reviewId=Review.id and S.id=Review.storeId limit 1)as 'imgUrl',\n" +
                "       subRegion,S.name,rating,viewCount,(select count(storeId) from Review where Review.storeId=S.id)'reviewCount',\n" +
                "       (select exists(select Wishes.id from Wishes where Wishes.userId= ? && Wishes.storeId=S.id))'wishCheck',\n" +
                "       (select exists(select Visited.id from Visited where  Visited.id = ? && S.id=Visited.storeId))'visitedCheck'\n" +
                "from Stores S\n" +
                "    join Wishes W on W.storeId=S.id\n" +
                "    join Users U on U.id=W.userId where W.userId=?" +
                " %s %s %s and parkingInfo IN (%s) \n" +
                "    order by %s ", region,category, price, parkingInfoList,order);
        return this.jdbcTemplate.query(getUserWishesQuery,
                (rs,rowNum)->new GetUserWishesRes(
                        rs.getString("distance"),
                        rs.getLong("storeId"),
                        rs.getString("imgUrl"),
                        rs.getString("subRegion"),
                        rs.getString("name"),
                        rs.getInt("rating"),
                        rs.getInt("viewCount"),
                        rs.getInt("reviewCount"),
                        rs.getInt("wishCheck"),
                        rs.getInt("visitedCheck")
                ),getUserWishesParmas);
    }

    public GetMyInfoRes getMyInfo(Long userId) {
        String getMyInfoQuery = "select Users.id as 'userId',profileImgUrl," +
                "       (select count(Following.id) from Following where Following.follwedUserId = Users.id)as'followerCount',\n" +
                "       (select count(Following.id) from Following where Following.userId=Users.id)as'followingCount',name,isHolic,\n" +
                "       (select count(EatDealPayment.id) from EatDealPayment where EatDealPayment.userId = Users.id)as 'eatDealCount', \n" +
                "       (select count(Review.id)from Review where Review.userId=Users.id)as 'reviewCount',\n" +
                "       (select count(Visited.id) from Visited where Visited.userId=Users.id)as 'visitedCount',\n" +
                "       (select count(ReviewImg.id)from ReviewImg join Review on Review.id=reviewId where Review.userId=Users.id) as 'imgCount',\n" +
                "       (select count(Wishes.id) from Wishes where Wishes.userId=Users.id) as 'wishesCount',\n" +
                "       (select count(Mylists.id) from Mylists where Mylists.userId=Users.id) as 'myListCount',\n" +
                "       (select count(BookMarks.id) from BookMarks where BookMarks.userId=Users.id) as 'bookmarkCount',\n" +
                "       (select count(Stores.id) from Stores where Stores.creatorId=Users.id) as 'storeCount'\n" +
                "from Users where Users.id=?";
        return this.jdbcTemplate.queryForObject(getMyInfoQuery,
                (rs, rowNum) -> new GetMyInfoRes(
                        rs.getLong("userId"),
                        rs.getString("profileImgUrl"),
                        rs.getInt("followerCount"),
                        rs.getInt("followingCount"),
                        rs.getString("name"),
                        rs.getString("isHolic"),
                        rs.getInt("eatDealCount"),
                        rs.getInt("reviewCount"),
                        rs.getInt("visitedCount"),
                        rs.getInt("imgCount"),
                        rs.getInt("wishesCount"),
                        rs.getInt("myListCount"),
                        rs.getInt("bookmarkCount"),
                        rs.getInt("storeCount")
                ), userId);
    }

    public Long createMylist(Long userId, PostMylistReq postMylistReq) {
        String createMylistQuery = "insert into Mylists (userId, name, description) values (?, ?, ?)";

        this.jdbcTemplate.update(createMylistQuery, userId, postMylistReq.getMylistName(), postMylistReq.getDescription());
        String lastInsertIdQuery = "select last_insert_id()";
        return this.jdbcTemplate.queryForObject(lastInsertIdQuery,Long.class);
    }

    public List<GetUserMylistsRes> getUserMylists(Long userId, Long profileUserId) {
        String getUserMylistsQuery = "SELECT *, M.id AS mylistId, (select I.imgurl from MylistStores S, ReviewImg I\n" +
                "left join Review on Review.id=reviewId where I.reviewId=Review.id && S.storeId=Review.storeId && S.mylistId = M.id limit 1) AS imgUrl,\n" +
                "(select exists(select B.id from BookMarks B where B.mylistId = M.id && B.userId = ?)) AS bookmarkCheck,\n" +
                "(select count(B.id) from BookMarks B where B.mylistId = M.id) AS bookmarkCount\n" +
                "FROM Mylists M\n" +
                "WHERE M.userId = ?";
        return this.jdbcTemplate.query(getUserMylistsQuery,
                (rs, rowNum) -> new GetUserMylistsRes(
                        rs.getLong("mylistId"),
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getString("imgUrl"),
                        rs.getInt("bookmarkCheck"),
                        rs.getInt("bookmarkCount")
                ), userId, profileUserId);
    }

    public void increaseViewCount(Long mylistId){
        String increaseQuery = "UPDATE Mylists SET viewCount = viewCount + 1 WHERE id = ?";
        Long increaseParam = mylistId;
        this.jdbcTemplate.update(increaseQuery, increaseParam);
    }

    public GetMylistRes getMylist(Long userId, Long mylistId) {
        String getDetailQuery = "SELECT M.updatedAt, M.viewCount, (select count(B.id) FROM BookMarks B WHERE B.mylistId = M.id)AS bookmarkCount,\n" +
                "       M.name AS mylistName, U.name AS userName, U.profileImgUrl, (select count(R.id) FROM Review R WHERE R.userId = M.userId)AS reviewCount,\n" +
                "       (select count(F.id) FROM Following F WHERE F.follwedUserId = M.userId)AS followerCount,\n" +
                "       (select exists(select F2.id FROM Following F2 WHERE F2.follwedUserId = M.userId && F2.userId = ?)) AS followCheck, M.description\n" +
                "FROM Mylists M, Users U\n" +
                "WHERE M.id = ? && U.id = M.userId";
        String getStoreQuery = "SELECT S.id AS storeId, S.name, S.address, S.rating,\n" +
                "       (SELECT exists(select W.id FROM Wishes W WHERE W.userId = ? && W.storeId = MS.storeId))AS wishCheck,\n" +
                "       (SELECT exists(select V.id FROM Visited V WHERE V.userId = ? && V.storeId = MS.storeId))AS visitedCheck,\n" +
                "       (SELECT DISTINCT R.review FROM Review R WHERE R.storeId = MS.storeId LIMIT 1)AS review,\n" +
                "       (SELECT DISTINCT U.name FROM Users U, Review R WHERE R.storeId = MS.storeId && U.id = R.userId LIMIT 1 )AS userName,\n" +
                "       (SELECT DISTINCT U.profileImgUrl FROM Users U, Review R WHERE R.storeId = MS.storeId && U.id = R.userId LIMIT 1 )AS profileImgUrl,\n" +
                "       (SELECT DISTINCT U.isHolic FROM Users U, Review R WHERE R.storeId = MS.storeId && U.id = R.userId LIMIT 1 )AS isHolic\n" +
                "FROM MylistStores MS, Stores S\n" +
                "WHERE MS.mylistId = ? && MS.storeId = S.id";
        GetMylistDetailRes getMylistDetailRes;
        List<GetMylistStoresRes> getMylistStoresRes;
        return new GetMylistRes(
                getMylistDetailRes = this.jdbcTemplate.queryForObject(getDetailQuery,
                        (rs, rowNum) -> new GetMylistDetailRes(
                                rs.getString("updatedAt"),
                                rs.getInt("viewCount"),
                                rs.getInt("bookmarkCount"),
                                rs.getString("mylistName"),
                                rs.getString("userName"),
                                rs.getString("profileImgUrl"),
                                rs.getInt("reviewCount"),
                                rs.getInt("followerCount"),
                                rs.getInt("followCheck"),
                                rs.getString("description")), userId, mylistId),
                        getMylistStoresRes = this.jdbcTemplate.query(getStoreQuery,
                                (rs, rowNum) -> new GetMylistStoresRes(
                                        rs.getLong("storeId"),
                                        rs.getString("name"),
                                        rs.getString("address"),
                                        rs.getFloat("rating"),
                                        rs.getInt("wishCheck"),
                                        rs.getInt("visitedCheck"),
                                        rs.getString("userName"),
                                        rs.getString("profileImgUrl"),
                                        rs.getString("isHolic"),
                                        rs.getString("review")), userId, userId, mylistId)
        );

    }
    public List<GetUserBookmarksRes> getUserBookmarks(Long userId, Long profileUserId) {
        String getUserBookmarksQuery = "SELECT *, M.id AS mylistId, M.name AS mylistName, (select I.imgurl from MylistStores S, ReviewImg I\n" +
                "left join Review on Review.id=reviewId where I.reviewId=Review.id && S.storeId=Review.storeId && S.mylistId = M.id limit 1) AS imgUrl,\n" +
                "(select exists(select B.id from BookMarks B where B.mylistId = M.id && B.userId = ?)) AS bookmarkCheck,\n" +
                "(select U.name from Users U where U.id = M.userId)AS userName, (select U.isHolic from Users U where U.id = M.userId)AS isHolic,\n" +
                "(select count(B.id) from BookMarks B where B.mylistId = M.id) AS bookmarkCount\n" +
                "FROM Mylists M, BookMarks B\n" +
                "WHERE B.userId = ? && B.mylistId = M.id";
        return this.jdbcTemplate.query(getUserBookmarksQuery,
                (rs, rowNum) -> new GetUserBookmarksRes(
                        rs.getLong("mylistId"),
                        rs.getString("userName"),
                        rs.getString("isHolic"),
                        rs.getString("mylistName"),
                        rs.getString("description"),
                        rs.getString("imgUrl"),
                        rs.getInt("bookmarkCheck"),
                        rs.getInt("bookmarkCount")
                ), userId, profileUserId);
    }



}
