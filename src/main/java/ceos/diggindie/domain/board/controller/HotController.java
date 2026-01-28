package ceos.diggindie.domain.board.controller;

import ceos.diggindie.common.code.SuccessCode;
import ceos.diggindie.common.response.Response;
import ceos.diggindie.domain.board.dto.HotPostResponse;
import ceos.diggindie.domain.board.service.HotService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Hot Posts", description = "인기 게시글 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/boards")
public class HotController {

    private final HotService hotService;

    @Operation(summary = "통합 게시판 인기 게시글 조회 (페이징)",
            description = "모든 게시판(Market, Board)을 통합하여 조회수 기준 인기 게시글을 페이징하여 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청")
    })
    @GetMapping("/hot")
    public ResponseEntity<Response<Page<HotPostResponse>>> getHotPosts(
            @Parameter(description = "페이지 번호 (0부터 시작)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기", example = "10")
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<HotPostResponse> result = hotService.getHotPosts(pageable);

        Response<Page<HotPostResponse>> response = Response.success(
                SuccessCode.GET_SUCCESS,
                result,
                "통합 게시판 인기 게시글 조회 API"
        );

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "인기 게시글 TOP3 조회",
            description = "모든 게시판을 통합하여 조회수 기준 상위 3개 게시글을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공")
    })
    @GetMapping("/hot/top3")
    public ResponseEntity<Response<List<HotPostResponse>>> getTop3Posts() {
        List<HotPostResponse> result = hotService.getTop3Posts();

        Response<List<HotPostResponse>> response = Response.success(
                SuccessCode.GET_SUCCESS,
                result,
                "인기 게시글 TOP3 조회 API"
        );

        return ResponseEntity.ok(response);
    }
}