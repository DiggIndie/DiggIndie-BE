package ceos.diggindie.domain.band.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "bands_raw_data")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BandsRawData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "bands_raw_data_id")
    private Long id;

    @Column(name = "band_name", columnDefinition = "TEXT")
    private String bandName;

    @Column(name = "main_music", columnDefinition = "TEXT")
    private String mainMusic;

    @Column(name = "description", nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(name = "artist", nullable = false, columnDefinition = "TEXT")
    private String artist;

}
