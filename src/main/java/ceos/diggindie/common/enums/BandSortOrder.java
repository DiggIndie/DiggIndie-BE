package ceos.diggindie.common.enums;

import ceos.diggindie.common.code.GeneralErrorCode;
import ceos.diggindie.common.exception.GeneralException;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum BandSortOrder {
    recent("recent"),
    alphabet("alphabet"),
    scrap("scrap");

    private final String value;

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static BandSortOrder from(String value) {
        for (BandSortOrder order : BandSortOrder.values()) {
            if (order.value.equalsIgnoreCase(value)) {
                return order;
            }
        }
        throw new GeneralException(GeneralErrorCode.BAD_REQUEST,
                "지원하지 않는 정렬 타입입니다: " + value + ". (recent, alphabet, scrap 중 선택해주세요.)");
    }
}

