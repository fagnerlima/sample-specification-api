package br.pro.fagnerlima.samplespecification.api.dto;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;

import com.github.fagnerlima.springspecificationtools.SpecOperation;
import com.github.fagnerlima.springspecificationtools.SpecOperator;
import com.github.fagnerlima.springspecificationtools.annotation.SpecBetween;
import com.github.fagnerlima.springspecificationtools.annotation.SpecEntity;
import com.github.fagnerlima.springspecificationtools.annotation.SpecField;
import com.github.fagnerlima.springspecificationtools.annotation.SpecGroup;
import com.github.fagnerlima.springspecificationtools.annotation.SpecJoin;
import com.github.fagnerlima.springspecificationtools.annotation.SpecPeriod;

import br.pro.fagnerlima.samplespecification.api.model.Task;

@SpecEntity(Task.class)
public class TaskFilter implements Serializable {

    private static final long serialVersionUID = -6133110095451893018L;

    private Long id;

    @SpecBetween(left = "period.startDate", right = "period.endDate")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate date;

    @SpecPeriod(start = "period.startDate", end = "period.endDate")
    private PeriodFilter period;

    @SpecField(operation = SpecOperation.LIKE_IGNORE_CASE_UNACCENT)
    private String description;

    private Task.Status status;

    @SpecJoin
    @SpecField("tags.id")
    private List<Long> tagId;

    @SpecGroup(operator = SpecOperator.OR)
    private DescriptionOrTagFilter descriptionOrTag;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public PeriodFilter getPeriod() {
        return period;
    }

    public void setPeriod(PeriodFilter period) {
        this.period = period;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Task.Status getStatus() {
        return status;
    }

    public void setStatus(Task.Status status) {
        this.status = status;
    }

    public List<Long> getTagId() {
        return tagId;
    }

    public void setTagId(List<Long> tagId) {
        this.tagId = tagId;
    }

    public DescriptionOrTagFilter getDescriptionOrTag() {
        return descriptionOrTag;
    }

    public void setDescriptionOrTag(DescriptionOrTagFilter descriptionOrTag) {
        this.descriptionOrTag = descriptionOrTag;
    }

}
