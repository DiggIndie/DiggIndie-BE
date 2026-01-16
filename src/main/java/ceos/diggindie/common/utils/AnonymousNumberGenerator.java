package ceos.diggindie.common.utils;

import ceos.diggindie.domain.board.entity.board.Board;
import ceos.diggindie.domain.board.entity.board.BoardComment;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AnonymousNumberGenerator {

    private final Long authorMemberId;
    private final Map<Long, Integer> memberAnonymousMap = new HashMap<>();
    private int nextNumber = 1;

    public AnonymousNumberGenerator(Board board, List<BoardComment> comments) {
        this.authorMemberId = board.getMember().getId();

        assignNumbers(comments);
    }

    private void assignNumbers(List<BoardComment> comments) {
        for (BoardComment comment : comments) {
            if (comment.getIsAnonymous()) {
                Long memberId = comment.getMember().getId();

                if (!memberId.equals(authorMemberId) && !memberAnonymousMap.containsKey(memberId)) {
                    memberAnonymousMap.put(memberId, nextNumber++);
                }
            }
            assignNumbers(comment.getChildComments());
        }
    }

    public String getNickname(Long memberId, Boolean isAnonymous, String realNickname) {
        if (!isAnonymous) {
            return realNickname;
        }

        if (memberId.equals(authorMemberId)) {
            return "글쓴이";
        }

        Integer number = memberAnonymousMap.get(memberId);
        return "익명" + number;
    }
}