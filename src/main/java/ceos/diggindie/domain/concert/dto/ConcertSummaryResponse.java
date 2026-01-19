package ceos.diggindie.domain.concert.dto;

import lombok.Builder;
import java.util.List;

@Builder
public record ConcertSummaryResponse(
        Long concertId,
        String concertName,
        String concertImage,
        String dDay,  // 예정 공연만 해당, 완료 공연은 null
        List<String> lineUp,
        String concertDate
) {
}