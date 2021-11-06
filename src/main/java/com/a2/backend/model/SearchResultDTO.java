package com.a2.backend.model;

import java.util.List;
import lombok.*;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchResultDTO {
    List<ProjectDTO> projects;
    int pageAmount;
}
