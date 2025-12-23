package ceos.diggindie.domain.keyword.repository;

import ceos.diggindie.domain.keyword.entity.MemberKeyword;
import ceos.diggindie.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MemberKeywordRepository extends JpaRepository<MemberKeyword, Long> {

    void deleteAllByMember(Member member);

    @Query("SELECT mk FROM MemberKeyword mk " +
            "JOIN FETCH mk.keyword " +
            "WHERE mk.member.id = :memberId")
    List<MemberKeyword> findAllByMemberIdWithKeyword(@Param("memberId") Long memberId);
}