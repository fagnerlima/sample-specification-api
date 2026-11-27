package com.fagnerlima.samplespecification.api.infrastructure.persistence.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.fagnerlima.samplespecification.api.domain.model.task.Task;
import com.fagnerlima.samplespecification.api.presentation.dto.task.TaskFilter;

public interface TaskRepositoryQuery {

    Page<Task> findAllWithJpaCriteria(TaskFilter filter, Pageable pageable);

}
