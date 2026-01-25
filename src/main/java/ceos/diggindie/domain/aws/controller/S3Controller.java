package ceos.diggindie.domain.aws.controller;

import ceos.diggindie.common.code.SuccessCode;
import ceos.diggindie.common.response.Response;
import ceos.diggindie.domain.aws.dto.PresignedUrlRequest;
import ceos.diggindie.domain.aws.dto.PresignedUrlResponse;
import ceos.diggindie.domain.aws.service.S3Service;
import ceos.diggindie.domain.member.dto.UserIdCheckResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "S3", description = "파일 업로드 관련 API")
@RestController
@RequiredArgsConstructor
public class S3Controller {

    private final S3Service s3Service;

    @Operation(
            summary = "Presigned URL 발급",
            description = "S3에 파일 업로드를 위한 Presigned URL을 발급합니다. " +
                    "발급받은 URL로 클라이언트가 직접 S3에 파일을 업로드할 수 있습니다."
    )
    @PostMapping("/files/presigned-url")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Response<PresignedUrlResponse>> generatePresignedUrl(
            @Valid @RequestBody PresignedUrlRequest request
    ) {

        PresignedUrlResponse presignedUrlResponse = s3Service.generatePresignedUrl(request);
        Response<PresignedUrlResponse> response = Response.success(
                SuccessCode.GET_SUCCESS,
                presignedUrlResponse,
                "Presigned URL 발급 API"
        );

        return ResponseEntity.ok().body(response);
    }
}
