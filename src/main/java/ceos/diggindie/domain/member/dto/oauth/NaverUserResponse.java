package ceos.diggindie.domain.member.dto.oauth;

import com.fasterxml.jackson.annotation.JsonProperty;

public record NaverUserResponse(
        Response response
) {
    public record Response(
            String id,
            String email,
            String nickname,
            String name,
            @JsonProperty("profile_image") String profileImage
    ) {}

    public String getId() {
        return response != null ? response.id() : null;
    }

    public String getEmail() {
        return response != null ? response.email() : null;
    }

    public String getNickname() {
        if (response == null) return null;
        return response.nickname() != null ? response.nickname() : response.name();
    }

    public String getProfileImage() {
        return response != null ? response.profileImage() : null;
    }
}