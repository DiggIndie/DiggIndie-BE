package ceos.diggindie.domain.board.service;

import ceos.diggindie.common.code.BusinessErrorCode;
import ceos.diggindie.common.enums.BoardCategory;
import ceos.diggindie.common.exception.BusinessException;
import ceos.diggindie.domain.board.dto.board.*;
import ceos.diggindie.domain.board.entity.board.*;
import ceos.diggindie.domain.board.repository.BoardCommentLikeRepository;
import ceos.diggindie.domain.board.repository.BoardCommentRepository;
import ceos.diggindie.domain.board.repository.BoardLikeRepository;
import ceos.diggindie.domain.board.repository.BoardRepository;
import ceos.diggindie.domain.member.entity.Member;

import ceos.diggindie.domain.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BoardService {

    private final BoardRepository boardRepository;
    private final MemberService memberService;
    private final BoardCommentRepository boardCommentRepository;
    private final BoardLikeRepository boardLikeRepository;
    private final BoardCommentLikeRepository boardCommentLikeRepository;

    @Transactional
    public BoardCreateResponse createBoard(Long memberId, BoardCreateRequest request) {
        Member member = memberService.findById(memberId);

        Board board = Board.builder()
                .title(request.title())
                .content(request.content())
                .isAnonymous(request.isAnonymous())
                .member(member)
                .category(request.category())
                .build();

        List<String> imageUrls = request.imageUrls();
        for (int i = 0; i < imageUrls.size(); i++) {
            BoardImage image = BoardImage.builder()
                    .imageUrl(imageUrls.get(i))
                    .imageOrder(i)
                    .board(board)
                    .build();
            board.addImage(image);
        }

        Board savedBoard = boardRepository.save(board);
        return BoardCreateResponse.from(savedBoard.getId());
    }

    @Transactional
    public BoardDetailResponse getBoardDetail(Long boardId, Long memberId) {
        Board board = boardRepository.findByIdWithImages(boardId)
                .orElseThrow(() -> new BusinessException(BusinessErrorCode.BOARD_NOT_FOUND,
                        "게시글을 찾을 수 없습니다."));

        board.increaseViews();

        List<BoardComment> comments = boardCommentRepository.findParentCommentsByBoardId(boardId);

        return BoardDetailResponse.of(board, comments, memberId);
    }

    @Transactional
    public CommentResponse createComment(Long memberId, Long boardId, CommentCreateRequest request) {
        Member member = memberService.findById(memberId);

        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new BusinessException(BusinessErrorCode.BOARD_NOT_FOUND,
                        "게시글을 찾을 수 없습니다."));

        BoardComment parentComment = null;
        if (request.parentCommentId() != null) {
            parentComment = boardCommentRepository.findById(request.parentCommentId())
                    .orElseThrow(() -> new BusinessException(BusinessErrorCode.COMMENT_NOT_FOUND,
                            "부모 댓글을 찾을 수 없습니다."));
        }

        BoardComment comment = BoardComment.builder()
                .content(request.content())
                .isAnonymous(request.isAnonymous())
                .board(board)
                .member(member)
                .parentComment(parentComment)
                .build();

        BoardComment savedComment = boardCommentRepository.save(comment);
        return CommentResponse.from(savedComment, memberId);
    }

    public BoardListResponse getBoardList(BoardCategory category, String query, Pageable pageable) {

        Page<Board> boards;
        boolean hasQuery = query != null && !query.isBlank();

        if (category == BoardCategory.NONE) {
            boards = hasQuery
                    ? boardRepository.findAllByQueryWithImages(query, pageable)
                    : boardRepository.findAllWithImages(pageable);
        } else {
            boards = hasQuery
                    ? boardRepository.findByCategoryAndQueryWithImages(category, query, pageable)
                    : boardRepository.findByCategoryWithImages(category, pageable);
        }

        return BoardListResponse.from(boards);
    }

    // 게시글 좋아요 토글
    @Transactional
    public LikeResponse toggleBoardLike(Long memberId, Long boardId) {
        Member member = memberService.findById(memberId);

        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new BusinessException(BusinessErrorCode.BOARD_NOT_FOUND,
                        "게시글을 찾을 수 없습니다."));

        // 본인 게시글 좋아요 방지
        if (board.getMember().getId().equals(memberId)) {
            throw new BusinessException(BusinessErrorCode.SELF_LIKE_NOT_ALLOWED,
                    "자신의 게시글에는 좋아요할 수 없습니다.");
        }

        Optional<BoardLike> existingLike = boardLikeRepository.findByMemberIdAndBoardId(memberId, boardId);

        boolean isLiked;
        if (existingLike.isPresent()) {
            boardLikeRepository.delete(existingLike.get());
            isLiked = false;
        } else {
            boardLikeRepository.save(BoardLike.builder()
                    .member(member)
                    .board(board)
                    .build());
            isLiked = true;
        }

        long likeCount = boardLikeRepository.countByBoardId(boardId);
        return LikeResponse.of(isLiked, likeCount);
    }

    // 댓글 좋아요 토글
    @Transactional
    public LikeResponse toggleCommentLike(Long memberId, Long commentId) {
        Member member = memberService.findById(memberId);

        BoardComment comment = boardCommentRepository.findById(commentId)
                .orElseThrow(() -> new BusinessException(BusinessErrorCode.COMMENT_NOT_FOUND,
                        "댓글을 찾을 수 없습니다."));

        // 본인 댓글 좋아요 방지
        if (comment.getMember().getId().equals(memberId)) {
            throw new BusinessException(BusinessErrorCode.SELF_LIKE_NOT_ALLOWED,
                    "자신의 댓글에는 좋아요할 수 없습니다.");
        }

        Optional<BoardCommentLike> existingLike = boardCommentLikeRepository.findByMemberIdAndBoardCommentId(memberId, commentId);

        boolean isLiked;
        if (existingLike.isPresent()) {
            boardCommentLikeRepository.delete(existingLike.get());
            isLiked = false;
        } else {
            boardCommentLikeRepository.save(BoardCommentLike.builder()
                    .member(member)
                    .boardComment(comment)
                    .build());
            isLiked = true;
        }

        long likeCount = boardCommentLikeRepository.countByBoardCommentId(commentId);
        return LikeResponse.of(isLiked, likeCount);
    }

    @Transactional
    public BoardUpdateResponse updateBoard(Long memberId, Long boardId, BoardUpdateRequest request) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new BusinessException(BusinessErrorCode.BOARD_NOT_FOUND));

        if (!board.getMember().getId().equals(memberId)) {
            throw new BusinessException(BusinessErrorCode.BOARD_NOT_OWNER, "본인의 게시글만 수정할 수 있습니다.");
        }

        board.update(
                request.title(),
                request.content(),
                request.isAnonymous(),
                request.category()
        );

        board.clearImages();

        List<String> imageUrls = request.imageUrls();
        for (int i = 0; i < imageUrls.size(); i++) {
            BoardImage image = BoardImage.builder()
                    .board(board)
                    .imageUrl(imageUrls.get(i))
                    .imageOrder(i)
                    .build();
            board.addImage(image);
        }

        return BoardUpdateResponse.from(board);
    }

    @Transactional
    public void deleteBoard(Long memberId, Long boardId) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new BusinessException(BusinessErrorCode.BOARD_NOT_FOUND));

        if (!board.getMember().getId().equals(memberId)) {
            throw new BusinessException(BusinessErrorCode.BOARD_NOT_OWNER, "본인의 게시글만 삭제할 수 있습니다.");
        }

        boardRepository.delete(board);
    }

}