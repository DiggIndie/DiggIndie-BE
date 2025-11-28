package ceos.diggindie.domain.board.entity.market;

import ceos.diggindie.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "market_image")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MarketImage extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "market_image_id")
    private Long id;

    @Column(name = "image_url", nullable = false, length = 200)
    private String imageUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "market_id", nullable = false)
    private Market market;
}