package br.pro.fagnerlima.samplespecification.api.repository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaBuilder.In;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;

import com.github.fagnerlima.springspecificationtools.util.StringUtils;

import br.pro.fagnerlima.samplespecification.api.dto.TaskFilter;
import br.pro.fagnerlima.samplespecification.api.model.Period_;
import br.pro.fagnerlima.samplespecification.api.model.Tag;
import br.pro.fagnerlima.samplespecification.api.model.Tag_;
import br.pro.fagnerlima.samplespecification.api.model.Task;
import br.pro.fagnerlima.samplespecification.api.model.Task_;

public class TaskRepositoryImpl implements TaskRepositoryQuery {

    private EntityManager entityManager;

    public TaskRepositoryImpl(EntityManager entityManager) {
        super();
        this.entityManager = entityManager;
    }

    @Override
    public Page<Task> findAllWithJpaCriteria(TaskFilter filter, Pageable pageable) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Task> query = builder.createQuery(Task.class);
        Root<Task> root = query.from(Task.class);

        query.distinct(true);
        query.where(createRestrictions(filter, builder, root));

        if (pageable.getSort() != null) {
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
            Join<Task, Tag> tagJoin = root.join(Task_.TAGS);
            predicates.add(builder.or(
                    createLikeUnaccent(builder, root.get(Task_.DESCRIPTION), filter.getDescriptionOrTag().getDescription()),
                    createLikeUnaccent(builder, tagJoin.get(Tag_.DESCRIPTION), filter.getDescriptionOrTag().getTagDescription())));
        }

        return predicates.toArray(Predicate[]::new);
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
        criteria.select(builder.count(root));

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
