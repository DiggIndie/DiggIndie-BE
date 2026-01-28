package ceos.diggindie.domain.board.repository;

import ceos.diggindie.common.enums.BoardCategory;
import ceos.diggindie.domain.board.entity.board.Board;
import ceos.diggindie.domain.member.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface BoardRepository extends JpaRepository<Board, Long> {

    @Query("SELECT b FROM Board b " +
            "LEFT JOIN FETCH b.member " +
            "LEFT JOIN FETCH b.boardImages " +
            "WHERE b.id = :boardId")
    Optional<Board> findByIdWithImages(@Param("boardId") Long boardId);

    @Query("SELECT b FROM Board b LEFT JOIN FETCH b.boardImages")
    Page<Board> findAllWithImages(Pageable pageable);

    @Query("SELECT b FROM Board b LEFT JOIN FETCH b.boardImages WHERE b.category = :category")
    Page<Board> findByCategoryWithImages(@Param("category") BoardCategory category, Pageable pageable);

    // 전체 + 검색
    @Query("SELECT b FROM Board b LEFT JOIN FETCH b.boardImages " +
            "WHERE b.title LIKE %:query% OR b.content LIKE %:query%")
    Page<Board> findAllByQueryWithImages(@Param("query") String query, Pageable pageable);

    // 카테고리 + 검색
    @Query("SELECT b FROM Board b LEFT JOIN FETCH b.boardImages " +
            "WHERE b.category = :category AND (b.title LIKE %:query% OR b.content LIKE %:query%)")
    Page<Board> findByCategoryAndQueryWithImages(@Param("category") BoardCategory category,
                                                 @Param("query") String query,
                                                 Pageable pageable);

    Page<Board> findByMemberOrderByCreatedAtDesc(Member member, Pageable pageable);

    Page<Board> findAllByOrderByViewsDesc(Pageable pageable);

}