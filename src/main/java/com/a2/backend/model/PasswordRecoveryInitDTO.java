package com.a2.backend.model;

import javax.validation.constraints.Email;
import lombok.*;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PasswordRecoveryInitDTO {

    @Email(message = "Email must be valid")
    String email;
}
