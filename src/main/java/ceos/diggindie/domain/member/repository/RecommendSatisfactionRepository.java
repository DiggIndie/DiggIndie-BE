package ceos.diggindie.domain.member.repository;

import ceos.diggindie.domain.member.entity.RecommendSatisfaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecommendSatisfactionRepository extends JpaRepository<RecommendSatisfaction, Long> {

}

