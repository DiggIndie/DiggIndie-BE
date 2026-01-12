package ceos.diggindie.domain.band.dto;

import ceos.diggindie.common.response.PageInfo;
import ceos.diggindie.domain.band.entity.Band;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.List;

public class BandSearchResponse {

    @Getter
    @Builder
    public static class ArtistInfo {
        private Long artistId;
        private String artistName;
        private List<String> keywords;
        private String artistImage;
        private String topTrack;

        public static ArtistInfo from(Band band) {
            List<String> keywordList = band.getBandKeywords().stream()
                    .map(bk -> bk.getKeyword().getKeyword())
                    .toList();

            return ArtistInfo.builder()
                    .artistId(band.getId())
                    .artistName(band.getBandName())
                    .keywords(keywordList)
                    .artistImage(band.getMainImage())
                    .topTrack(band.getMainMusic())
                    .build();
        }
    }

    @Getter
    @Builder
    public static class ArtistListDTO {
        private List<ArtistInfo> artists;
        private PageInfo pageInfo;

        public static ArtistListDTO from(Page<Band> bandPage) {
            List<ArtistInfo> artistInfos = bandPage.getContent().stream()
                    .map(ArtistInfo::from)
                    .toList();

            PageInfo pageInfo = new PageInfo(
                    bandPage.getNumber(),
                    bandPage.getSize(),
                    bandPage.hasNext(),
                    bandPage.getTotalElements(),
                    bandPage.getTotalPages()
            );

            return ArtistListDTO.builder()
                    .artists(artistInfos)
                    .pageInfo(pageInfo)
                    .build();
        }
    }
}
