package ceos.diggindie.common.config.security;

import ceos.diggindie.domain.member.entity.Member;
import ceos.diggindie.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CustomUserDetailService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public CustomUserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {

        Member member = memberRepository.findByUserId(userId)
                .orElseThrow(() -> new UsernameNotFoundException("Member not found"));

        return new CustomUserDetails(member.getId(), member.getUserId(), member.getRole());
    }

    public CustomUserDetails loadByMemberId(Long memberId) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new UsernameNotFoundException("Member not found"));

        return new CustomUserDetails(member.getId(), member.getUserId(), member.getRole());
    }

    public CustomUserDetails loadByExternalId(String externalId) {

        Member member = memberRepository.findByExternalId(externalId)
                .orElseThrow(() -> new UsernameNotFoundException("Member not found"));

        return new CustomUserDetails(member.getId(), member.getUserId(), member.getRole());
    }
}
