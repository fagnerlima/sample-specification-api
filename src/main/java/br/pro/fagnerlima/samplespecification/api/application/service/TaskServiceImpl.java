package br.pro.fagnerlima.samplespecification.api.application.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import br.pro.fagnerlima.samplespecification.api.domain.model.Task;
import br.pro.fagnerlima.samplespecification.api.domain.model.Task.Status;
import br.pro.fagnerlima.samplespecification.api.domain.service.TaskService;
import br.pro.fagnerlima.samplespecification.api.infrastructure.persistence.repository.TaskRepository;
import br.pro.fagnerlima.samplespecification.api.infrastructure.persistence.specification.TaskSpecification;
import br.pro.fagnerlima.samplespecification.api.presentation.dto.TaskFilter;

@Service
public class TaskServiceImpl extends BaseServiceImpl<Task> implements TaskService {

    private TaskRepository taskRepository;

    public TaskServiceImpl(TaskRepository taskRepository) {
        super();
        this.taskRepository = taskRepository;
    }

    @Override
    public Page<Task> findAllWithJpaCriteria(TaskFilter filter, Pageable pageable) {
        return taskRepository.findAllWithJpaCriteria(filter, pageable);
    }

    @Override
    public Page<Task> findAllHasTags(Pageable pageable) {
        return taskRepository.findAll(TaskSpecification.hasTags(), pageable);
    }

    @Override
    public Page<Task> findAllByStatus(Status status, Pageable pageable) {
        return taskRepository.findAll(TaskSpecification.withStatus(status), pageable);
    }

    @Override
    protected TaskRepository getRepository() {
        return taskRepository;
    }

}
