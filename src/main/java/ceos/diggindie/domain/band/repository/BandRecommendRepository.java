package ceos.diggindie.domain.band.repository;

import ceos.diggindie.domain.band.entity.BandRecommend;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BandRecommendRepository extends JpaRepository<BandRecommend, Long> {

    @Query("SELECT br FROM BandRecommend br " +
            "JOIN FETCH br.band b " +
            "LEFT JOIN FETCH b.topTrack " +
            "LEFT JOIN FETCH b.bandKeywords bk " +
            "LEFT JOIN FETCH bk.keyword " +
            "WHERE br.member.id = :memberId " +
            "ORDER BY br.priority ASC")
    List<BandRecommend> findByMemberIdWithBandAndTopTrack(@Param("memberId") Long memberId);
}
