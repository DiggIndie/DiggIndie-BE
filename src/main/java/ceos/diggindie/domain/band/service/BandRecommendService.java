package ceos.diggindie.domain.band.service;

import ceos.diggindie.domain.band.dto.BandRecommendResponse;
import ceos.diggindie.domain.band.entity.BandRecommend;
import ceos.diggindie.domain.band.repository.BandRecommendRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BandRecommendService {

    private final BandRecommendRepository bandRecommendRepository;

    @Transactional(readOnly = true)
    public BandRecommendResponse.BandListDTO getRecommendedBands(Long memberId) {
        List<BandRecommend> bandRecommends = bandRecommendRepository.findByMemberIdWithBandAndTopTrack(memberId);
        return BandRecommendResponse.BandListDTO.from(bandRecommends);
    }
}
