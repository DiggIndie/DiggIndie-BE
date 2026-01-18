package ceos.diggindie.domain.member.dto.oauth;

public record GoogleUserResponse(
        String sub,
        String email,
        String name,
        String picture
) {
    public String getId() {
        return sub;
    }

    public String getNickname() {
        if (email != null && email.contains("@")) {
            return email.substring(0, email.indexOf("@"));
        }
        return name;
    }

    public String getProfileImage() {
        return picture;
    }
}