package br.pro.fagnerlima.samplespecification.api.controller;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.fagnerlima.springspecificationtools.SpecBuilder;

import br.pro.fagnerlima.samplespecification.api.dto.TaskFilterRequestTO;
import br.pro.fagnerlima.samplespecification.api.model.Task;
import br.pro.fagnerlima.samplespecification.api.service.TaskService;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    private TaskService taskService;

    private ModelMapper modelMapper;

    public TaskController(TaskService taskService, ModelMapper modelMapper) {
        this.taskService = taskService;
        this.modelMapper = modelMapper;
    }

    @GetMapping
    public ResponseEntity<Page<Task>> findAll(TaskFilterRequestTO filterRequestTO, Pageable pageable) {
        Specification<Task> specification = new SpecBuilder<Task>().add(filterRequestTO).build();
        Page<Task> taskPage = taskService.findAll(specification, pageable);

        return ResponseEntity.ok(taskPage);
    }

}
