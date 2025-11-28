package ceos.diggindie.domain.concert.entity;

import ceos.diggindie.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "concert_hall")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ConcertHall extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "concert_hall_id")
    private Long id;

    @Column(length = 30)
    private String name;

    @Column(length = 255)
    private String address;

    @Column(length = 255)
    private String description;

}