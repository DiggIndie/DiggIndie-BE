package ceos.diggindie.domain.member.repository;

import ceos.diggindie.domain.member.entity.MemberBand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MemberBandRepository extends JpaRepository<MemberBand, Long> {

    @Query("SELECT mb.band.id FROM MemberBand mb WHERE mb.member.id = :memberId")
    List<Long> findBandIdsByMemberId(@Param("memberId") Long memberId);
}
