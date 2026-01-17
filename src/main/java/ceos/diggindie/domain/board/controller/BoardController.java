package ceos.diggindie.domain.board.controller;

import ceos.diggindie.common.code.SuccessCode;
import ceos.diggindie.common.config.security.CustomUserDetails;
import ceos.diggindie.common.enums.BoardCategory;
import ceos.diggindie.common.response.Response;
import ceos.diggindie.domain.board.dto.board.*;
import ceos.diggindie.domain.board.service.BoardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
    public ResponseEntity<Response<BoardCreateResponse>> createBoard(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody BoardCreateRequest request
    ) {
        BoardCreateResponse result = boardService.createBoard(userDetails.getMemberId(), request);

        Response<BoardCreateResponse> response = Response.success(
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

    @GetMapping("/boards")
    public ResponseEntity<Response<BoardListResponse>> getBoardList(
            @RequestParam(defaultValue = "none") String category,
            @RequestParam(required = false) String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        BoardCategory boardCategory = BoardCategory.from(category);
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        BoardListResponse result = boardService.getBoardList(boardCategory, query, pageable);

        Response<BoardListResponse> response = Response.success(
                SuccessCode.GET_SUCCESS,
                result,
                "디깅 라운지 게시글 목록 조회 API"
        );

        return ResponseEntity.status(200).body(response);
    }

    @Operation(summary = "게시글 좋아요 토글", description = "게시글 좋아요를 토글합니다.")
    @PreAuthorize("isAuthenticated()")
    @PatchMapping("/boards/{boardId}/like")
    public ResponseEntity<Response<LikeResponse>> toggleBoardLike(
            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long boardId
    ) {
        LikeResponse response = boardService.toggleBoardLike(userDetails.getMemberId(), boardId);

        return ResponseEntity.ok(Response.success(
                SuccessCode.UPDATE_SUCCESS,
                response,
                "게시글 좋아요 토글 성공"
        ));
    }

    @Operation(summary = "댓글 좋아요 토글", description = "댓글 좋아요를 토글합니다.")
    @PreAuthorize("isAuthenticated()")
    @PatchMapping("/boards/comments/{commentId}/like")
    public ResponseEntity<Response<LikeResponse>> toggleCommentLike(
            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long commentId
    ) {
        LikeResponse response = boardService.toggleCommentLike(userDetails.getMemberId(), commentId);

        return ResponseEntity.ok(Response.success(
                SuccessCode.UPDATE_SUCCESS,
                response,
                "댓글 좋아요 토글 성공"
        ));
    }
}