package ceos.diggindie.domain.user.domain;

import ceos.diggindie.common.util.LoginPlatform;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "member")
@Getter
@NoArgsConstructor
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @Column(name = "external_id", length = 36, nullable = false, unique = true)
    private String externalId;

    @Enumerated(EnumType.STRING)
    @Column(name = "recent_login_platform")
    private LoginPlatform recentLoginPlatform;

    @Column(name = "nickname", length = 50, nullable = false)
    private String nickname;

    @Column(name = "password", length = 100)
    private String password;

    @Column(name = "email", length = 100, nullable = false, unique = true)
    private String email;

    @Column(name = "phone", length = 20, nullable = false)
    private String phone;

    @Column(name = "profile_img", length = 150)
    private String profileImg;

    @Column(name = "salt", length = 200)
    private String salt;

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted;

}
