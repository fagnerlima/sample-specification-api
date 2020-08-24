package br.pro.fagnerlima.samplespecification.api.presentation.dto.task;

import java.io.Serializable;
import java.util.Set;

import br.pro.fagnerlima.samplespecification.api.domain.model.task.Task.Status;
import br.pro.fagnerlima.samplespecification.api.presentation.dto.shared.PeriodTO;
import br.pro.fagnerlima.samplespecification.api.presentation.dto.tag.TagResponseTO;

public class TaskResponseTO implements Serializable {

    private static final long serialVersionUID = -8312060302727604884L;

    private Long id;

    private PeriodTO period;

    private String description;

    private Status status;

    private Set<TagResponseTO> tags;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PeriodTO getPeriod() {
        return period;
    }

    public void setPeriod(PeriodTO period) {
        this.period = period;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Set<TagResponseTO> getTags() {
        return tags;
    }

    public void setTags(Set<TagResponseTO> tags) {
        this.tags = tags;
    }

}
