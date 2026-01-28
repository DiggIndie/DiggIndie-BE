package ceos.diggindie.domain.board.service;

import ceos.diggindie.domain.board.dto.HotPostResponse;
import ceos.diggindie.domain.board.entity.board.Board;
import ceos.diggindie.domain.board.entity.market.Market;
import ceos.diggindie.domain.board.repository.BoardRepository;
import ceos.diggindie.domain.board.repository.MarketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class HotService {
    private final MarketRepository marketRepository;
    private final BoardRepository boardRepository;

    private static final int MAX_FETCH_SIZE = 100;

    public Page<HotPostResponse> getHotPosts(Pageable pageable) {
        int fetchSize = Math.min(pageable.getPageSize() * 2, MAX_FETCH_SIZE);
        Pageable fetchPageable = PageRequest.of(0, fetchSize, Sort.by(Sort.Direction.DESC, "views"));

        List<HotPostResponse> allPosts = fetchAllPosts(fetchPageable);

        List<HotPostResponse> sortedPosts = allPosts.stream()
                .sorted(Comparator.comparing(HotPostResponse::getViews).reversed())
                .limit(pageable.getOffset() + pageable.getPageSize())
                .collect(Collectors.toList());

        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), sortedPosts.size());

        List<HotPostResponse> pagedPosts = start < sortedPosts.size()
                ? sortedPosts.subList(start, end)
                : new ArrayList<>();

        return new PageImpl<>(pagedPosts, pageable, sortedPosts.size());
    }

    public List<HotPostResponse> getTop3Posts() {
        Pageable top10 = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "views"));

        List<HotPostResponse> allPosts = fetchAllPosts(top10);

        return allPosts.stream()
                .sorted(Comparator.comparing(HotPostResponse::getViews).reversed())
                .limit(3)
                .collect(Collectors.toList());
    }

    private List<HotPostResponse> fetchAllPosts(Pageable pageable) {
        List<HotPostResponse> allPosts = new ArrayList<>();

        Page<Market> marketPage = marketRepository.findAllByOrderByViewsDesc(pageable);
        marketPage.getContent().forEach(market ->
                allPosts.add(HotPostResponse.fromMarket(market))
        );

        Page<Board> boardPage = boardRepository.findAllByOrderByViewsDesc(pageable);
        boardPage.getContent().forEach(board ->
                allPosts.add(HotPostResponse.fromBoard(board))
        );

        return allPosts;
    }
}