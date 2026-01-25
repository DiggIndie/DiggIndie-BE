package ceos.diggindie.domain.member.service;

import ceos.diggindie.common.code.BusinessErrorCode;
import ceos.diggindie.common.enums.PostType;
import ceos.diggindie.common.exception.BusinessException;
import ceos.diggindie.domain.board.repository.*;
import ceos.diggindie.domain.member.dto.mypage.*;
import ceos.diggindie.domain.member.entity.Member;
import ceos.diggindie.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
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
    public List<MyBoardPostResponse> getMyBoardPosts(String externalId, Pageable pageable) {
        Member member = findMember(externalId);
        return boardRepository.findByMemberOrderByCreatedAtDesc(member, pageable)
                .map(MyBoardPostResponse::from)
                .getContent();
    }

    // 내가 쓴 마켓 글 조회
    public List<MyMarketPostResponse> getMyMarketPosts(String externalId, Pageable pageable) {
        Member member = findMember(externalId);
        return marketRepository.findByMemberOrderByCreatedAtDesc(member, pageable)
                .map(MyMarketPostResponse::from)
                .getContent();
    }

    // 내가 댓글 단 게시물 조회 (자유게시판)
    public List<MyCommentedPostResponse> getMyCommentedPosts(String externalId, Pageable pageable) {
        Member member = findMember(externalId);
        return boardCommentRepository.findDistinctBoardsByMember(member, pageable)
                .map(MyCommentedPostResponse::from)
                .getContent();
    }

    // 좋아요한 게시물 조회 (자유게시판)
    public List<MyLikedPostResponse> getMyLikedPosts(String externalId, Pageable pageable) {
        Member member = findMember(externalId);
        return boardLikeRepository.findBoardsByMember(member, pageable)
                .map(MyLikedPostResponse::from)
                .getContent();
    }

    // 스크랩한 양도글 조회 (거래게시판)
    public List<MyScrappedPostResponse> getMyScrappedPosts(String externalId, Pageable pageable) {
        Member member = findMember(externalId);
        return marketScrapRepository.findMarketsByMember(member, pageable)
                .map(MyScrappedPostResponse::from)
                .getContent();
    }

    private Member findMember(String externalId) {
        return memberRepository.findByExternalId(externalId)
                .orElseThrow(() -> new BusinessException(BusinessErrorCode.MEMBER_NOT_FOUND));
    }
}