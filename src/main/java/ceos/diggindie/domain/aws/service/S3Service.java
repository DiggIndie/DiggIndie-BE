package ceos.diggindie.domain.aws.service;

import ceos.diggindie.domain.aws.dto.PresignedUrlRequest;
import ceos.diggindie.domain.aws.dto.PresignedUrlResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3Service {

    private final S3Presigner s3Presigner;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    private static final long PRESIGNED_URL_EXPIRATION_MINUTES = 10;
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmssSSS");

    public PresignedUrlResponse generatePresignedUrl(PresignedUrlRequest request) {
        String fileKey = generateFileKey(request.fileName());

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(fileKey)
                .build();

        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(PRESIGNED_URL_EXPIRATION_MINUTES))
                .putObjectRequest(putObjectRequest)
                .build();

        PresignedPutObjectRequest presignedRequest = s3Presigner.presignPutObject(presignRequest);

        log.info("Presigned URL 생성 완료 - fileKey: {}, expiresIn: {}분", fileKey, PRESIGNED_URL_EXPIRATION_MINUTES);

        return PresignedUrlResponse.builder()
                .presignedUrl(presignedRequest.url().toString())
                .fileKey(fileKey)
                .expiresIn(PRESIGNED_URL_EXPIRATION_MINUTES * 60)
                .build();
    }


    private String generateFileKey(String fileName) {
        String timestamp = LocalDateTime.now().format(DATE_TIME_FORMATTER);
        return String.format("%s_%s", timestamp, fileName);
    }
}
