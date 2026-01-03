package ceos.diggindie.domain.band.service;

import ceos.diggindie.common.exception.GeneralException;
import ceos.diggindie.domain.band.dto.BandScrapRequest;
import ceos.diggindie.domain.band.dto.BandScrapResponse;
import ceos.diggindie.domain.band.entity.Band;
import ceos.diggindie.domain.band.entity.BandScrap;
import ceos.diggindie.domain.band.repository.BandRepository;
import ceos.diggindie.domain.band.repository.BandScrapRepository;
import ceos.diggindie.domain.member.entity.Member;
import ceos.diggindie.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BandScrapService {

    private final BandScrapRepository bandScrapRepository;
    private final BandRepository bandRepository;

    @Transactional
    public void toggleBandScraps(Long userId, BandScrapRequest request) {

        List<Band> bands = bandRepository.findAllById(request.bandIds());
        if (bands.size() != request.bandIds().size()) {
            throw GeneralException.notFound("존재하지 않는 밴드가 포함되어 있습니다.");
        }

        // 현재 스크랩된 밴드 ID 조회
        List<Long> currentScrapBandIds = bandScrapRepository
                .findAllByMemberId(userId).stream()
                .map(scrap -> scrap.getBand().getId())
                .toList();

        // 토글 처리
        for (Band band : bands) {
            if (currentScrapBandIds.contains(band.getId())) {
                // 이미 스크랩된 밴드 → 삭제
                bandScrapRepository.deleteByMemberIdAndBandId(userId, band.getId());
            } else {
                // 스크랩 안된 밴드 → 추가
                BandScrap scrap = BandScrap.builder()
                        .memberId(userId)
                        .band(band)
                        .build();
                bandScrapRepository.save(scrap);
            }
        }
    }

    public Page<BandScrapResponse.BandScrapInfoDTO> getBandScraps(Long userId, Pageable pageable) {
        Page<BandScrap> scrapPage = bandScrapRepository.findAllByMemberIdWithKeywords(userId, pageable);

        return scrapPage.map(scrap -> {
            Band band = scrap.getBand();

            List<String> keywords = band.getBandKeywords().stream()
                    .map(bk -> bk.getKeyword().getKeyword())
                    .toList();

            return BandScrapResponse.BandScrapInfoDTO.builder()
                    .bandId(band.getId())
                    .bandName(band.getBandName())
                    .keywords(keywords)
                    .bandImage(band.getMainImage())
                    .mainMusic(band.getMainMusic())
                    .build();
        });
    }
}