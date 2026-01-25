package ceos.diggindie.domain.member.dto.mypage;

import ceos.diggindie.common.utils.TimeUtils;
import ceos.diggindie.domain.board.entity.board.Board;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record MyCommentedPostResponse(
        Long boardId,
        String category,
        String title,
        String content,
        int views,
        int likeCount,
        int imageCount,
        String createdAt
) {
    public static MyCommentedPostResponse from(Board board) {
        return MyCommentedPostResponse.builder()
                .boardId(board.getId())
                .category(board.getCategory() != null ? board.getCategory().name() : null)
                .title(board.getTitle())
                .content(board.getContent())
                .views(board.getViews())
                .likeCount(board.getBoardLikes().size())
                .imageCount(board.getBoardImages() != null ? board.getBoardImages().size() : 0)
                .createdAt(TimeUtils.toRelativeTime(board.getCreatedAt()))
                .build();
    }
}