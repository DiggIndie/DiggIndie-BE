package ceos.diggindie.common.response;

import ceos.diggindie.common.code.Code;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;

import java.util.List;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Response<T> {

    private int statusCode;

    @Getter(AccessLevel.NONE)
    private boolean isSuccess;

    private String message;

    private PageInfo pageInfo;

    private T payload;

    @JsonProperty("isSuccess")
    public boolean isSuccess() {
        return isSuccess;
    }

    // ==================== 성공 응답 ====================

    // 성공 - 데이터 없음
    public static ResponseEntity<Response<Void>> success(Code code) {
        return ResponseEntity
                .status(code.getStatusCode())
                .body(Response.<Void>builder()
                        .statusCode(code.getStatusCode())
                        .isSuccess(true)
                        .message(code.getMessage())
                        .build());
    }

    // 성공 - 커스텀 메시지
    public static ResponseEntity<Response<Void>> success(Code code, String message) {
        return ResponseEntity
                .status(code.getStatusCode())
                .body(Response.<Void>builder()
                        .statusCode(code.getStatusCode())
                        .isSuccess(true)
                        .message(message)
                        .build());
    }

    // 성공 - 데이터 포함
    public static <T> ResponseEntity<Response<T>> success(Code code, T payload) {
        return ResponseEntity
                .status(code.getStatusCode())
                .body(Response.<T>builder()
                        .statusCode(code.getStatusCode())
                        .isSuccess(true)
                        .message(code.getMessage())
                        .payload(payload)
                        .build());
    }

    // 성공 - 페이지네이션 포함
    public static <T> ResponseEntity<Response<List<T>>> success(Code code, Page<T> page) {
        PageInfo pageInfo = new PageInfo(
                page.getNumber(),
                page.getSize(),
                page.hasNext(),
                page.getTotalElements(),
                page.getTotalPages()
        );

        return ResponseEntity
                .status(code.getStatusCode())
                .body(Response.<List<T>>builder()
                        .statusCode(code.getStatusCode())
                        .isSuccess(true)
                        .message(code.getMessage())
                        .pageInfo(pageInfo)
                        .payload(page.getContent())
                        .build());
    }

    // ==================== 실패 응답 ====================

    // 실패 - 기본 에러 메시지
    public static ResponseEntity<Response<Void>> fail(Code code) {
        return ResponseEntity
                .status(code.getStatusCode())
                .body(Response.<Void>builder()
                        .statusCode(code.getStatusCode())
                        .isSuccess(false)
                        .message(code.getMessage())
                        .build());
    }

    // 실패 - 커스텀 에러 메시지
    public static ResponseEntity<Response<Void>> fail(Code code, String message) {
        return ResponseEntity
                .status(code.getStatusCode())
                .body(Response.<Void>builder()
                        .statusCode(code.getStatusCode())
                        .isSuccess(false)
                        .message(message != null && !message.isBlank() ? message : code.getMessage())
                        .build());
    }

    // ==================== 기존 of 메서드 (하위 호환) ====================

    // Non-paginated
    public static <T> Response<T> of(Code code, boolean isSuccess, T payload) {
        return Response.<T>builder()
                .statusCode(code.getStatusCode())
                .isSuccess(isSuccess)
                .message(code.getMessage())
                .payload(payload)
                .build();
    }

    public static <T> Response<T> of(Code code, boolean isSuccess, String message, T payload) {
        return Response.<T>builder()
                .statusCode(code.getStatusCode())
                .isSuccess(isSuccess)
                .message(message)
                .payload(payload)
                .build();
    }

    // Paginated
    public static <T> Response<T> of(Code code, boolean isSuccess, T payload, PageInfo pageInfo) {
        return Response.<T>builder()
                .statusCode(code.getStatusCode())
                .isSuccess(isSuccess)
                .message(code.getMessage())
                .pageInfo(pageInfo)
                .payload(payload)
                .build();
    }

    public static <T> Response<T> of(Code code, boolean isSuccess, String message, T payload, PageInfo pageInfo) {
        return Response.<T>builder()
                .statusCode(code.getStatusCode())
                .isSuccess(isSuccess)
                .message(message)
                .pageInfo(pageInfo)
                .payload(payload)
                .build();
    }
}
