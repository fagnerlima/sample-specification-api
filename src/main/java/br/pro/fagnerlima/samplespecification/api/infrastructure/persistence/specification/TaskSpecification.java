package br.pro.fagnerlima.samplespecification.api.infrastructure.persistence.specification;

import org.springframework.data.jpa.domain.Specification;

import com.github.fagnerlima.springspecificationtools.SpecFactory;

import br.pro.fagnerlima.samplespecification.api.domain.model.task.Task;
import br.pro.fagnerlima.samplespecification.api.domain.model.task.Task.Status;
import br.pro.fagnerlima.samplespecification.api.domain.model.task.Task_;

public class TaskSpecification {

    public static Specification<Task> hasTags() {
        return Specification.where((root, query, criteriaBuilder) -> criteriaBuilder.isNotEmpty(root.get(Task_.TAGS)));
    }

    public static Specification<Task> withStatus(Status status) {
        return new SpecFactory<Task>().create(Task_.STATUS, status);
    }

}
