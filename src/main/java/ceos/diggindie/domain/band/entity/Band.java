package ceos.diggindie.domain.band.entity;

import ceos.diggindie.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "band")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Band extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "band_id")
    private Long id;

    @Column(name = "band_name", length = 20)
    private String bandName;

    @Column(name = "main_image", length = 200)
    private String mainImage;

    @Column(name = "main_url", length = 200)
    private String mainUrl;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

}