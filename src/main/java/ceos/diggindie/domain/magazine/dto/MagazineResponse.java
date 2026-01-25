package ceos.diggindie.domain.magazine.dto;

import ceos.diggindie.common.response.PageInfo;
import ceos.diggindie.domain.magazine.entity.Magazine;
import ceos.diggindie.domain.magazine.entity.MagazineImage;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.List;

public class MagazineResponse {

    public record MagazineInfo(
            Long magazineId,
            String title,
            String content,
            String externalUrl,
            Long view,
            List<String> imageUrls,
            LocalDateTime createdAt
    ) {
        public static MagazineInfo from(Magazine magazine) {
            List<String> imageUrls = magazine.getMagazineImages().stream()
                    .map(MagazineImage::getImageUrl)
                    .toList();

            return new MagazineInfo(
                    magazine.getId(),
                    magazine.getTitle(),
                    magazine.getContent(),
                    magazine.getExternalUrl(),
                    magazine.getView(),
                    imageUrls,
                    magazine.getCreatedAt()
            );
        }
    }

    public record MagazineListDTO(
            List<MagazineInfo> magazines,
            PageInfo pageInfo
    ) {
        public static MagazineListDTO from(Page<Magazine> magazinePage) {
            List<MagazineInfo> magazineInfos = magazinePage.getContent().stream()
                    .map(MagazineInfo::from)
                    .toList();

            PageInfo pageInfo = new PageInfo(
                    magazinePage.getNumber(),
                    magazinePage.getSize(),
                    magazinePage.hasNext(),
                    magazinePage.getTotalElements(),
                    magazinePage.getTotalPages()
            );

            return new MagazineListDTO(magazineInfos, pageInfo);
        }
    }
}

