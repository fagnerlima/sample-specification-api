package br.pro.fagnerlima.samplespecification.api.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import br.pro.fagnerlima.samplespecification.api.dto.TaskFilter;
import br.pro.fagnerlima.samplespecification.api.model.Task;
import br.pro.fagnerlima.samplespecification.api.model.Task.Status;
import br.pro.fagnerlima.samplespecification.api.repository.TaskRepository;
import br.pro.fagnerlima.samplespecification.api.specification.TaskSpecification;

@Service
public class TaskService extends BaseService<Task> {

    private TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        super();
        this.taskRepository = taskRepository;
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

    @Override
    protected TaskRepository getRepository() {
        return taskRepository;
    }

}
