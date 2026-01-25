package ceos.diggindie.domain.concert.service;

import ceos.diggindie.domain.band.entity.Band;
import ceos.diggindie.domain.band.repository.BandRepository;
import ceos.diggindie.domain.concert.dto.ConcertCsvRow;
import ceos.diggindie.domain.concert.entity.BandConcert;
import ceos.diggindie.domain.concert.entity.Concert;
import ceos.diggindie.domain.concert.entity.ConcertHall;
import ceos.diggindie.domain.concert.repository.BandConcertRepository;
import ceos.diggindie.domain.concert.repository.ConcertHallRepository;
import ceos.diggindie.domain.concert.repository.ConcertRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConcertAppender {

    private final ConcertRepository concertRepository;
    private final ConcertHallRepository hallRepository;
    private final BandRepository bandRepository;
    private final BandConcertRepository bandConcertRepository;

    private static final String IMAGE_DOMAIN = "https://cdn.indistreet.app";

    /**
     * CSV 한 줄을 기반으로 공연 정보를 저장한다.
     * 실패 시 해당 Row는 롤백된다.
     */
    @Transactional
    public boolean appendConcert(ConcertCsvRow row) {
        // 필수값 검증
        if (row.getTitle() == null || row.getTitle().isBlank()) return false;

        LocalDateTime startDate = parseDateTime(row.getStartDate());
        if (startDate == null) return false;

        // 중복 공연 스킵
        if (concertRepository.existsByTitleAndStartDate(row.getTitle(), startDate)) {
            log.debug("중복 스킵: {}", row.getTitle());
            return false;
        }

        // 공연장 조회 또는 생성
        ConcertHall hall = findOrCreateHall(row.getVenue(), row.getAddress());

        // 공연 저장
        Concert concert = Concert.builder()
                .title(truncate(row.getTitle(), 200))
                .description(row.getDescription())
                .startDate(startDate)
                .endDate(parseDateTime(row.getEndDate()))
                .mainImg(fixUrl(row.getPosterUrl()))
                .mainUrl(row.getUrl())
                .bookUrl(row.getTicketLink())
                .preorderPrice(parsePrice(row.getPreorderPrice()))
                .onSitePrice(parsePrice(row.getOnsitePrice()))
                .concertHall(hall)
                .build();

        concertRepository.save(concert);

        // 출연진 저장 및 매핑
        savePerformers(row.getPerformers(), concert);

        log.info("저장 완료: {}", row.getTitle());
        return true;
    }

    private ConcertHall findOrCreateHall(String name, String address) {
        if (name == null || name.isBlank()) name = "미정";

        String finalName = truncate(name, 100);
        String finalAddress = address != null ? truncate(address, 300) : "";

        return hallRepository.findByName(finalName)
                .orElseGet(() -> hallRepository.save(
                        ConcertHall.create(finalName, finalAddress)
                ));
    }

    private void savePerformers(String performersStr, Concert concert) {
        if (performersStr == null || performersStr.isBlank()) return;

        String[] names = performersStr.split("[,/]\\s*");

        for (String rawName : names) {
            final String name = rawName.trim();
            if (name.isEmpty()) continue;

            Band band = bandRepository.findByBandName(name)
                    .orElseGet(() -> bandRepository.save(
                            Band.builder()
                                    .bandName(name)
                                    .description(name)
                                    .build()
                    ));

            if (!bandConcertRepository.existsByBandAndConcert(band, concert)) {
                bandConcertRepository.save(BandConcert.of(band, concert));
            }
        }
    }

    // ---------- util ----------

    private LocalDateTime parseDateTime(String dateStr) {
        if (dateStr == null || dateStr.isBlank()) return null;
        try {
            return dateStr.contains("T")
                    ? OffsetDateTime.parse(dateStr).toLocalDateTime()
                    : LocalDateTime.parse(dateStr);
        } catch (Exception e) {
            log.warn("날짜 파싱 실패: {}", dateStr);
            return null;
        }
    }

    private Integer parsePrice(String priceStr) {
        if (priceStr == null || priceStr.isBlank()) return null;
        try {
            String cleanStr = priceStr.replaceAll("[^0-9.]", "");
            if (cleanStr.isEmpty()) return null;
            return (int) Double.parseDouble(cleanStr);
        } catch (Exception e) {
            log.warn("가격 파싱 실패: {}", priceStr);
            return null;
        }
    }

    private String truncate(String str, int maxLength) {
        if (str == null) return null;
        return str.length() > maxLength ? str.substring(0, maxLength) : str;
    }

    private String fixUrl(String url) {
        if (url == null || url.isBlank()) return null;
        if (url.startsWith("http")) return url;
        if (url.startsWith("/")) return IMAGE_DOMAIN + url;
        return url;
    }
}
