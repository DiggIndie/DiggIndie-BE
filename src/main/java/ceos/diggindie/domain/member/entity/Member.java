package ceos.diggindie.domain.member.entity;

import ceos.diggindie.common.entity.BaseEntity;
import ceos.diggindie.common.enums.LoginPlatform;
import ceos.diggindie.common.enums.Role;
import ceos.diggindie.domain.band.entity.BandRecommend;
import ceos.diggindie.domain.band.entity.BandScrap;
import ceos.diggindie.domain.board.entity.board.Board;
import ceos.diggindie.domain.board.entity.board.BoardLike;
import ceos.diggindie.domain.board.entity.market.Market;
import ceos.diggindie.domain.board.entity.market.MarketScrap;
import ceos.diggindie.domain.concert.entity.ConcertScrap;
import ceos.diggindie.domain.keyword.entity.MemberKeyword;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "member")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(name = "external_id", nullable = false, length = 36, unique = true)
    private String externalId;

    @Enumerated(EnumType.STRING)
    @Column(name = "recent_login_platform")
    private LoginPlatform recentLoginPlatform;

    @Column(nullable = false, length = 50)
    private String userId;

    @Column(length = 100)
    private String password;

    @Column(nullable = false, length = 100, unique = true)
    private String email;

    @Column(length = 20)
    private String phone;

    @Column(name = "profile_img", length = 150)
    private String profileImg;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SocialAccount> socialAccounts = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MemberBand> memberBands = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MemberKeyword> memberKeywords = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RecentSearch> recentSearches = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BandRecommend> artistRecommends = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BandScrap> artistScraps = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ConcertScrap> concertScraps = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MarketScrap> marketScraps = new ArrayList<>();

    @OneToMany(mappedBy = "member")
    private List<Board> boards = new ArrayList<>();

    @OneToMany(mappedBy = "member")
    private List<BoardLike> boardLikes = new ArrayList<>();

    @OneToMany(mappedBy = "member")
    private List<Market> markets = new ArrayList<>();

    @Builder
    public Member(String userId, String password, String email, String phone) {
        this.externalId = UUID.randomUUID().toString();
        this.userId = userId;
        this.password = password;
        this.email = email;
        this.role = Role.ROLE_USER;
        this.phone = phone;
    }

    public void updatePassword(String password) {
        this.password = password;
    }

}