package com.a2.backend.utils.SearchUtils;

import com.a2.backend.entity.Project;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

public class ProjectSpecification implements Specification<Project> {

    private SearchCriteria criteria;

    public ProjectSpecification(SearchCriteria criteria) {
        this.criteria = criteria;
    }

    @Override
    public Predicate toPredicate(
            Root<Project> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
        return builder.like(root.<String>get(criteria.getKey()), "%" + criteria.getValue() + "%");
    }
}
