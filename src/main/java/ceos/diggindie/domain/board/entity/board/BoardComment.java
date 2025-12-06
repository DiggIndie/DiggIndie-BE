package ceos.diggindie.domain.board.entity.board;

import ceos.diggindie.common.entity.BaseEntity;
import ceos.diggindie.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "board_comment")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BoardComment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "board_comment_id")
    private Long id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id", nullable = false)
    private Board board;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    // 부모 댓글 (null 이면 최상위 댓글, not null 이면 대댓글)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_comment_id")
    private BoardComment parentComment;

    // 대댓글 목록
    @OneToMany(mappedBy = "parentComment", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BoardComment> childComments = new ArrayList<>();

    @OneToMany(mappedBy = "boardComment", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BoardCommentLike> likes = new ArrayList<>();
}
