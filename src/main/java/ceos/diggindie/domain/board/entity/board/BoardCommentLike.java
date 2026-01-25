package ceos.diggindie.domain.board.entity.board;

import ceos.diggindie.common.entity.BaseEntity;
import ceos.diggindie.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "board_comment_like")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BoardCommentLike extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "board_comment_like_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_comment_id", nullable = false)
    private BoardComment boardComment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Builder
    public BoardCommentLike(BoardComment boardComment, Member member) {
        this.boardComment = boardComment;
        this.member = member;
    }
}
