package ceos.diggindie.domain.band.repository;

import ceos.diggindie.domain.band.entity.Band;
import ceos.diggindie.domain.band.entity.TopTrack;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TopTrackRepository extends JpaRepository<TopTrack, Long> {

    Optional<TopTrack> findByBandId(Long bandId);

    Optional<TopTrack> findByBand(Band band);

    boolean existsByBand(Band band);

    @Query("SELECT t FROM TopTrack t WHERE t.band.id IN :bandIds")
    List<TopTrack> findByBandIdIn(@Param("bandIds") List<Long> bandIds);
}