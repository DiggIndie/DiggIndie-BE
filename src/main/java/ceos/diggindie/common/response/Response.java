package ceos.diggindie.common.response;

import ceos.diggindie.common.code.Code;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Response<T> {

    // API 상태 코드
    private int statusCode;

    // API 성공 여부
    @Getter(AccessLevel.NONE)
    private boolean isSuccess;

    // API 관련 메세지
    private String message;

    // API 응답
    private T payload;

    @JsonProperty("isSuccess")
    public boolean isSuccess() {
        return isSuccess;
    }

    public static <T> Response<T> of(Code code, boolean isSuccess, T payload) {
        return Response.<T>builder()
                .statusCode(code.getStatusCode())
                .isSuccess(isSuccess)
                .message(code.getMessage())
                .payload(payload)
                .build();
    }
}
