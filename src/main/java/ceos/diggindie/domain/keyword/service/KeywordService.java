package ceos.diggindie.domain.keyword.service;

import ceos.diggindie.common.code.GeneralErrorCode;
import ceos.diggindie.common.exception.GeneralException;
import ceos.diggindie.domain.keyword.dto.KeywordRequest;
import ceos.diggindie.domain.keyword.dto.KeywordResponse;
import ceos.diggindie.domain.keyword.entity.Keyword;
import ceos.diggindie.domain.keyword.entity.MemberKeyword;
import ceos.diggindie.domain.keyword.repository.KeywordRepository;
import ceos.diggindie.domain.keyword.repository.MemberKeywordRepository;
import ceos.diggindie.domain.member.entity.Member;
import ceos.diggindie.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class KeywordService {

    private final KeywordRepository keywordRepository;
    private final MemberKeywordRepository memberKeywordRepository;
    private final MemberRepository memberRepository;

    public List<KeywordResponse> getAllKeywords() {
        return keywordRepository.findAll().stream()
                .map(KeywordResponse::from)
                .toList();
    }

    public List<KeywordResponse> getMyKeywords(Long memberId) {
        List<MemberKeyword> memberKeywords = memberKeywordRepository.findAllByMemberIdWithKeyword(memberId);

        return memberKeywords.stream()
                .map(mk -> KeywordResponse.from(mk.getKeyword()))
                .toList();
    }

    @Transactional
    public void setMyKeywords(Long memberId, KeywordRequest request) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> GeneralException.notFound("사용자를 찾을 수 없습니다."));

        memberKeywordRepository.deleteAllByMember(member);

        List<Keyword> keywords = keywordRepository.findAllById(request.keywordIds());

        List<Long> foundIds = keywords.stream().map(Keyword::getId).toList();
        List<Long> invalidIds = request.keywordIds().stream()
                .filter(id -> !foundIds.contains(id))
                .toList();

        if (!invalidIds.isEmpty()) {
            throw new GeneralException(GeneralErrorCode.BAD_REQUEST,
                    "유효하지 않은 키워드 ID: " + invalidIds);
        }

        List<MemberKeyword> memberKeywords = keywords.stream()
                .map(keyword -> MemberKeyword.of(member, keyword))
                .toList();

        memberKeywordRepository.saveAll(memberKeywords);
    }
}