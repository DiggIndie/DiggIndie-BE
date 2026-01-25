package ceos.diggindie.domain.member.entity;


import ceos.diggindie.common.entity.BaseEntity;
import ceos.diggindie.common.enums.RecommendSatisfactionReason;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "recommend_satisfaction")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RecommendSatisfaction extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "recommend_satisfaction_id")
    private Long id;

    @Column(name = "isSatisfied", nullable = false)
    private Boolean isSatisfied;

    @Enumerated(EnumType.STRING)
    @Column(name = "reason", length = 30)
    private RecommendSatisfactionReason reason;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Builder
    public RecommendSatisfaction(Boolean isSatisfied, RecommendSatisfactionReason reason, Member member) {
        this.isSatisfied = isSatisfied;
        this.reason = reason;
        this.member = member;
    }
}
