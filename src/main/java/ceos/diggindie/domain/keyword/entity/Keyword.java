package ceos.diggindie.domain.keyword.entity;

import ceos.diggindie.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "keyword")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Keyword extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "keyword_id")
    private Long id;

    @Column(length = 20)
    private String keyword;

    @OneToMany(mappedBy = "keyword")
    private List<BandKeyword> bandKeywords = new ArrayList<>();

    @OneToMany(mappedBy = "keyword")
    private List<MemberKeyword> memberKeywords = new ArrayList<>();

}