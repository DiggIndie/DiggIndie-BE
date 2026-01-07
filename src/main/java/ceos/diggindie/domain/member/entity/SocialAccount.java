package ceos.diggindie.domain.member.entity;

import ceos.diggindie.common.entity.BaseEntity;
import ceos.diggindie.common.enums.LoginPlatform;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "social_account",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_social_account_platform",
                        columnNames = {"platform", "platform_id"}
                )
        })
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SocialAccount extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "social_account_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private LoginPlatform platform;

    @Column(name = "platform_id", nullable = false, length = 100)
    private String platformId;

    @Column(length = 100)
    private String email;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Builder
    public SocialAccount(LoginPlatform platform, String platformId,
                         String email, Member member) {
        this.platform = platform;
        this.platformId = platformId;
        this.email = email;
        this.member = member;
    }

    public void updateEmail(String email) {
        this.email = email;
    }
}