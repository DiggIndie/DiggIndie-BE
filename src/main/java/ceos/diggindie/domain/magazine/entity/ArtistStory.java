package ceos.diggindie.domain.magazine.entity;

import ceos.diggindie.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "artist_story")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ArtistStory extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "artist_story_id")
    private Long id;

    @Column(name = "band_id", nullable = false)
    private Long bandId;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

}