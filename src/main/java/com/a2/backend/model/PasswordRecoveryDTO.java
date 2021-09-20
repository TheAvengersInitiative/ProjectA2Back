package com.a2.backend.model;

import javax.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PasswordRecoveryDTO {

    @Size(min = 8, max = 32, message = "Password must be between 8 and 32 characters")
    String newPassword;

    @Size(min = 8, max = 32)
    String passwordRecoveryToken;
}
