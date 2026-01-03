package ceos.diggindie.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum LoginPlatform {

    KAKAO("카카오"),
    NAVER("네이버"),
    GOOGLE("구글"),
    LOCAL("로컬");

    private final String description;
}
