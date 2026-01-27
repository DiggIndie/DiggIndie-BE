package ceos.diggindie.domain.board.repository;

import ceos.diggindie.domain.board.entity.board.Board;
import ceos.diggindie.domain.board.entity.board.BoardLike;
import ceos.diggindie.domain.member.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface BoardLikeRepository extends JpaRepository<BoardLike, Long> {

    Optional<BoardLike> findByMemberIdAndBoardId(Long memberId, Long boardId);

    boolean existsByMemberIdAndBoardId(Long memberId, Long boardId);

    long countByBoardId(Long boardId);

    @Query("""
    SELECT b FROM BoardLike bl
    JOIN bl.board b
    WHERE bl.member = :member
""")
    Page<Board> findBoardsByMember(@Param("member") Member member, Pageable pageable);

}