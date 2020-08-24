package br.pro.fagnerlima.samplespecification.api.infrastructure.persistence.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import br.pro.fagnerlima.samplespecification.api.domain.model.task.Task;
import br.pro.fagnerlima.samplespecification.api.presentation.dto.task.TaskFilter;

public interface TaskRepositoryQuery {

    Page<Task> findAllWithJpaCriteria(TaskFilter filter, Pageable pageable);

}
