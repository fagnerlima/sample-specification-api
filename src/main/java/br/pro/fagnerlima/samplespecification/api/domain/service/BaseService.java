package br.pro.fagnerlima.samplespecification.api.domain.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import br.pro.fagnerlima.samplespecification.api.domain.model.shared.BaseEntity;

public interface BaseService<T extends BaseEntity> {

    T findById(Long id);

    List<T> findAll();

    Page<T> findAll(Pageable pageable);

    Page<T> findAll(Specification<T> specification, Pageable pageable);

    T save(T entity);

    T update(Long id, T entity);

    void deleteById(Long id);

    void deleteAll(Iterable<T> entities);

}
