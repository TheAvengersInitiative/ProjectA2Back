package com.a2.backend.utils.SearchUtils;

import com.a2.backend.entity.Project;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.data.jpa.domain.Specification;

public class ProjectSpecificationBuilder {
    private final List<SearchCriteria> params;

    public ProjectSpecificationBuilder() {
        params = new ArrayList<SearchCriteria>();
    }

    public ProjectSpecificationBuilder with(String key, String operation, String value) {
        params.add(new SearchCriteria(key, operation, value));
        return this;
    }

    public Specification<Project> build() {
        if (params.size() == 0) {
            return null;
        }

        List<Specification> specs =
                params.stream().map(ProjectSpecification::new).collect(Collectors.toList());

        Specification result = specs.get(0);

        for (int i = 1; i < params.size(); i++) {
            result = Specification.where(result).or(specs.get(i));
        }
        return result;
    }
}
