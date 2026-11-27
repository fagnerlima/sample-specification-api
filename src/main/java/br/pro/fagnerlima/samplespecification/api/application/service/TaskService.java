package br.pro.fagnerlima.samplespecification.api.application.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.pro.fagnerlima.samplespecification.api.domain.model.task.Task;
import br.pro.fagnerlima.samplespecification.api.domain.model.task.Task.Status;
import br.pro.fagnerlima.samplespecification.api.infrastructure.persistence.repository.TaskRepository;
import br.pro.fagnerlima.samplespecification.api.infrastructure.persistence.specification.TaskSpecification;
import br.pro.fagnerlima.samplespecification.api.presentation.dto.task.TaskFilter;

@Service
@Transactional(readOnly = true)
public class TaskService {

    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public Page<Task> findAll(Specification<Task> specification, Pageable pageable) {
        return taskRepository.findAll(specification, pageable);
    }

    public Page<Task> findAllWithJpaCriteria(TaskFilter filter, Pageable pageable) {
        return taskRepository.findAllWithJpaCriteria(filter, pageable);
    }

    public Page<Task> findAllHasTags(Pageable pageable) {
        return taskRepository.findAll(TaskSpecification.hasTags(), pageable);
    }

    public Page<Task> findAllByStatus(Status status, Pageable pageable) {
        return taskRepository.findAll(TaskSpecification.withStatus(status), pageable);
    }

}
