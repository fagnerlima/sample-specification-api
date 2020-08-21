package br.pro.fagnerlima.samplespecification.api.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.fagnerlima.springspecificationtools.SpecBuilder;

import br.pro.fagnerlima.samplespecification.api.dto.TaskFilter;
import br.pro.fagnerlima.samplespecification.api.model.Task;
import br.pro.fagnerlima.samplespecification.api.service.TaskService;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    private TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping
    public ResponseEntity<Page<Task>> findAll(TaskFilter filter, Pageable pageable) {
        Specification<Task> specification = new SpecBuilder<Task>().add(filter).build();
        Page<Task> taskPage = taskService.findAll(specification, pageable);

        return ResponseEntity.ok(taskPage);
    }

    @GetMapping(params = "jpa_criteria")
    public ResponseEntity<Page<Task>> findAllWithJpaCriteria(TaskFilter filter, Pageable pageable) {
        Page<Task> taskPage = taskService.findAllWithJpaCriteria(filter, pageable);

        return ResponseEntity.ok(taskPage);
    }

}
