package com.a2.backend.model;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import lombok.*;
import org.springframework.lang.Nullable;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateDTO {

    @NotNull
    @Size(min = 3, max = 24, message = "Nickname must be between 3 and 24 characters")
    @Pattern(regexp = "[a-zA-Z0-9]+")
    String nickname;

    @Nullable
    @Size(max = 500, message = "Biography must be shorter than 500 characters")
    String biography;

    @Nullable
    @Size(min = 8, max = 32, message = "Password must be between 8 and 32 characters")
    String password;
}
