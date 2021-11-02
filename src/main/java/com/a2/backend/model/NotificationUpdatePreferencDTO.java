package com.a2.backend.model;

import javax.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationUpdatePreferencDTO {
    @NotNull boolean allowsNotifications;
}
