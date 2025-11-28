package ceos.diggindie.domain.member.entity;

import ceos.diggindie.common.entity.BaseEntity;
import ceos.diggindie.common.enums.LoginPlatform;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "member")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @Column(name = "external_id", nullable = false, length = 36)
    private String externalId;

    @Enumerated(EnumType.STRING)
    @Column(name = "recent_login_platform")
    private LoginPlatform recentLoginPlatform;

    @Column(nullable = false, length = 50)
    private String nickname;

    @Column(length = 100)
    private String password;

    @Column(nullable = false, length = 100)
    private String email;

    @Column(nullable = false, length = 20)
    private String phone;

    @Column(name = "profile_img", length = 150)
    private String profileImg;

    @Column(length = 200)
    private String salt;

}