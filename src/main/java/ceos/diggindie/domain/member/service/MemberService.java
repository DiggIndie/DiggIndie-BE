package ceos.diggindie.domain.member.service;

import ceos.diggindie.common.code.BusinessErrorCode;
import ceos.diggindie.common.exception.BusinessException;
import ceos.diggindie.domain.member.dto.MarketingConsentRequest;
import ceos.diggindie.domain.member.dto.MarketingConsentResponse;
import ceos.diggindie.domain.member.entity.Member;
import ceos.diggindie.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;

    public Member findById(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new BusinessException(BusinessErrorCode.MEMBER_NOT_FOUND,
                        "회원을 찾을 수 없습니다."));
    }

    public Member findByEmail(String email) {
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(BusinessErrorCode.MEMBER_NOT_FOUND,
                        "회원을 찾을 수 없습니다."));
    }

    public Member findByUserId(String userId) {
        return memberRepository.findByUserId(userId)
                .orElseThrow(() -> new BusinessException(BusinessErrorCode.MEMBER_NOT_FOUND,
                        "회원을 찾을 수 없습니다."));
    }


    @Transactional
    public MarketingConsentResponse updateMarketingConsent(String externalId, MarketingConsentRequest request) {
        Member member = memberRepository.findByExternalId(externalId)
                .orElseThrow(() -> new BusinessException(BusinessErrorCode.MEMBER_NOT_FOUND,
                        "회원을 찾을 수 없습니다."));
        member.updateMarketingConsent(request.marketingConsent());
        return new MarketingConsentResponse(member.getMarketingConsent());
    }

    @Transactional(readOnly = true)
    public MarketingConsentResponse getMarketingConsent(String externalId) {
        Member member = memberRepository.findByExternalId(externalId)
                .orElseThrow(() -> new BusinessException(BusinessErrorCode.MEMBER_NOT_FOUND,
                        "회원을 찾을 수 없습니다."));
        return new MarketingConsentResponse(member.getMarketingConsent());
    }

}