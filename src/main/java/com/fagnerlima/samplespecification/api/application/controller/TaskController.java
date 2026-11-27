package com.fagnerlima.samplespecification.api.application.controller;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.fagnerlima.springspecificationtools.SpecBuilder;

import com.fagnerlima.samplespecification.api.application.service.TaskService;
import com.fagnerlima.samplespecification.api.domain.model.task.Task;
import com.fagnerlima.samplespecification.api.domain.model.task.Task.Status;
import com.fagnerlima.samplespecification.api.presentation.dto.task.TaskFilter;
import com.fagnerlima.samplespecification.api.presentation.dto.task.TaskResponse;
import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @Operation(summary = "Find all, with pagination, by filters using Specification")
    @GetMapping
    public Page<TaskResponse> findAll(@ParameterObject TaskFilter filter, @ParameterObject Pageable pageable) {
        Specification<Task> specification = new SpecBuilder<Task>().add(filter).build();

        return taskService.findAll(specification, pageable).map(TaskResponse::from);
    }

    @Operation(summary = "Find all, with pagination, by filters using JPA Criteria API")
    @GetMapping("/jpa-criteria")
    public Page<TaskResponse> findAllWithJpaCriteria(@ParameterObject TaskFilter filter,
            @ParameterObject Pageable pageable) {
        return taskService.findAllWithJpaCriteria(filter, pageable).map(TaskResponse::from);
    }

    @Operation(summary = "Find all, with pagination, that has tags")
    @GetMapping("/has-tags")
    public Page<TaskResponse> findAllHasTags(@ParameterObject Pageable pageable) {
        return taskService.findAllHasTags(pageable).map(TaskResponse::from);
    }

    @Operation(summary = "Find all, with pagination, by status")
    @GetMapping("/status/{status}")
    public Page<TaskResponse> findAllByStatus(@PathVariable Status status, @ParameterObject Pageable pageable) {
        return taskService.findAllByStatus(status, pageable).map(TaskResponse::from);
    }

}
