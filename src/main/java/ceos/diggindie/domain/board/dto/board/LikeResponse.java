package ceos.diggindie.domain.board.dto.board;

public record LikeResponse(
        boolean isLiked,
        long likeCount
) {
    public static LikeResponse of(boolean isLiked, long likeCount) {
        return new LikeResponse(isLiked, likeCount);
    }
}