package com.a2.backend.model;

import javax.validation.constraints.Email;
import javax.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConfirmationTokenDTO {

    @Email(message = "Email must be valid")
    String email;

    @Size(min = 32, max = 32)
    String confirmationToken;
}
