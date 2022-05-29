package com.example.demo.src.visited;

import com.example.demo.config.BaseException;
import com.example.demo.src.review.ReviewDao;
import com.example.demo.src.review.ReviewProvider;
import com.example.demo.src.review.model.PostCommentReq;
import com.example.demo.src.review.model.PostReviewListReq;
import com.example.demo.src.store.StoreDao;
import com.example.demo.src.visited.model.PatchVisitedCommentsReq;
import com.example.demo.src.visited.model.PatchVisitedReq;
import com.example.demo.src.visited.model.PostVisitedCommentsReq;
import com.example.demo.src.visited.model.PostVisitedReq;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.sql.SQLException;

import static com.example.demo.config.BaseResponseStatus.*;

@Service
public class VisitedService {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final VisitedDao visitedDao;
    private final VisitedProvider visitedProvider;
    private final JwtService jwtService;
    private final StoreDao storeDao;


    @Autowired
    public VisitedService(VisitedDao visitedDao, VisitedProvider visitedProvider,StoreDao storeDao, JwtService jwtService) {
        this.visitedDao = visitedDao;
        this.visitedProvider = visitedProvider;
        this.storeDao = storeDao;
        this.jwtService = jwtService;
    }

    @Transactional(rollbackOn = BaseException.class)
    public void createVisited(PostVisitedReq postVisitedReq) throws BaseException {
        if(storeDao.checkStoreId(postVisitedReq.getStoreId()) == 0){
            throw new BaseException(NON_EXIST_STORE);
        }
        // 하루에 한번만 가봤어요 생성 가능
       if (visitedDao.checkDate(postVisitedReq.getStoreId(), postVisitedReq.getUserId()) == 1) {
           throw new BaseException(ALREADY_VISITED_TODAY);
       }
        // 가고싶다 했던 가게일 경우 가고싶다 삭제
        try {
            if (storeDao.checkWish(postVisitedReq.getStoreId(), postVisitedReq.getUserId()) == 1) {
                storeDao.deleteWish(postVisitedReq.getStoreId(), postVisitedReq.getUserId());
            }
        } catch (Exception exception) {
            throw new BaseException(DELETE_WISH_FAIL);
        }
        try {
            visitedDao.createVisited(postVisitedReq);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    @Transactional(rollbackOn = BaseException.class)
    public void deleteVisited(Long visitedId, Long userId) throws BaseException {
        if(visitedDao.checkVisitedId(visitedId) == 0){
            throw new BaseException(NON_EXIST_VISITED);
        }
        if(visitedDao.checkCreateUser(visitedId) != userId){
            throw new BaseException(WRONG_USERID);
        }
        // 해당 가봤어요 댓글 모두 삭제
        try {
            visitedDao.deleteAllComments(visitedId);
        } catch (Exception e) {
            throw new BaseException(DELETE_ALL_COMMENTS_FAIL);
        }
        //해당 가봤어요 좋아요 모두 삭제
        try {
            visitedDao.deleteAllLikes(visitedId);
        } catch (Exception e) {
            throw new BaseException(DELETE_ALL_LIKES_FAIL);
        }
        try {
            visitedDao.deleteVisited(visitedId);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public void modifyVisited(Long visitedId, Long userId, PatchVisitedReq patchVisitedReq) throws BaseException {
        if(visitedDao.checkVisitedId(visitedId) == 0){
            throw new BaseException(NON_EXIST_VISITED);
        }
        if(visitedDao.checkCreateUser(visitedId) != userId){
            throw new BaseException(WRONG_USERID);
        }
        try {
            visitedDao.modifyVisited(visitedId, patchVisitedReq);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public void createVisitedLike(Long visitedId, Long userId) throws BaseException{
        if(visitedDao.checkVisitedId(visitedId)==0){
            throw new BaseException(NON_EXIST_VISITED);
        }
        if(visitedDao.checkVisitedLike(visitedId,userId)==1){
            throw new BaseException(EXISTS_VISITED_LIKE);
        }
       try {
            visitedDao.createVisitedLike(visitedId, userId);
        }catch (Exception e){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public void deleteVisitedLike(Long visitedId, Long userId) throws BaseException{
        if(visitedDao.checkVisitedId(visitedId)==0){
            throw new BaseException(NON_EXIST_VISITED);
        }
        if(visitedDao.checkVisitedLike(visitedId,userId)==0){
            throw new BaseException(NON_EXISTS_VISITED_LIKE);
        }
       try {
            visitedDao.deleteVisitedLike(visitedId, userId);
        }catch (Exception e){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public void createVisitedComment(PostVisitedCommentsReq postVisitedCommentsReq) throws BaseException {
        if(visitedDao.checkVisitedId(postVisitedCommentsReq.getVisitedId())==0){
            throw new BaseException(NON_EXIST_VISITED);
        }
        if(visitedDao.checkTagUserId(postVisitedCommentsReq.getTagUserId())==0){
            throw new BaseException(NON_EXISTS_TAG_USER);
        }
        try {
            visitedDao.createVisitedComment(postVisitedCommentsReq);
        }catch (Exception e){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public void deleteVisitedComment(Long commentId, Long userId) throws BaseException{
        if(visitedDao.checkCommentId(commentId)==0){
            throw new BaseException(NON_EXISTS_COMMENT);
        }
        if(visitedDao.checkCommentCreateUser(commentId) != userId){
            throw new BaseException(WRONG_USER_ID);
        }
        try {
            visitedDao.deleteVisitedComment(commentId);
        }catch (Exception e){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public void modifyVisitedComment(Long commentId, Long userId, PatchVisitedCommentsReq patchVisitedCommentsReq) throws BaseException {
        if(visitedDao.checkCommentId(commentId)==0){
            throw new BaseException(NON_EXISTS_COMMENT);
        }
        if(visitedDao.checkCommentCreateUser(commentId) != userId){
            throw new BaseException(WRONG_USER_ID);
        }
        if(visitedDao.checkTagUserId(patchVisitedCommentsReq.getTagUserId())==0){
            throw new BaseException(NON_EXISTS_TAG_USER);
        }
        try {
            visitedDao.modifyVisitedComment(commentId, patchVisitedCommentsReq);
        }catch (Exception e){
            throw new BaseException(DATABASE_ERROR);
        }
    }
}
