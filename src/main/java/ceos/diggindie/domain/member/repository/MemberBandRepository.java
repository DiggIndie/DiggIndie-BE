package ceos.diggindie.domain.member.repository;

import ceos.diggindie.domain.member.entity.MemberBand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MemberBandRepository extends JpaRepository<MemberBand, Long> {

    @Modifying
    @Query("DELETE FROM MemberBand mb WHERE mb.member.id = :memberId")
    void deleteAllByMemberId(@Param("memberId") Long memberId);

    @Query("SELECT mb FROM MemberBand mb " +
            "JOIN FETCH mb.band " +
            "WHERE mb.member.id = :memberId")
    List<MemberBand> findAllByMemberIdWithBand(@Param("memberId") Long memberId);
}
