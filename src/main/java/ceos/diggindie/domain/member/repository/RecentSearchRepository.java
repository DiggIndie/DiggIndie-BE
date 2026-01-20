package ceos.diggindie.domain.member.repository;

import ceos.diggindie.domain.member.entity.RecentSearch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RecentSearchRepository extends JpaRepository<RecentSearch, Long> {

    List<RecentSearch> findByMemberIdOrderByCreatedAtDesc(Long memberId);

    void deleteAllByMemberId(Long memberId);

    Optional<RecentSearch> findByMemberIdAndContent(Long memberId, String content);

}
