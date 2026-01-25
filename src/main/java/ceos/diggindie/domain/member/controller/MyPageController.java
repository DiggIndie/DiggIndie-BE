package ceos.diggindie.domain.member.controller;

import ceos.diggindie.common.code.SuccessCode;
import ceos.diggindie.common.config.security.CustomUserDetails;
import ceos.diggindie.common.response.Response;
import ceos.diggindie.domain.member.dto.mypage.*;
import ceos.diggindie.domain.member.service.MyPageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "MyPage", description = "마이페이지 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/my")
@PreAuthorize("isAuthenticated()")
public class MyPageController {

    private final MyPageService myPageService;

    private static final String SORT_PROPERTY = "createdAt";

    @Operation(summary = "내가 쓴 자유게시판 글 조회")
    @GetMapping("/posts/board")
    public ResponseEntity<Response<List<MyBoardPostResponse>>> getMyBoardPosts(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(SORT_PROPERTY).descending());

        Response<List<MyBoardPostResponse>> response = Response.success(
                SuccessCode.GET_SUCCESS,
                myPageService.getMyBoardPosts(userDetails.getExternalId(), pageable),
                "마이페이지 내가 쓴 자유게시판 글 API"
        );
        return ResponseEntity.ok().body(response);
    }

    @Operation(summary = "내가 쓴 마켓 글 조회")
    @GetMapping("/posts/market")
    public ResponseEntity<Response<List<MyMarketPostResponse>>> getMyMarketPosts(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(SORT_PROPERTY).descending());

        Response<List<MyMarketPostResponse>> response = Response.success(
                SuccessCode.GET_SUCCESS,
                myPageService.getMyMarketPosts(userDetails.getExternalId(), pageable),
                "마이페이지 내가 쓴 마켓 글 API"
        );
        return ResponseEntity.ok().body(response);
    }

    @Operation(summary = "내가 댓글 단 게시물 조회 (자유게시판)")
    @GetMapping("/comments")
    public ResponseEntity<Response<List<MyCommentedPostResponse>>> getMyCommentedPosts(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(SORT_PROPERTY).descending());

        Response<List<MyCommentedPostResponse>> response = Response.success(
                SuccessCode.GET_SUCCESS,
                myPageService.getMyCommentedPosts(userDetails.getExternalId(), pageable),
                "마이페이지 내가 댓글 단 게시물 API"
        );
        return ResponseEntity.ok().body(response);
    }

    @Operation(summary = "좋아요한 게시물 조회 (자유게시판)")
    @GetMapping("/likes")
    public ResponseEntity<Response<List<MyLikedPostResponse>>> getMyLikedPosts(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(SORT_PROPERTY).descending());

        Response<List<MyLikedPostResponse>> response = Response.success(
                SuccessCode.GET_SUCCESS,
                myPageService.getMyLikedPosts(userDetails.getExternalId(), pageable),
                "마이페이지 좋아요한 게시물 API"
        );
        return ResponseEntity.ok().body(response);
    }

    @Operation(summary = "스크랩한 게시물 조회 (거래게시판)")
    @GetMapping("/scraps")
    public ResponseEntity<Response<List<MyScrappedPostResponse>>> getMyScrappedPosts(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(SORT_PROPERTY).descending());

        Response<List<MyScrappedPostResponse>> response = Response.success(
                SuccessCode.GET_SUCCESS,
                myPageService.getMyScrappedPosts(userDetails.getExternalId(), pageable),
                "마이페이지 스크랩한 게시물 API"
        );
        return ResponseEntity.ok().body(response);
    }
}