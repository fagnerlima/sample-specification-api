package br.pro.fagnerlima.samplespecification.api.infrastructure.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import br.pro.fagnerlima.samplespecification.api.domain.model.task.Task;

public interface TaskRepository extends JpaRepository<Task, Long>, JpaSpecificationExecutor<Task>, TaskRepositoryQuery {

}
