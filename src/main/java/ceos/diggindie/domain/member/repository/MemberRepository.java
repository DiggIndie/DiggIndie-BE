package ceos.diggindie.domain.member.repository;


import ceos.diggindie.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByUserId(String userId);

    Optional<Member> findByExternalId(String externalId);

    boolean existsByUserId(String userId);

    boolean existsByEmail(String email);

}
