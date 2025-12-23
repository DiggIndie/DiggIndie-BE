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
    private final MemberRepository memberRepository;
    private final BandRepository bandRepository;

    @Transactional
    public void saveBandScraps(Long userId, BandScrapRequest request) {
        bandScrapRepository.deleteAllByMemberId(userId);

        Member member = memberRepository.findById(userId)
                .orElseThrow(() -> GeneralException.notFound("사용자를 찾을 수 없습니다."));

        List<Band> bands = bandRepository.findAllById(request.bandIds());
        // 위 부분에서 요청 밴드 중 일부가 없어도 예외 발생 안하도록 하는게 맞는지 수정 필요

        List<BandScrap> scraps = bands.stream()
                .map(band -> BandScrap.of(member, band))
                .toList();

        bandScrapRepository.saveAll(scraps);
    }

    public Page<BandScrapResponse.BandScrapInfoDTO> getBandScraps(Long userId, Pageable pageable) {
        Page<BandScrap> scrapPage = bandScrapRepository.findAllByMemberIdWithKeywords(userId, pageable);

        return scrapPage.map(scrap -> {
            Band band = scrap.getBand();

            List<String> keywords = band.getBandKeywords().stream()
                    .map(bk -> bk.getKeyword().getKeyword())
                    .collect(Collectors.toList());

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
