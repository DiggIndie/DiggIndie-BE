package ceos.diggindie.domain.member.repository;

import ceos.diggindie.common.enums.LoginPlatform;
import ceos.diggindie.domain.member.entity.SocialAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SocialAccountRepository extends JpaRepository<SocialAccount, Long> {

    @Query("SELECT sa FROM SocialAccount sa " +
            "JOIN FETCH sa.member " +
            "WHERE sa.platform = :platform AND sa.platformId = :platformId")
    Optional<SocialAccount> findByPlatformAndPlatformIdWithMember(
            @Param("platform") LoginPlatform platform,
            @Param("platformId") String platformId);

    Optional<SocialAccount> findByPlatformAndPlatformId(
            LoginPlatform platform, String platformId);

    List<SocialAccount> findAllByMemberId(Long memberId);

    boolean existsByMemberIdAndPlatform(Long memberId, LoginPlatform platform);

    void deleteByMemberIdAndPlatform(Long memberId, LoginPlatform platform);

    long countByMemberId(Long memberId);
}