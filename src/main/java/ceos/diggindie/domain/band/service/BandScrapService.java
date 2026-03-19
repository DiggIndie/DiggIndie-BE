package ceos.diggindie.domain.band.service;

import ceos.diggindie.common.exception.GeneralException;
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

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BandScrapService {

    private final BandScrapRepository bandScrapRepository;
    private final BandRepository bandRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public void toggleBandScrap(Long memberId, Long bandId) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> GeneralException.notFound("존재하지 않는 회원입니다."));

        Band band = bandRepository.findById(bandId)
                .orElseThrow(() -> GeneralException.notFound("존재하지 않는 밴드입니다."));

        if (bandScrapRepository.existsByMemberIdAndBandId(memberId, bandId)) {
            bandScrapRepository.deleteByMemberIdAndBandId(memberId, bandId);
            return;
        }

        BandScrap scrap = BandScrap.builder()
                .member(member)
                .band(band)
                .build();
        bandScrapRepository.save(scrap);
    }

    public Page<BandScrapResponse.BandScrapInfoDTO> getBandScraps(Long memberId, Pageable pageable) {
        Page<BandScrap> scrapPage = bandScrapRepository.findAllByMemberIdWithKeywords(memberId, pageable);

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