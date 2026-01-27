package ceos.diggindie.domain.member.service;

import ceos.diggindie.common.code.BusinessErrorCode;
import ceos.diggindie.common.enums.PostType;
import ceos.diggindie.common.exception.BusinessException;
import ceos.diggindie.domain.board.repository.*;
import ceos.diggindie.domain.member.dto.mypage.*;
import ceos.diggindie.domain.member.entity.Member;
import ceos.diggindie.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MyPageService {

    private final MemberRepository memberRepository;
    private final BoardRepository boardRepository;
    private final MarketRepository marketRepository;
    private final BoardCommentRepository boardCommentRepository;
    private final BoardLikeRepository boardLikeRepository;
    private final MarketScrapRepository marketScrapRepository;

    // 내가 쓴 자유게시판 글 조회
    public Page<MyBoardPostResponse> getMyBoardPosts(String externalId, Pageable pageable) {
        Member member = findMember(externalId);
        return boardRepository.findByMemberOrderByCreatedAtDesc(member, pageable)
                .map(MyBoardPostResponse::from);
    }

    // 내가 쓴 마켓 글 조회
    public Page<MyMarketPostResponse> getMyMarketPosts(String externalId, Pageable pageable) {
        Member member = findMember(externalId);
        return marketRepository.findByMemberOrderByCreatedAtDesc(member, pageable)
                .map(MyMarketPostResponse::from);
    }

    // 내가 댓글 단 게시물 조회 (자유게시판)
    public Page<MyCommentedPostResponse> getMyCommentedPosts(String externalId, Pageable pageable) {
        Member member = findMember(externalId);
        return boardCommentRepository.findDistinctBoardsByMember(member, pageable)
                .map(MyCommentedPostResponse::from);
    }

    // 좋아요한 게시물 조회 (자유게시판)
    public Page<MyLikedPostResponse> getMyLikedPosts(String externalId, Pageable pageable) {
        Member member = findMember(externalId);
        return boardLikeRepository.findBoardsByMember(member, pageable)
                .map(MyLikedPostResponse::from);
    }

    // 스크랩한 양도글 조회 (거래게시판)
    public Page<MyScrappedPostResponse> getMyScrappedPosts(String externalId, Pageable pageable) {
        Member member = findMember(externalId);
        return marketScrapRepository.findMarketsByMember(member, pageable)
                .map(MyScrappedPostResponse::from);
    }

    private Member findMember(String externalId) {
        return memberRepository.findByExternalId(externalId)
                .orElseThrow(() -> new BusinessException(BusinessErrorCode.MEMBER_NOT_FOUND));
    }
}