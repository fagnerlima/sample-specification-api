package br.pro.fagnerlima.samplespecification.api.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import br.pro.fagnerlima.samplespecification.api.dto.TaskFilter;
import br.pro.fagnerlima.samplespecification.api.model.Task;
import br.pro.fagnerlima.samplespecification.api.repository.TaskRepository;

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

    @Override
    protected TaskRepository getRepository() {
        return taskRepository;
    }

}
