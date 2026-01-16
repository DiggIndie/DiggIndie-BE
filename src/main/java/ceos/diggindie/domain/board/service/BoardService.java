package ceos.diggindie.domain.board.service;

import ceos.diggindie.common.code.BusinessErrorCode;
import ceos.diggindie.common.enums.BoardCategory;

import ceos.diggindie.common.exception.BusinessException;
import ceos.diggindie.domain.board.dto.board.BoardCreateRequest;
import ceos.diggindie.domain.board.dto.board.BoardDetailResponse;
import ceos.diggindie.domain.board.dto.board.BoardResponse;
import ceos.diggindie.domain.board.entity.board.Board;
import ceos.diggindie.domain.board.entity.board.BoardComment;
import ceos.diggindie.domain.board.entity.board.BoardImage;
import ceos.diggindie.domain.board.repository.BoardCommentRepository;
import ceos.diggindie.domain.board.repository.BoardRepository;
import ceos.diggindie.domain.member.entity.Member;

import ceos.diggindie.domain.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BoardService {

    private final BoardRepository boardRepository;
    private final MemberService memberService;
    private final BoardCommentRepository boardCommentRepository;

    @Transactional
    public BoardResponse createBoard(Long memberId, BoardCreateRequest request) {
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
        return BoardResponse.from(savedBoard);
    }

    @Transactional
    public BoardDetailResponse getBoardDetail(Long boardId) {
        Board board = boardRepository.findByIdWithDetails(boardId)
                .orElseThrow(() -> new BusinessException(BusinessErrorCode.BOARD_NOT_FOUND,
                        "게시글을 찾을 수 없습니다."));

        board.increaseViews();

        List<BoardComment> comments = boardCommentRepository.findParentCommentsByBoardId(boardId);

        return BoardDetailResponse.of(board, comments);
    }
}