package ceos.diggindie.domain.keyword.entity;

import ceos.diggindie.common.entity.BaseEntity;
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

    @Column(name = "artist_id", nullable = false)
    private Long artistId;

    @Column(name = "keyword_id", nullable = false)
    private Long keywordId;

}