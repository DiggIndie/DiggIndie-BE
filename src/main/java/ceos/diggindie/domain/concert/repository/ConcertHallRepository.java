package ceos.diggindie.domain.concert.repository;

import ceos.diggindie.domain.concert.entity.ConcertHall;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ConcertHallRepository extends JpaRepository<ConcertHall, Long> {
    Optional<ConcertHall> findByName(String name);
}
