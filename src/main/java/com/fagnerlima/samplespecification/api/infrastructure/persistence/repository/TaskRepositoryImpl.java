package com.fagnerlima.samplespecification.api.infrastructure.persistence.repository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaBuilder.In;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;

import com.github.fagnerlima.springspecificationtools.util.StringUtils;

import com.fagnerlima.samplespecification.api.domain.model.shared.Period_;
import com.fagnerlima.samplespecification.api.domain.model.tag.Tag;
import com.fagnerlima.samplespecification.api.domain.model.tag.Tag_;
import com.fagnerlima.samplespecification.api.domain.model.task.Task;
import com.fagnerlima.samplespecification.api.domain.model.task.Task_;
import com.fagnerlima.samplespecification.api.presentation.dto.task.DescriptionOrTagFilter;
import com.fagnerlima.samplespecification.api.presentation.dto.task.TaskFilter;

public class TaskRepositoryImpl implements TaskRepositoryQuery {

    private final EntityManager entityManager;

    public TaskRepositoryImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public Page<Task> findAllWithJpaCriteria(TaskFilter filter, Pageable pageable) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Task> query = builder.createQuery(Task.class);
        Root<Task> root = query.from(Task.class);

        query.distinct(true);
        query.where(createRestrictions(filter, builder, root));

        if (pageable.getSort().isSorted()) {
            query.orderBy(createOrders(builder, root, pageable));
        }

        TypedQuery<Task> typedQuery = entityManager.createQuery(query);
        createRestrictionsPageable(typedQuery, pageable);

        return new PageImpl<Task>(typedQuery.getResultList(), pageable, total(filter));
    }

    private Predicate[] createRestrictions(TaskFilter filter, CriteriaBuilder builder, Root<Task> root) {
        List<Predicate> predicates = new ArrayList<>();

        if (filter.getId() != null) {
            predicates.add(builder.equal(root.get(Task_.ID), filter.getId()));
        }

        if (filter.getDate() != null) {
            predicates.add(builder.between(builder.literal(filter.getDate()),
                    root.get(Task_.PERIOD).get(Period_.START_DATE),
                    root.get(Task_.PERIOD).get(Period_.END_DATE)));
        }

        if (filter.getPeriod() != null && filter.getPeriod().getStartDate() != null && filter.getPeriod().getEndDate() != null) {
            LocalDate startDate = filter.getPeriod().getStartDate();
            LocalDate endDate = filter.getPeriod().getEndDate();
            predicates.add(builder.or(
                    builder.between(root.get(Task_.PERIOD).get(Period_.START_DATE), startDate, endDate),
                    builder.between(root.get(Task_.PERIOD).get(Period_.END_DATE), startDate, endDate)));
        }

        if (!StringUtils.isBlank(filter.getDescription())) {
            predicates.add(createLikeUnaccent(builder, root.get(Task_.DESCRIPTION), filter.getDescription()));
        }

        if (filter.getStatus() != null) {
            predicates.add(builder.equal(root.get(Task_.STATUS), filter.getStatus()));
        }

        if (filter.getTagId() != null) {
            Join<Task, Tag> tagJoin = root.join(Task_.TAGS);
            In<Long> tagPredicate = builder.in(tagJoin.get(Tag_.ID));
            filter.getTagId().stream().forEach(id -> tagPredicate.value(id));
            predicates.add(tagPredicate);
        }

        if (filter.getDescriptionOrTag() != null) {
            createDescriptionOrTagRestriction(filter.getDescriptionOrTag(), builder, root).ifPresent(predicates::add);
        }

        return predicates.toArray(Predicate[]::new);
    }

    private Optional<Predicate> createDescriptionOrTagRestriction(DescriptionOrTagFilter filter,
            CriteriaBuilder builder, Root<Task> root) {
        List<Predicate> alternatives = new ArrayList<>();

        if (!StringUtils.isBlank(filter.getDescription())) {
            alternatives.add(createLikeUnaccent(builder, root.get(Task_.DESCRIPTION), filter.getDescription()));
        }

        // Joins the tags only when filtering by their description, as SpecBuilder does for @SpecJoin fields.
        if (!StringUtils.isBlank(filter.getTagDescription())) {
            Join<Task, Tag> tagJoin = root.join(Task_.TAGS);
            alternatives.add(createLikeUnaccent(builder, tagJoin.get(Tag_.DESCRIPTION), filter.getTagDescription()));
        }

        if (alternatives.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(builder.or(alternatives.toArray(Predicate[]::new)));
    }

    private void createRestrictionsPageable(TypedQuery<Task> typedQuery, Pageable pageable) {
        int pageNumber = pageable.getPageNumber();
        int pageSize = pageable.getPageSize();
        int firstResult = pageNumber * pageSize;

        typedQuery.setMaxResults(pageSize);
        typedQuery.setFirstResult(firstResult);
    }

    private Order[] createOrders(CriteriaBuilder builder, Root<Task> root, Pageable pageable) {
        List<Order> orders = new ArrayList<>();

        for (Sort.Order order : pageable.getSort()) {
            String[] properties = order.getProperty().split("\\.");
            Path<Object> x = root.get(properties[0]);

            if (properties.length > 1) {
                Integer index = 1;

                do {
                    x = x.get(properties[index]);
                } while (++index < properties.length);
            }

            orders.add(order.getDirection().equals(Direction.ASC) ? builder.asc(x) : builder.desc(x));
        }

        return orders.toArray(Order[]::new);
    }

    private Long total(TaskFilter filter) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> criteria = builder.createQuery(Long.class);
        Root<Task> root = criteria.from(Task.class);

        Predicate[] predicates = createRestrictions(filter, builder, root);
        criteria.where(predicates);
        // Distinct, like the page query: joining many tags would otherwise count a task once per tag.
        criteria.select(builder.countDistinct(root));

        return entityManager.createQuery(criteria).getSingleResult();
    }

    private Predicate createLikeUnaccent(CriteriaBuilder builder, Expression<String> x, String y) {
        return builder.like(
                builder.function("unaccent", String.class, builder.lower(x)),
                prepareForLike(StringUtils.unaccent(y.toLowerCase())));
    }

    private String prepareForLike(String value) {
        return "%" + value.replaceAll("\\s+", "%") + "%";
    }

}
