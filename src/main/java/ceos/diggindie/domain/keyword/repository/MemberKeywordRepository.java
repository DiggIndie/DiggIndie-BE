package ceos.diggindie.domain.keyword.repository;

import ceos.diggindie.domain.keyword.entity.MemberKeyword;
import ceos.diggindie.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberKeywordRepository extends JpaRepository<MemberKeyword, Long> {

    // 특정 회원의 모든 키워드 취향 삭제
    void deleteAllByMember(Member member);
}