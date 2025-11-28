package ceos.diggindie.domain.keyword.entity;

import ceos.diggindie.common.entity.BaseEntity;
import ceos.diggindie.domain.band.entity.Band;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "artist_keyword")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ArtistKeyword extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "artist_keyword_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "artist_id", nullable = false)
    private Band band;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "keyword_id", nullable = false)
    private Keyword keyword;

}