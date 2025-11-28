package ceos.diggindie.domain.board.entity.market;

import ceos.diggindie.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "market_scrap")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MarketScrap extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "market_scrap_id")
    private Long id;

    @Column(name = "market_id", nullable = false)
    private Long marketId;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

}