package ceos.diggindie.domain.aws.dto;

import lombok.Builder;

@Builder
public record PresignedUrlResponse(
        String presignedUrl,
        String fileKey,
        Long expiresIn
) {
}
