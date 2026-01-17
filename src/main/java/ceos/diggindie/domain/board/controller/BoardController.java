package ceos.diggindie.domain.board.controller;

import ceos.diggindie.common.code.SuccessCode;
import ceos.diggindie.common.config.security.CustomUserDetails;
import ceos.diggindie.common.enums.BoardCategory;
import ceos.diggindie.common.response.Response;
import ceos.diggindie.domain.board.dto.board.*;
import ceos.diggindie.domain.board.service.BoardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Board", description = "자유게시판 관련 API")
@RestController
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;

    @Operation(summary = "게시글 작성", description = "자유게시판에 새 게시글을 작성합니다. none, info, review, recommend, release, news, companion을 카테고리로 하며, none일 경우 미지정을 뜻합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "게시글 작성 성공"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청")
    })
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/boards")
    public ResponseEntity<Response<BoardCreateResponse>> createBoard(
            @Parameter(hidden = true)
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

    @Operation(summary = "게시글 상세 조회", description = "게시글의 상세 정보와 댓글을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
            @ApiResponse(responseCode = "404", description = "게시글을 찾을 수 없음")
    })
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/boards/{boardId}")
    public ResponseEntity<Response<BoardDetailResponse>> getBoardDetail(
            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Parameter(description = "게시글 ID", example = "1")
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

    @Operation(summary = "댓글 작성", description = "게시글에 댓글 또는 대댓글을 작성합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "댓글 작성 성공"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
            @ApiResponse(responseCode = "404", description = "게시글 또는 부모 댓글을 찾을 수 없음")
    })
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/boards/{boardId}/comments")
    public ResponseEntity<Response<CommentResponse>> createComment(
            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Parameter(description = "게시글 ID", example = "1")
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

    @Operation(summary = "게시글 목록 조회", description = "카테고리별 게시글 목록을 조회합니다. 검색어로 필터링 가능합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공")
    })
    @GetMapping("/boards")
    public ResponseEntity<Response<BoardListResponse>> getBoardList(
            @Parameter(description = "카테고리 (none, info, review, recommend, release, news, companion), none일 경우 전체를 의미함", example = "none")
            @RequestParam(defaultValue = "none") String category,
            @Parameter(description = "검색어", example = "인디밴드")
            @RequestParam(required = false) String query,
            @Parameter(description = "페이지 번호 (0부터 시작)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기", example = "10")
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

    @Operation(summary = "게시글 좋아요 토글", description = "게시글 좋아요를 토글합니다. 좋아요 상태면 취소, 아니면 좋아요 추가.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "토글 성공"),
            @ApiResponse(responseCode = "400", description = "자신의 게시글/댓글에는 좋아요 불가"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
            @ApiResponse(responseCode = "404", description = "게시글을 찾을 수 없음")
    })
    @PreAuthorize("isAuthenticated()")
    @PatchMapping("/boards/{boardId}/like")
    public ResponseEntity<Response<LikeResponse>> toggleBoardLike(
            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Parameter(description = "게시글 ID", example = "1")
            @PathVariable Long boardId
    ) {
        LikeResponse result = boardService.toggleBoardLike(userDetails.getMemberId(), boardId);

        return ResponseEntity.ok(Response.success(
                SuccessCode.UPDATE_SUCCESS,
                result,
                "게시글 좋아요 토글 성공"
        ));
    }

    @Operation(summary = "댓글 좋아요 토글", description = "댓글 좋아요를 토글합니다. 좋아요 상태면 취소, 아니면 좋아요 추가.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "토글 성공"),
            @ApiResponse(responseCode = "400", description = "자신의 게시글/댓글에는 좋아요 불가"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
            @ApiResponse(responseCode = "404", description = "댓글을 찾을 수 없음")
    })
    @PreAuthorize("isAuthenticated()")
    @PatchMapping("/boards/comments/{commentId}/like")
    public ResponseEntity<Response<LikeResponse>> toggleCommentLike(
            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Parameter(description = "댓글 ID", example = "1")
            @PathVariable Long commentId
    ) {
        LikeResponse result = boardService.toggleCommentLike(userDetails.getMemberId(), commentId);

        return ResponseEntity.ok(Response.success(
                SuccessCode.UPDATE_SUCCESS,
                result,
                "댓글 좋아요 토글 성공"
        ));
    }

    @Operation(summary = "게시글 수정", description = "자신이 작성한 게시글을 수정합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "게시글 수정 성공"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
            @ApiResponse(responseCode = "403", description = "수정 권한 없음"),
            @ApiResponse(responseCode = "404", description = "게시글을 찾을 수 없음")
    })
    @PreAuthorize("isAuthenticated()")
    @PutMapping("/boards/{boardId}")
    public ResponseEntity<Response<BoardUpdateResponse>> updateBoard(
            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Parameter(description = "게시글 ID", example = "1")
            @PathVariable Long boardId,
            @Valid @RequestBody BoardUpdateRequest request
    ) {
        BoardUpdateResponse result = boardService.updateBoard(
                userDetails.getMemberId(), boardId, request);

        Response<BoardUpdateResponse> response = Response.success(
                SuccessCode.UPDATE_SUCCESS,
                result,
                "디깅 라운지 게시글 수정 API"
        );

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "게시글 삭제", description = "자신이 작성한 게시글을 삭제합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "게시글 삭제 성공"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
            @ApiResponse(responseCode = "403", description = "삭제 권한 없음"),
            @ApiResponse(responseCode = "404", description = "게시글을 찾을 수 없음")
    })
    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/boards/{boardId}")
    public ResponseEntity<Response<Void>> deleteBoard(
            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Parameter(description = "게시글 ID", example = "1")
            @PathVariable Long boardId
    ) {
        boardService.deleteBoard(userDetails.getMemberId(), boardId);

        Response<Void> response = Response.success(
                SuccessCode.DELETE_SUCCESS,
                "디깅 라운지 게시글 삭제 API"
        );

        return ResponseEntity.ok(response);
    }
}