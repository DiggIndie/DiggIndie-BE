package ceos.diggindie.domain.member.service;

import ceos.diggindie.common.code.BusinessErrorCode;
import ceos.diggindie.common.exception.BusinessException;
import ceos.diggindie.domain.member.dto.RecommendSatisfactionRequest;
import ceos.diggindie.domain.member.dto.RecommendSatisfactionResponse;
import ceos.diggindie.domain.member.entity.Member;
import ceos.diggindie.domain.member.entity.RecommendSatisfaction;
import ceos.diggindie.domain.member.repository.MemberRepository;
import ceos.diggindie.domain.member.repository.RecommendSatisfactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RecommendSatisfactionService {

    private final RecommendSatisfactionRepository recommendSatisfactionRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public RecommendSatisfactionResponse.RecommendSatisfactionInfo addRecommendSatisfaction(
            Long memberId, RecommendSatisfactionRequest request) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BusinessException(BusinessErrorCode.MEMBER_NOT_FOUND));

        RecommendSatisfaction recommendSatisfaction = RecommendSatisfaction.builder()
                .isSatisfied(request.isSatisfied())
                .reason(request.reason())
                .member(member)
                .build();

        RecommendSatisfaction saved = recommendSatisfactionRepository.save(recommendSatisfaction);
        return RecommendSatisfactionResponse.RecommendSatisfactionInfo.from(saved);
    }
}

