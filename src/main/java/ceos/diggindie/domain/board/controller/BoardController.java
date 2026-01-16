package ceos.diggindie.domain.board.controller;

import ceos.diggindie.common.code.SuccessCode;
import ceos.diggindie.common.config.security.CustomUserDetails;
import ceos.diggindie.common.response.Response;
import ceos.diggindie.domain.board.dto.board.*;
import ceos.diggindie.domain.board.service.BoardService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/boards")
    public ResponseEntity<Response<BoardResponse>> createBoard(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody BoardCreateRequest request
    ) {
        BoardResponse result = boardService.createBoard(userDetails.getMemberId(), request);

        Response<BoardResponse> response = Response.success(
                SuccessCode.INSERT_SUCCESS,
                result,
                "디깅 라운지 자유 게시글 작성 API"
        );

        return ResponseEntity.status(201).body(response);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/boards/{boardId}")
    public ResponseEntity<Response<BoardDetailResponse>> getBoardDetail(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long boardId
    ) {
        BoardDetailResponse result = boardService.getBoardDetail(boardId, userDetails.getMemberId());
        Response<BoardDetailResponse> response = Response.success(
                SuccessCode.GET_SUCCESS,
                result,
                "디깅 라운지 자유 게시글 상세 조회 API"
        );

        return ResponseEntity.status(200).body(response);
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/boards/{boardId}/comments")
    public ResponseEntity<Response<CommentResponse>> createComment(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long boardId,
            @Valid @RequestBody CommentCreateRequest request
    ) {
        CommentResponse result = boardService.createComment(
                userDetails.getMemberId(), boardId, request);
        Response<CommentResponse> response = Response.success(
                SuccessCode.INSERT_SUCCESS,
                result,
                "디깅 라운지 댓글 작성 API"
        );

        return ResponseEntity.status(201).body(response);
    }
}