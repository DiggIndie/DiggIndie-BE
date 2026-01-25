package ceos.diggindie.domain.board.repository;

import ceos.diggindie.domain.board.entity.board.BoardCommentLike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BoardCommentLikeRepository extends JpaRepository<BoardCommentLike, Long> {

    Optional<BoardCommentLike> findByMemberIdAndBoardCommentId(Long memberId, Long boardCommentId);

    boolean existsByMemberIdAndBoardCommentId(Long memberId, Long boardCommentId);

    long countByBoardCommentId(Long boardCommentId);
}