package com.fagnerlima.samplespecification.api.infrastructure.persistence.specification;

import org.springframework.data.jpa.domain.Specification;

import com.github.fagnerlima.springspecificationtools.SpecFactory;

import com.fagnerlima.samplespecification.api.domain.model.task.Task;
import com.fagnerlima.samplespecification.api.domain.model.task.Task.Status;
import com.fagnerlima.samplespecification.api.domain.model.task.Task_;

public final class TaskSpecification {

    private TaskSpecification() {
    }

    public static Specification<Task> hasTags() {
        return (root, query, criteriaBuilder) -> criteriaBuilder.isNotEmpty(root.get(Task_.TAGS));
    }

    public static Specification<Task> withStatus(Status status) {
        return new SpecFactory<Task>().create(Task_.STATUS, status);
    }

}
