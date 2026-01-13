package ceos.diggindie.domain.concert.service;

import ceos.diggindie.common.code.BusinessErrorCode;
import ceos.diggindie.common.exception.BusinessException;
import ceos.diggindie.domain.concert.dto.ConcertScrapResponse;
import ceos.diggindie.domain.concert.entity.Concert;
import ceos.diggindie.domain.concert.entity.ConcertScrap;
import ceos.diggindie.domain.concert.repository.ConcertRepository;
import ceos.diggindie.domain.concert.repository.ConcertScrapRepository;
import ceos.diggindie.domain.concert.util.ConcertDDayCalculator;
import ceos.diggindie.domain.member.entity.Member;
import ceos.diggindie.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ConcertScrapService {

    private final ConcertScrapRepository concertScrapRepository;
    private final ConcertRepository concertRepository;
    private final MemberRepository memberRepository;

    public ConcertScrapResponse.ConcertScrapListDTO getMyScrappedConcerts(Long memberId) {
        List<ConcertScrap> scraps = concertScrapRepository.findAllByMemberIdWithConcert(memberId);

        List<ConcertScrapResponse.ConcertScrapInfoDTO> concertInfos = scraps.stream()
                .map(scrap -> {
                    Concert concert = scrap.getConcert();
                    LocalDate startDate = concert.getStartDate().toLocalDate();
                    LocalDate endDate = (concert.getEndDate() != null) ? concert.getEndDate().toLocalDate() : startDate;

                    return ConcertScrapResponse.ConcertScrapInfoDTO.builder()
                            .concertId(concert.getId())
                            .concertName(concert.getTitle())
                            .duration(formatDuration(startDate, endDate))
                            .dDay(ConcertDDayCalculator.calculate(startDate, endDate))
                            .imageUrl(concert.getMainImg())
                            .isFinished(concert.getEndDate() != null && concert.getEndDate().isBefore(LocalDateTime.now()))
                            .build();
                })
                .collect(Collectors.toList());

        return ConcertScrapResponse.ConcertScrapListDTO.builder()
                .concerts(concertInfos)
                .build();
    }

    @Transactional
    public ConcertScrapResponse.ConcertScrapCreateDTO createConcertScrap(Long memberId, Long concertId) {
        // 이미 스크랩한 경우 예외 처리
        if (concertScrapRepository.existsByMemberIdAndConcertId(memberId, concertId)) {
            throw new BusinessException(BusinessErrorCode.ALREADY_SCRAPPED);
        }

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BusinessException(BusinessErrorCode.MEMBER_NOT_FOUND));

        Concert concert = concertRepository.findById(concertId)
                .orElseThrow(() -> new BusinessException(BusinessErrorCode.CONCERT_NOT_FOUND));

        ConcertScrap concertScrap = ConcertScrap.builder()
                .member(member)
                .concert(concert)
                .build();

        concertScrapRepository.save(concertScrap);

        return ConcertScrapResponse.ConcertScrapCreateDTO.builder()
                .memberId(member.getExternalId())
                .concertId(concert.getId())
                .build();
    }

    @Transactional
    public ConcertScrapResponse.ConcertScrapCreateDTO deleteConcertScrap(Long memberId, Long concertId) {

        if (!concertScrapRepository.existsByMemberIdAndConcertId(memberId, concertId)) {
            throw new BusinessException(BusinessErrorCode.SCRAP_NOT_FOUND);
        }

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BusinessException(BusinessErrorCode.MEMBER_NOT_FOUND));

        concertScrapRepository.deleteByMemberIdAndConcertId(memberId, concertId);

        return ConcertScrapResponse.ConcertScrapCreateDTO.builder()
                .memberId(member.getExternalId())
                .concertId(concertId)
                .build();
    }


    private static final DateTimeFormatter FULL_FORMAT = DateTimeFormatter.ofPattern("yyyy.MM.d");
    private static final DateTimeFormatter MONTH_DAY_FORMAT = DateTimeFormatter.ofPattern("MM.d");

    private String formatDuration(LocalDate start, LocalDate end) {
        // 연도와 월이 같은 경우: "2025.12.1 ~ 3"
        if (start.getYear() == end.getYear() && start.getMonth() == end.getMonth()) {
            return start.format(FULL_FORMAT) + " ~ " + end.getDayOfMonth();
        }

        // 연도만 같은 경우: "2025.12.1 ~ 01.05"
        if (start.getYear() == end.getYear()) {
            return start.format(FULL_FORMAT) + " ~ " + end.format(MONTH_DAY_FORMAT);
        }

        // 연도가 다른 경우: "2025.12.31 ~ 2026.01.01"
        return start.format(FULL_FORMAT) + " ~ " + end.format(FULL_FORMAT);
    }
}