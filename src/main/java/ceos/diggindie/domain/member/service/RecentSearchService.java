package ceos.diggindie.domain.member.service;

import ceos.diggindie.common.code.BusinessErrorCode;
import ceos.diggindie.common.exception.BusinessException;
import ceos.diggindie.domain.member.dto.RecentSearchRequest;
import ceos.diggindie.domain.member.dto.RecentSearchResponse;
import ceos.diggindie.domain.member.entity.Member;
import ceos.diggindie.domain.member.entity.RecentSearch;
import ceos.diggindie.domain.member.repository.MemberRepository;
import ceos.diggindie.domain.member.repository.RecentSearchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RecentSearchService {

    private final RecentSearchRepository recentSearchRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public RecentSearchResponse.RecentSearchInfo addRecentSearch(Long memberId, RecentSearchRequest request) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BusinessException(BusinessErrorCode.MEMBER_NOT_FOUND));

        recentSearchRepository.findByMemberIdAndContent(memberId, request.content())
                .ifPresent(recentSearchRepository::delete);

        RecentSearch recentSearch = RecentSearch.builder()
                .content(request.content())
                .member(member)
                .build();

        RecentSearch saved = recentSearchRepository.save(recentSearch);
        return RecentSearchResponse.RecentSearchInfo.from(saved);
    }

    public RecentSearchResponse.RecentSearchListDTO getRecentSearches(Long memberId) {
        List<RecentSearch> recentSearches = recentSearchRepository.findByMemberIdOrderByCreatedAtDesc(memberId);
        return RecentSearchResponse.RecentSearchListDTO.from(recentSearches);
    }

    @Transactional
    public void deleteRecentSearch(Long memberId, Long recentSearchId) {
        recentSearchRepository.deleteByMemberIdAndId(memberId, recentSearchId);
    }

    @Transactional
    public void deleteAllRecentSearches(Long memberId) {
        recentSearchRepository.deleteAllByMemberId(memberId);
    }

}
