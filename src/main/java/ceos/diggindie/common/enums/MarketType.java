package ceos.diggindie.common.enums;

import ceos.diggindie.common.code.GeneralErrorCode;
import ceos.diggindie.common.exception.GeneralException;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MarketType {
    SELL("sell", "판매"),
    BUY("buy", "구매");

    private final String value;
    private final String displayName;

    @JsonValue
    public String getDisplayName() {
        return displayName;
    }

    @JsonCreator
    public static MarketType from(String input) {
        for (MarketType type : MarketType.values()) {
            if (type.value.equalsIgnoreCase(input) || type.displayName.equals(input)) {
                return type;
            }
        }
        throw new GeneralException(GeneralErrorCode.BAD_REQUEST,
                "지원하지 않는 거래 타입입니다: " + input + ". (sell/buy 또는 판매/구매 중 선택해주세요.)");
    }
}