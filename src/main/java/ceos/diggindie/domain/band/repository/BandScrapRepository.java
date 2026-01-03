package ceos.diggindie.domain.band.repository;

import ceos.diggindie.domain.band.entity.BandScrap;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BandScrapRepository extends JpaRepository<BandScrap, Long> {

    void deleteAllByMemberId(Long memberId);

    @Query(value = "SELECT bs FROM BandScrap bs " +
            "JOIN FETCH bs.band b " +
            "LEFT JOIN FETCH b.bandKeywords bk " +
            "LEFT JOIN FETCH bk.keyword " +
            "WHERE bs.memberId = :memberId",
            countQuery = "SELECT count(bs) FROM BandScrap bs WHERE bs.memberId = :memberId")
    Page<BandScrap> findAllByMemberIdWithKeywords(@Param("memberId") Long memberId, Pageable pageable);

    List<BandScrap> findAllByMemberId(Long memberId);

    void deleteByMemberIdAndBandId(Long memberId, Long bandId);
}