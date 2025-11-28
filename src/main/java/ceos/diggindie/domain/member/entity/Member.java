package ceos.diggindie.domain.member.entity;

import ceos.diggindie.common.entity.BaseEntity;
import ceos.diggindie.common.enums.LoginPlatform;
import ceos.diggindie.domain.band.entity.ArtistRecommend;
import ceos.diggindie.domain.band.entity.ArtistScrap;
import ceos.diggindie.domain.board.entity.board.Board;
import ceos.diggindie.domain.board.entity.board.BoardLike;
import ceos.diggindie.domain.board.entity.market.Market;
import ceos.diggindie.domain.board.entity.market.MarketScrap;
import ceos.diggindie.domain.concert.entity.ConcertScrap;
import ceos.diggindie.domain.keyword.entity.MemberKeyword;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "member")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @Column(name = "external_id", nullable = false, length = 36, unique = true)
    private String externalId;

    @Enumerated(EnumType.STRING)
    @Column(name = "recent_login_platform")
    private LoginPlatform recentLoginPlatform;

    @Column(nullable = false, length = 50)
    private String nickname;

    @Column(length = 100)
    private String password;

    @Column(nullable = false, length = 100, unique = true)
    private String email;

    @Column(nullable = false, length = 20)
    private String phone;

    @Column(name = "profile_img", length = 150)
    private String profileImg;

    @Column(length = 200)
    private String salt;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SocialAccount> socialAccounts = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MemberBand> memberBands = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MemberKeyword> memberKeywords = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RecentSearch> recentSearches = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ArtistRecommend> artistRecommends = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ArtistScrap> artistScraps = new ArrayList<>();

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

}