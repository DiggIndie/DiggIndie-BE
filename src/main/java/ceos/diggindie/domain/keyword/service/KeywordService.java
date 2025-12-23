package ceos.diggindie.domain.keyword.service;

import ceos.diggindie.common.exception.GeneralException;
import ceos.diggindie.common.status.ErrorStatus;
import ceos.diggindie.domain.keyword.dto.KeywordRequest;
import ceos.diggindie.domain.keyword.dto.KeywordResponse;
import ceos.diggindie.domain.keyword.entity.Keyword;
import ceos.diggindie.domain.keyword.entity.MemberKeyword;
import ceos.diggindie.domain.keyword.repository.KeywordRepository;
import ceos.diggindie.domain.keyword.repository.MemberKeywordRepository;
import ceos.diggindie.domain.member.entity.Member;
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
    public void setMyKeywords(Member member, KeywordRequest request) {
        memberKeywordRepository.deleteAllByMember(member);

        List<Keyword> keywords = keywordRepository.findAllById(request.keywordIds());

        if (keywords.size() != request.keywordIds().size()) {
            throw new GeneralException(ErrorStatus._BAD_REQUEST, "유효하지 않은 키워드 ID가 포함되어 있습니다.");
        }

        List<MemberKeyword> memberKeywords = keywords.stream()
                .map(keyword -> MemberKeyword.of(member, keyword))
                .toList();

        memberKeywordRepository.saveAll(memberKeywords);
    }
}