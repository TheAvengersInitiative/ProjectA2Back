package com.a2.backend.entity;

import com.a2.backend.model.ReviewDTO;
import java.time.LocalDateTime;
import java.util.UUID;
import javax.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne(cascade = {CascadeType.MERGE})
    private User collaborator;

    private String comment;

    private int score;

    private LocalDateTime date;

    public ReviewDTO toDTO() {
        return ReviewDTO.builder()
                .collaborator(collaborator.toDTO())
                .comment(comment)
                .score(score)
                .date(date)
                .id(id)
                .build();
    }
}
