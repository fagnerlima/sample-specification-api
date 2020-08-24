package br.pro.fagnerlima.samplespecification.api.infrastructure.persistence.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import br.pro.fagnerlima.samplespecification.api.domain.model.Task;
import br.pro.fagnerlima.samplespecification.api.presentation.dto.TaskFilter;

public interface TaskRepositoryQuery {

    Page<Task> findAllWithJpaCriteria(TaskFilter filter, Pageable pageable);

}
