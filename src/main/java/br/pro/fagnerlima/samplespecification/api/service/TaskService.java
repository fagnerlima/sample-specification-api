package br.pro.fagnerlima.samplespecification.api.service;

import org.springframework.stereotype.Service;

import br.pro.fagnerlima.samplespecification.api.model.Task;
import br.pro.fagnerlima.samplespecification.api.repository.TaskRepository;

@Service
public class TaskService extends BaseService<Task> {

    private TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        super();
        this.taskRepository = taskRepository;
    }

    @Override
    protected TaskRepository getRepository() {
        return taskRepository;
    }

}
