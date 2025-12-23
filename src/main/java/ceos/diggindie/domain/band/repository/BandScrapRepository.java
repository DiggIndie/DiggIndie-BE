package ceos.diggindie.domain.band.repository;

import ceos.diggindie.domain.band.entity.BandScrap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BandScrapRepository extends JpaRepository<BandScrap, Long> {

    @Modifying
    @Query("DELETE FROM BandScrap bs WHERE bs.member.id = :memberId")
    void deleteAllByMemberId(@Param("memberId") Long memberId);

    List<BandScrap> findAllByMemberId(Long memberId);
}