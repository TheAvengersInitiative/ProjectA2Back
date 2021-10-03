package com.a2.backend.model;

import java.util.List;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import lombok.*;
import org.hibernate.validator.constraints.UniqueElements;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DiscussionCreateDTO {
    @NotNull
    @Size(min = 3, max = 32, message = "Title must be between 3 and 32 characters")
    private String title;

    @NotNull
    @NotEmpty
    @Size(min = 1, max = 5, message = "Number of tags must be between 1 and 5")
    @UniqueElements
    private List<
                    @Pattern(regexp = "[a-zA-Z0-9]+")
                    @Size(
                            min = 1,
                            max = 24,
                            message = "Tag name must be between 1 and 24 characters")
                    String>
            forumTags;
}
