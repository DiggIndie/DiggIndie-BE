package ceos.diggindie.domain.band.dto;

import ceos.diggindie.domain.band.entity.Band;
import ceos.diggindie.domain.band.entity.BandRecommend;
import ceos.diggindie.domain.band.entity.TopTrack;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

public class BandRecommendResponse {

    @Getter
    @Builder
    public static class TopTrackInfo {
        private String title;
        private String externalUrl;

        public static TopTrackInfo from(TopTrack topTrack) {
            if (topTrack == null) {
                return null;
            }
            return TopTrackInfo.builder()
                    .title(topTrack.getTitle())
                    .externalUrl(topTrack.getExternalUrl())
                    .build();
        }
    }

    @Getter
    @Builder
    public static class BandInfo {
        private Long bandId;
        private String bandName;
        private String imageUrl;
        private TopTrackInfo topTrack;
        private List<String> keyword;
        private Float score;

        public static BandInfo from(BandRecommend bandRecommend) {
            Band band = bandRecommend.getBand();
            List<String> keywords = band.getBandKeywords().stream()
                    .map(bk -> bk.getKeyword().getKeyword())
                    .toList();

            return BandInfo.builder()
                    .bandId(band.getId())
                    .bandName(band.getBandName())
                    .imageUrl(band.getMainImage())
                    .topTrack(TopTrackInfo.from(band.getTopTrack()))
                    .keyword(keywords)
                    .score(bandRecommend.getScore())
                    .build();
        }
    }

    @Getter
    @Builder
    public static class BandListDTO {
        private List<BandInfo> bands;

        public static BandListDTO from(List<BandRecommend> bandRecommends) {
            List<BandInfo> bandInfos = bandRecommends.stream()
                    .map(BandInfo::from)
                    .toList();

            return BandListDTO.builder()
                    .bands(bandInfos)
                    .build();
        }
    }
}
