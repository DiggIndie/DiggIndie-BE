package ceos.diggindie.domain.board.repository;

import ceos.diggindie.domain.board.entity.board.BoardComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BoardCommentRepository extends JpaRepository<BoardComment, Long> {

    @Query("SELECT c FROM BoardComment c " +
            "LEFT JOIN FETCH c.member " +
            "LEFT JOIN FETCH c.likes " +
            "LEFT JOIN FETCH c.childComments " +
            "WHERE c.board.id = :boardId AND c.parentComment IS NULL " +
            "ORDER BY c.createdAt ASC")
    List<BoardComment> findParentCommentsByBoardId(@Param("boardId") Long boardId);
}