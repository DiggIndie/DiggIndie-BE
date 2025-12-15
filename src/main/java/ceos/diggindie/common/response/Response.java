package ceos.diggindie.common.response;

import ceos.diggindie.common.code.Code;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

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

    // Non-paginated
    public static <T> Response<T> of(Code code, boolean isSuccess, T payload) {
        return Response.<T>builder()
                .statusCode(code.getStatusCode())
                .isSuccess(isSuccess)
                .message(code.getMessage())
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
}
