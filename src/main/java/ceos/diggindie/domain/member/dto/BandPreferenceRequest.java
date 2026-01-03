package ceos.diggindie.domain.member.dto;

import jakarta.validation.constraints.NotNull;

import java.util.List;

public record BandPreferenceRequest(
        @NotNull(message = "밴드 ID 리스트는 필수입니다.")
        List<Long> bands
) {}
