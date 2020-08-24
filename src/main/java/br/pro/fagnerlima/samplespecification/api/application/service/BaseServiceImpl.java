package br.pro.fagnerlima.samplespecification.api.application.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;

import br.pro.fagnerlima.samplespecification.api.domain.model.shared.BaseEntity;
import br.pro.fagnerlima.samplespecification.api.domain.service.BaseService;
import br.pro.fagnerlima.samplespecification.api.infrastructure.persistence.repository.BaseRepository;

public abstract class BaseServiceImpl<T extends BaseEntity> implements BaseService<T> {

    @Override
    @Transactional(readOnly = true)
    public T findById(Long id) {
        Optional<T> entityOpt = getRepository().findById(id);

        return entityOpt.orElseThrow(() -> new EmptyResultDataAccessException(1));
    }

    @Override
    @Transactional(readOnly = true)
    public List<T> findAll() {
        return getRepository().findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<T> findAll(Pageable pageable) {
        return getRepository().findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<T> findAll(Specification<T> specification, Pageable pageable) {
        return getRepository().findAll(specification, pageable);
    }

    @Override
    @Transactional
    public T save(T entity) {
        return getRepository().save(entity);
    }

    @Override
    @Transactional
    public T update(Long id, T entity) {
        T savedEntity = findById(id);
        BeanUtils.copyProperties(entity, savedEntity, "id");

        return getRepository().save(savedEntity);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        getRepository().deleteById(id);
    }

    @Override
    @Transactional
    public void deleteAll(Iterable<T> entities) {
        entities.forEach(e -> deleteById(e.getId()));
    }

    protected abstract BaseRepository<T> getRepository();

}
