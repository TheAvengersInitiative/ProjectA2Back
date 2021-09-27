package com.a2.backend.model;

import java.util.UUID;
import javax.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConfirmationTokenDTO {
    UUID id;

    @Size(min = 32, max = 32)
    String confirmationToken;
}
