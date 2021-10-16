package com.a2.backend.entity;

import com.a2.backend.model.CommentDTO;
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
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne(cascade = {CascadeType.MERGE})
    private User user;

    @Column(length = 500)
    private String comment;

    private LocalDateTime date;

    private boolean hidden;

    private boolean highlighted;

    public CommentDTO toDTO() {
        return CommentDTO.builder()
                .id(id)
                .user(user.toDTO())
                .comment(comment)
                .date(date)
                .hidden(hidden)
                .highlighted(highlighted)
                .build();
    }
}
