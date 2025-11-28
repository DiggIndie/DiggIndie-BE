package ceos.diggindie.domain.band.entity;

import ceos.diggindie.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "album")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Album extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "album_id")
    private Long id;

    @Column(name = "artist_id", nullable = false)
    private Long artistId;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(name = "album_image", length = 200)
    private String albumImage;

}