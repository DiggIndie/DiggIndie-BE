package ceos.diggindie.domain.keyword.entity;

import ceos.diggindie.common.entity.BaseEntity;
import ceos.diggindie.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "member_keyword")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberKeyword extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_keyword_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "keyword_id", nullable = false)
    private Keyword keyword;

    @Builder
    public MemberKeyword(Member member, Keyword keyword) {
        this.member = member;
        this.keyword = keyword;
    }

    public static MemberKeyword of(Member member, Keyword keyword) {
        return MemberKeyword.builder()
                .member(member)
                .keyword(keyword)
                .build();
    }
}