package com.a2.backend.model;

import java.util.UUID;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PasswordRecoveryDTO {

    @NotNull UUID id;

    @Size(min = 8, max = 32, message = "Password must be between 8 and 32 characters")
    String newPassword;

    @Size(min = 32, max = 32)
    String passwordRecoveryToken;
}
