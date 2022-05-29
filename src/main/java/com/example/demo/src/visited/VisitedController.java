package com.example.demo.src.visited;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.review.ReviewProvider;
import com.example.demo.src.review.ReviewService;
import com.example.demo.src.review.model.*;
import com.example.demo.src.visited.model.*;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.example.demo.config.BaseResponseStatus.INVALID_USER_JWT;

@RestController
@RequestMapping("/visited")
public class VisitedController {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private final VisitedProvider visitedProvider;
    @Autowired
    private final VisitedService visitedService;
    @Autowired
    private final JwtService jwtService;

    public VisitedController(VisitedProvider visitedProvider, VisitedService visitedService, JwtService jwtService) {
        this.visitedProvider = visitedProvider;
        this.visitedService = visitedService;
        this.jwtService = jwtService;
    }

    /**
     * 가봤어요 생성 API
     * [POST] /visited
     * * @return BaseResponse<String>
     */
    @ResponseBody
    @PostMapping("")
    public BaseResponse<String> createVisited(@RequestBody PostVisitedReq postVisitedReq)  {
        try {
            Long userIdxByJwt = jwtService.getUserIdx();
            if (postVisitedReq.getUserId() != userIdxByJwt) {
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            visitedService.createVisited(postVisitedReq);
            String result = "가봤어요 생성 성공";
            return new BaseResponse<>(result);
        }catch (BaseException e){
            return new BaseResponse<>(e.getStatus());
        }
    }

    /**
     * 가봤어요 삭제 API
     * [DELETE] /visited/:visitedId/:userId
     * * @return BaseResponse<String>
     */
    @ResponseBody
    @DeleteMapping("/{visitedId}/{userId}")
    public BaseResponse<String> deleteVisited(@PathVariable("visitedId") Long visitedId, @PathVariable("userId") Long userId) {
        try {
            Long userIdxByJwt = jwtService.getUserIdx();
            if (userId != userIdxByJwt) {
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            visitedService.deleteVisited(visitedId, userId);
            String result = "가봤어요 삭제 성공";
            return new BaseResponse<>(result);
        } catch (BaseException e){
            return new BaseResponse<>(e.getStatus());
        }
    }

    /**
     * 가봤어요 수정 API
     * [PATCH] /visited/:visitedId/:userId
     * * @return BaseResponse<String>
     */
    @ResponseBody
    @PatchMapping("/{visitedId}/{userId}")
    public BaseResponse<String> modifyVisited(@PathVariable("visitedId") Long visitedId, @PathVariable("userId") Long userId, @RequestBody PatchVisitedReq patchVisitedReq) {
        try {
            Long userIdxByJwt = jwtService.getUserIdx();
            if (userId != userIdxByJwt) {
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            visitedService.modifyVisited(visitedId, userId, patchVisitedReq);
            String result = "가봤어요 수정 성공";
            return new BaseResponse<>(result);
        } catch (BaseException e){
            return new BaseResponse<>(e.getStatus());
        }
    }

    /**
     * 가봤어요 상세 조회 API
     * [GET] /visited/:visitedId/:userId
     * * @return BaseResponse<GetVisitedRes>
     */
    @ResponseBody
    @GetMapping("/{visitedId}/{userId}")
    public BaseResponse<GetVisitedRes> getVisited(@PathVariable("visitedId") Long visitedId, @PathVariable("userId") Long userId) {
        try {
            Long userIdxByJwt = jwtService.getUserIdx();
            if (userId != userIdxByJwt) {
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            GetVisitedRes getVisitedRes = visitedProvider.getVisited(visitedId, userId);
            return new BaseResponse<>(getVisitedRes);
        } catch (BaseException e){
            return new BaseResponse<>(e.getStatus());
        }
    }

    /**
     * 가봤어요 좋아요 생성 API
     * [POST] /visited/like/:visitedId/:userId
     * * @return BaseResponse<String>
     */

    @ResponseBody
    @PostMapping("/likes/{visitedId}/{userId}")
    public BaseResponse<String> createVisitedLike(@PathVariable("visitedId") Long visitedId,@PathVariable("userId") Long userId){
        try {
            Long userIdxByJwt = jwtService.getUserIdx();
            if (userId != userIdxByJwt) {
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            String result="좋아요 성공";
            visitedService.createVisitedLike(visitedId,userId);
            return new BaseResponse<>(result);
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }

    }

    /**
     * 가봤어요 좋아요 취소 API
     * [DELETE] /visited/like/:visitedId/:userId
     * * @return BaseResponse<String>
     */

    @ResponseBody
    @DeleteMapping("/likes/{visitedId}/{userId}")
    public BaseResponse<String> deleteVisitedLike(@PathVariable("visitedId") Long visitedId,@PathVariable("userId") Long userId){
        try {
            Long userIdxByJwt = jwtService.getUserIdx();
            if (userId != userIdxByJwt) {
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            String result="좋아요 취소 성공";
            visitedService.deleteVisitedLike(visitedId,userId);
            return new BaseResponse<>(result);
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }

    }

    /**
     * 가봤어요 댓글 생성 API
     * [POST] /comments
     * * @return BaseResponse<String>
     */
    @ResponseBody
    @PostMapping("/comments")
    public BaseResponse<String> createVisitedComment(@RequestBody PostVisitedCommentsReq postVisitedCommentsReq){
        try {
            Long userIdxByJwt = jwtService.getUserIdx();
            if (postVisitedCommentsReq.getUserId() != userIdxByJwt) {
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            String result="가봤어요 댓글 달기 성공";
            visitedService.createVisitedComment(postVisitedCommentsReq);
            return new BaseResponse<>(result);
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

    /**
     * 가봤어요 댓글 삭제 API
     * [DELETE] /comments/:commentId/:userId
     * * @return BaseResponse<String>
     */
    @ResponseBody
    @DeleteMapping("/comments/{commentId}/{userId}")
    public BaseResponse<String> deleteVisitedComment(@PathVariable ("commentId") Long commentId, @PathVariable ("userId") Long userId){
        try {
            Long userIdxByJwt = jwtService.getUserIdx();
            if (userId != userIdxByJwt) {
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            String result="가봤어요 댓글 삭제 성공";
            visitedService.deleteVisitedComment(commentId, userId);
            return new BaseResponse<>(result);
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

    /**
     * 가봤어요 댓글 수정 API
     * [PATCH] /comments/:commentId/:userId
     * * @return BaseResponse<String>
     */
    @ResponseBody
    @PatchMapping("/comments/{commentId}/{userId}")
    public BaseResponse<String> modifyVisitedComment(@PathVariable ("commentId") Long commentId, @PathVariable ("userId") Long userId, @RequestBody PatchVisitedCommentsReq patchVisitedCommentsReq){
        try {
            Long userIdxByJwt = jwtService.getUserIdx();
            if (userId != userIdxByJwt) {
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            String result="가봤어요 댓글 수정 성공";
            visitedService.modifyVisitedComment(commentId, userId,patchVisitedCommentsReq);
            return new BaseResponse<>(result);
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

    /**
     * 가봤어요 좋아요 한 유저 조회 API
     * [GET] /likes/:visitedId/:userId
     * * @return BaseResponse<List<GetVisitedLikeUserRes>
     */
    @ResponseBody
    @GetMapping("/likes/{visitedId}/{userId}")
    public BaseResponse<List<GetVisitedLikeUserRes>> getVisitedLikesUser(@PathVariable("visitedId") Long visitedId, @PathVariable("userId") Long userId){
        try {
            Long userIdxByJwt = jwtService.getUserIdx();
            if (userId != userIdxByJwt) {
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            List<GetVisitedLikeUserRes> getVisitedLikesUserRes = visitedProvider.getVisitedLikesUser(visitedId,userId);
            return new BaseResponse<>(getVisitedLikesUserRes);
        }catch(BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }

    }
}
