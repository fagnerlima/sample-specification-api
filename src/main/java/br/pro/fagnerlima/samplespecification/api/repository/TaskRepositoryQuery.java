package br.pro.fagnerlima.samplespecification.api.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import br.pro.fagnerlima.samplespecification.api.dto.TaskFilter;
import br.pro.fagnerlima.samplespecification.api.model.Task;

public interface TaskRepositoryQuery {

    Page<Task> findAllWithJpaCriteria(TaskFilter filter, Pageable pageable);

}
