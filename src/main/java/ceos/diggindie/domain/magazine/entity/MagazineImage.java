package ceos.diggindie.domain.magazine.entity;

import ceos.diggindie.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "magazine_image")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MagazineImage extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "magazine_image_id")
    private Long id;

    @Column(name = "magazine_id", nullable = false)
    private Long magazineId;

    @Column(name = "image_url", length = 200)
    private String imageUrl;

}