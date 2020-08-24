package br.pro.fagnerlima.samplespecification.api.domain.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import br.pro.fagnerlima.samplespecification.api.domain.model.task.Task;
import br.pro.fagnerlima.samplespecification.api.domain.model.task.Task.Status;
import br.pro.fagnerlima.samplespecification.api.presentation.dto.task.TaskFilter;

public interface TaskService extends BaseService<Task> {

    public Page<Task> findAllWithJpaCriteria(TaskFilter filter, Pageable pageable);

    public Page<Task> findAllHasTags(Pageable pageable);

    public Page<Task> findAllByStatus(Status status, Pageable pageable);

}
