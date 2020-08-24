package br.pro.fagnerlima.samplespecification.api.application.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.fagnerlima.springspecificationtools.SpecBuilder;

import br.pro.fagnerlima.samplespecification.api.application.service.TaskServiceImpl;
import br.pro.fagnerlima.samplespecification.api.domain.model.task.Task;
import br.pro.fagnerlima.samplespecification.api.domain.model.task.Task.Status;
import br.pro.fagnerlima.samplespecification.api.presentation.dto.task.TaskFilter;
import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    private TaskServiceImpl taskService;

    public TaskController(TaskServiceImpl taskService) {
        this.taskService = taskService;
    }

    @Operation(summary = "Find all, with pagination, by filters using Specification")
    @GetMapping
    public ResponseEntity<Page<Task>> findAll(TaskFilter filter, Pageable pageable) {
        Specification<Task> specification = new SpecBuilder<Task>().add(filter).build();
        Page<Task> taskPage = taskService.findAll(specification, pageable);

        return ResponseEntity.ok(taskPage);
    }

    @Operation(summary = "Find all, with pagination, by filters using JPA Criteria API")
    @GetMapping("/jpa-criteria")
    public ResponseEntity<Page<Task>> findAllWithJpaCriteria(TaskFilter filter, Pageable pageable) {
        Page<Task> taskPage = taskService.findAllWithJpaCriteria(filter, pageable);

        return ResponseEntity.ok(taskPage);
    }

    @Operation(summary = "Find all, with pagination, that has tags")
    @GetMapping("/has-tags")
    public ResponseEntity<Page<Task>> findAllHasTags(Pageable pageable) {
        Page<Task> taskPage = taskService.findAllHasTags(pageable);

        return ResponseEntity.ok(taskPage);
    }

    @Operation(summary = "Find all, with pagination, by status")
    @GetMapping("/status/{status}")
    public ResponseEntity<Page<Task>> findAllByStatus(@PathVariable Status status, Pageable pageable) {
        Page<Task> taskPage = taskService.findAllByStatus(status, pageable);

        return ResponseEntity.ok(taskPage);
    }

}
