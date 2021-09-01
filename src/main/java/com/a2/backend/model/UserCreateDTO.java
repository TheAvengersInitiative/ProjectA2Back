package com.a2.backend.model;

import lombok.*;
import org.springframework.lang.Nullable;

import javax.validation.constraints.Email;
import javax.validation.constraints.Size;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserCreateDTO {

    @Size(min = 3, max = 24, message = "Nickname must be between 3 and 24 characters")
    String nickname;

    @Email(message = "Email must be valid")
    String email;

    @Nullable
    @Size(max = 500, message = "Biography must be shorter than 500 characters")
    String biography;

    @Size(min = 8, max = 32, message = "Password must be between 8 and 32 characters")
    String password;
}
