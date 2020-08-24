package br.pro.fagnerlima.samplespecification.api.domain.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import br.pro.fagnerlima.samplespecification.api.domain.model.Task;
import br.pro.fagnerlima.samplespecification.api.domain.model.Task.Status;
import br.pro.fagnerlima.samplespecification.api.presentation.dto.TaskFilter;

public interface TaskService extends BaseService<Task> {

    public Page<Task> findAllWithJpaCriteria(TaskFilter filter, Pageable pageable);

    public Page<Task> findAllHasTags(Pageable pageable);

    public Page<Task> findAllByStatus(Status status, Pageable pageable);

}
