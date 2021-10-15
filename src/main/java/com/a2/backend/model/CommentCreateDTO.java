package com.a2.backend.model;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentCreateDTO {

    @NotBlank(message = "Comment cannot be empty")
    @Size(max = 500, message = "Comment should be between 10 and 500 characters")
    private String comment;
}
