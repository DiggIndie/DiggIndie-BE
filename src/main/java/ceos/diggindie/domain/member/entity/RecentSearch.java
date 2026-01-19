package ceos.diggindie.domain.member.entity;

import ceos.diggindie.common.entity.BaseEntity;
import ceos.diggindie.common.enums.SearchCategory;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "recent_search")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RecentSearch extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "recent_search_id")
    private Long id;

    @Column(nullable = false, length = 100)
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SearchCategory category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Builder
    public RecentSearch(String content, Member member) {
        this.content = content;
        this.member = member;
    }
}