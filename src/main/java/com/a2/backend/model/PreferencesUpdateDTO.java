package com.a2.backend.model;

import java.util.List;
import javax.validation.constraints.Size;
import lombok.*;
import org.hibernate.validator.constraints.UniqueElements;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PreferencesUpdateDTO {

    @UniqueElements(message = "Tag names must be unique")
    @Size(max = 4, message = "Maximum number of tags is 4")
    List<String> tags;

    @UniqueElements(message = "Languages must be unique")
    @Size(max = 3, message = "Maximum number of languages is 3")
    List<String> languages;
}
