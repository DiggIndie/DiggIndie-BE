package ceos.diggindie.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Optional;

@Getter
@RequiredArgsConstructor
public enum LoginPlatform {

    KAKAO("카카오"),
    NAVER("네이버"),
    GOOGLE("구글"),
    LOCAL("로컬");

    private final String description;

    public static Optional<LoginPlatform> fromName(String name) {
        if (name == null || name.isBlank()) {
            return Optional.empty();
        }

        return Arrays.stream(values())
                .filter(p -> p.name().equalsIgnoreCase(name))
                .findFirst();
    }
}