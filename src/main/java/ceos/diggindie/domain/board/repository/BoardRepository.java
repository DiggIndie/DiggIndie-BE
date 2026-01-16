package ceos.diggindie.domain.board.repository;

import ceos.diggindie.domain.board.entity.board.Board;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface BoardRepository extends JpaRepository<Board, Long> {

    @Query("SELECT b FROM Board b " +
            "LEFT JOIN FETCH b.member " +
            "LEFT JOIN FETCH b.boardImages " +
            "LEFT JOIN FETCH b.boardLikes " +
            "WHERE b.id = :boardId")
    Optional<Board> findByIdWithDetails(@Param("boardId") Long boardId);
}