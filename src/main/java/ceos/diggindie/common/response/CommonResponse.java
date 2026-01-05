package ceos.diggindie.common.response;

import ceos.diggindie.common.status.ErrorStatus;
import ceos.diggindie.common.status.SuccessStatus;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import java.util.List;

@Getter
@Builder
@JsonPropertyOrder({"statusCode", "isSuccess", "message", "pageInfo", "payload"})
public class CommonResponse<T> {

    private final Integer statusCode;
    private final Boolean isSuccess;
    private final String message;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final PageInfo pageInfo;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final T payload;

    // ==================== 성공 응답 ====================

    // 성공 - 데이터 없음
    public static ResponseEntity<CommonResponse<Void>> onSuccess(SuccessStatus status) {
        return ResponseEntity
                .status(status.getHttpStatus())
                .body(CommonResponse.<Void>builder()
                        .statusCode(status.getHttpStatus().value())
                        .isSuccess(true)
                        .message(status.getMessage())
                        .build());
    }

    // 성공 - 커스텀 메시지
    public static ResponseEntity<CommonResponse<Void>> onSuccess(SuccessStatus status, String message) {
        return ResponseEntity
                .status(status.getHttpStatus())
                .body(CommonResponse.<Void>builder()
                        .statusCode(status.getHttpStatus().value())
                        .isSuccess(true)
                        .message(message)
                        .build());
    }

    // 성공 - 데이터 포함
    public static <T> ResponseEntity<CommonResponse<T>> onSuccess(SuccessStatus status, T payload) {
        return ResponseEntity
                .status(status.getHttpStatus())
                .body(CommonResponse.<T>builder()
                        .statusCode(status.getHttpStatus().value())
                        .isSuccess(true)
                        .message(status.getMessage())
                        .payload(payload)
                        .build());
    }

    // 성공 - 페이지네이션 포함
    public static <T> ResponseEntity<CommonResponse<List<T>>> onSuccess(SuccessStatus status, Page<T> page) {
        PageInfo pageInfo = new PageInfo(
                page.getNumber(),
                page.getSize(),
                page.hasNext(),
                page.getTotalElements(),
                page.getTotalPages()
        );

        return ResponseEntity
                .status(status.getHttpStatus())
                .body(CommonResponse.<List<T>>builder()
                        .statusCode(status.getHttpStatus().value())
                        .isSuccess(true)
                        .message(status.getMessage())
                        .pageInfo(pageInfo)
                        .payload(page.getContent())
                        .build());
    }

    // ==================== 실패 응답 ====================

    // 실패 - 기본 에러 메시지
    public static ResponseEntity<CommonResponse<Void>> onFailure(ErrorStatus error) {
        return ResponseEntity
                .status(error.getHttpStatus())
                .body(CommonResponse.<Void>builder()
                        .statusCode(error.getHttpStatus().value())
                        .isSuccess(false)
                        .message(error.getMessage())
                        .build());
    }

    // 실패 - 커스텀 에러 메시지
    public static ResponseEntity<CommonResponse<Void>> onFailure(ErrorStatus error, String message) {
        return ResponseEntity
                .status(error.getHttpStatus())
                .body(CommonResponse.<Void>builder()
                        .statusCode(error.getHttpStatus().value())
                        .isSuccess(false)
                        .message(message != null && !message.isBlank() ? message : error.getMessage())
                        .build());
    }
}