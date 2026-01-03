package ceos.diggindie.domain.member.service;

import ceos.diggindie.common.exception.GeneralException;
import ceos.diggindie.domain.band.dto.BandListResponse;
import ceos.diggindie.domain.band.entity.Band;
import ceos.diggindie.domain.band.repository.BandRepository;
import ceos.diggindie.domain.member.dto.BandPreferenceRequest;
import ceos.diggindie.domain.member.dto.BandPreferenceResponse;
import ceos.diggindie.domain.member.entity.MemberBand;
import ceos.diggindie.domain.member.repository.MemberBandRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberBandService {

    private final MemberBandRepository memberBandRepository;
    private final BandRepository bandRepository;  // memberRepository 제거

    @Transactional
    public void saveBandPreferences(Long userId, BandPreferenceRequest request) {
        // 1. 기존 취향 삭제
        memberBandRepository.deleteAllByMemberId(userId);

        // 2. 밴드 조회 및 검증
        List<Band> bands = bandRepository.findAllById(request.bands());
        if (bands.size() != request.bands().size()) {
            throw GeneralException.notFound("일부 밴드를 찾을 수 없습니다.");
        }

        // 3. 새로운 취향 저장 (memberId만 사용)
        List<MemberBand> memberBands = bands.stream()
                .map(band -> MemberBand.of(userId, band))
                .toList();

        memberBandRepository.saveAll(memberBands);
    }

    public BandPreferenceResponse getBandPreferences(Long userId) {
        List<MemberBand> memberBands = memberBandRepository.findAllByMemberIdWithBand(userId);

        List<BandListResponse> bands = memberBands.stream()
                .map(mb -> BandListResponse.from(mb.getBand()))
                .toList();

        return BandPreferenceResponse.of(bands);
    }
}