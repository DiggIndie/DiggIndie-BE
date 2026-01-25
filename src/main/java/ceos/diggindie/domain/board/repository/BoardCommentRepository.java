package ceos.diggindie.domain.board.repository;

import ceos.diggindie.domain.board.entity.board.Board;
import ceos.diggindie.domain.board.entity.board.BoardComment;
import ceos.diggindie.domain.member.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BoardCommentRepository extends JpaRepository<BoardComment, Long> {

    @Query("SELECT c FROM BoardComment c " +
            "LEFT JOIN FETCH c.member " +
            "WHERE c.board.id = :boardId AND c.parentComment IS NULL " +
            "ORDER BY c.createdAt ASC")
    List<BoardComment> findParentCommentsByBoardId(@Param("boardId") Long boardId);

    @Query("""
    SELECT DISTINCT b FROM BoardComment bc
    JOIN bc.board b
    WHERE bc.member = :member
""")
    Page<Board> findDistinctBoardsByMember(@Param("member") Member member, Pageable pageable);
}