package ceos.diggindie.domain.board.repository;

import ceos.diggindie.domain.board.entity.board.BoardLike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BoardLikeRepository extends JpaRepository<BoardLike, Long> {

    Optional<BoardLike> findByMemberIdAndBoardId(Long memberId, Long boardId);

    boolean existsByMemberIdAndBoardId(Long memberId, Long boardId);

    long countByBoardId(Long boardId);
}