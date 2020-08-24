package br.pro.fagnerlima.samplespecification.api.infrastructure.persistence.repository;

import br.pro.fagnerlima.samplespecification.api.domain.model.Task;

public interface TaskRepository extends BaseRepository<Task>, TaskRepositoryQuery {

}
