package ceos.diggindie.domain.member.repository;

import ceos.diggindie.common.enums.SearchCategory;
import ceos.diggindie.domain.member.entity.RecentSearch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecentSearchRepository extends JpaRepository<RecentSearch, Long> {

    // 특정 회원의 최근 검색어 조회 
    List<RecentSearch> findByMemberIdOrderByCreatedAtDesc(Long memberId);

    // 특정 회원의 카테고리별 최근 검색어 조회 
    List<RecentSearch> findByMemberIdAndCategoryOrderByCreatedAtDesc(Long memberId, SearchCategory category);

    // 특정 회원의 특정 검색어 삭제
    void deleteByMemberIdAndId(Long memberId, Long recentSearchId);

    // 특정 회원의 모든 검색어 삭제
    void deleteAllByMemberId(Long memberId);

    // 특정 회원의 카테고리별 검색어 전체 삭제
    void deleteAllByMemberIdAndCategory(Long memberId, SearchCategory category);

}
