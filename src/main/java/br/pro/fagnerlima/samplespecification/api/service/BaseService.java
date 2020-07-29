package br.pro.fagnerlima.samplespecification.api.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;

import br.pro.fagnerlima.samplespecification.api.model.BaseEntity;
import br.pro.fagnerlima.samplespecification.api.repository.BaseRepository;

public abstract class BaseService<T extends BaseEntity> {

    @Transactional(readOnly = true)
    public T findById(Long id) {
        Optional<T> entityOpt = getRepository().findById(id);

        return entityOpt.orElseThrow(() -> new EmptyResultDataAccessException(1));
    }

    @Transactional(readOnly = true)
    public List<T> findAll() {
        return getRepository().findAll();
    }

    @Transactional(readOnly = true)
    public Page<T> findAll(Pageable pageable) {
        return getRepository().findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Page<T> findAll(Specification<T> specification, Pageable pageable) {
        return getRepository().findAll(specification, pageable);
    }

    @Transactional
    public T save(T entity) {
        return getRepository().save(entity);
    }

    @Transactional
    public T update(Long id, T entity) {
        T savedEntity = findById(id);
        BeanUtils.copyProperties(entity, savedEntity, "id");

        return getRepository().save(savedEntity);
    }

    @Transactional
    public void deleteById(Long id) {
        getRepository().deleteById(id);
    }

    @Transactional
    public void deleteAll(Iterable<T> entities) {
        entities.forEach(e -> deleteById(e.getId()));
    }

    protected abstract BaseRepository<T> getRepository();

}
