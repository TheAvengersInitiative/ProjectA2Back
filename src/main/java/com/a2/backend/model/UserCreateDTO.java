package com.a2.backend.model;

import lombok.*;
import org.springframework.lang.Nullable;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserCreateDTO {

    @NotNull
    @Size(min = 3, max = 24, message = "Nickname must be between 3 and 24 characters")
    @Pattern(regexp = "[a-zA-Z0-9]+")
    String nickname;

    @NotNull
    @Email(message = "Email must be valid")
    String email;

    @Nullable
    @Size(max = 500, message = "Biography must be shorter than 500 characters")
    String biography;

    @NotNull
    @Size(min = 8, max = 32, message = "Password must be between 8 and 32 characters")
    String password;

    @Size(min = 8, max = 32)
    String confirmationToken;
}
