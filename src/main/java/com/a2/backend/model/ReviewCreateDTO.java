package com.a2.backend.model;

import com.sun.istack.NotNull;
import java.util.UUID;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewCreateDTO {

    @NotNull private UUID collaboratorID;

    @Size(min = 10, max = 500, message = "Comment should be between 10 and 500 characters")
    private String comment;

    @NotNull
    @Max(value = 5, message = "Max score is 5")
    @Min(value = 1, message = "Min score is 1")
    private int score;
}
