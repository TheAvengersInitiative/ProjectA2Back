package com.a2.backend.model;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentUpdateDTO {

    @NotBlank(message = "Comment cannot be empty")
    @Size(max = 500, message = "Comment should have less than 500 characters")
    private String comment;
}
