package ceos.diggindie.domain.concert.service;

import ceos.diggindie.domain.concert.dto.ConcertScrapResponse;
import ceos.diggindie.domain.concert.entity.Concert;
import ceos.diggindie.domain.concert.entity.ConcertScrap;
import ceos.diggindie.domain.concert.repository.ConcertScrapRepository;
import ceos.diggindie.domain.member.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ConcertScrapService {

    private final ConcertScrapRepository concertScrapRepository;

    public ConcertScrapResponse.ConcertScrapListDTO getMyScrappedConcerts(Long userId) {
        List<ConcertScrap> scraps = concertScrapRepository.findAllByMemberIdWithConcert(userId);

        List<ConcertScrapResponse.ConcertScrapInfoDTO> concertInfos = scraps.stream()
                .map(scrap -> {
                    Concert concert = scrap.getConcert();
                    LocalDate startDate = concert.getStartDate().toLocalDate();
                    LocalDate endDate = (concert.getEndDate() != null) ? concert.getEndDate().toLocalDate() : startDate;

                    return ConcertScrapResponse.ConcertScrapInfoDTO.builder()
                            .concertId(concert.getId())
                            .concertName(concert.getTitle())
                            .duration(formatDuration(startDate, endDate))
                            .dDay(calculateDDay(startDate, endDate))
                            .imageUrl(concert.getMainImg())
                            .isFinished(concert.getEndDate() != null && concert.getEndDate().isBefore(LocalDateTime.now()))
                            .build();
                })
                .collect(Collectors.toList());

        return ConcertScrapResponse.ConcertScrapListDTO.builder()
                .concerts(concertInfos)
                .build();
    }

    private String calculateDDay(LocalDate startDate, LocalDate endDate) {
        LocalDate now = LocalDate.now();
        if (now.isBefore(startDate)) {
            long days = ChronoUnit.DAYS.between(now, startDate);
            return days == 0 ? "D-Day" : "D-" + days;
        } else if (now.isAfter(endDate)) {
            return "종료";
        } else {
            return "진행 중";
        }
    }

    private String formatDuration(LocalDate start, LocalDate end) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.d");

        if (start.getYear() == end.getYear() && start.getMonth() == end.getMonth()) {
            return start.format(formatter) + " ~ " + end.getDayOfMonth();
        }
        if (start.getYear() == end.getYear()) {
            return start.format(formatter) + " ~ " + end.format(DateTimeFormatter.ofPattern("MM.d"));
        }
        return start.format(formatter) + " ~ " + end.format(formatter);
    }
}