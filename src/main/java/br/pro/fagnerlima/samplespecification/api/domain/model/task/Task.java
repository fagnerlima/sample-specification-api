package br.pro.fagnerlima.samplespecification.api.domain.model.task;

import java.util.Set;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import br.pro.fagnerlima.samplespecification.api.domain.model.shared.BaseEntity;
import br.pro.fagnerlima.samplespecification.api.domain.model.shared.Period;
import br.pro.fagnerlima.samplespecification.api.domain.model.tag.Tag;

@Entity
@Table(name = "task")
public class Task extends BaseEntity {

    private static final long serialVersionUID = 8418975877892704197L;

    @Valid
    @Embedded
    private Period period;

    @NotNull
    private String description;

    @Enumerated(EnumType.STRING)
    private Status status;

    @ManyToMany
    @Fetch(FetchMode.JOIN)
    @JoinTable(name = "tag_task",
            joinColumns = @JoinColumn(name = "id_task"),
            inverseJoinColumns = @JoinColumn(name = "id_tag"))
    private Set<Tag> tags;

    public Period getPeriod() {
        return period;
    }

    public void setPeriod(Period period) {
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

    public Set<Tag> getTags() {
        return tags;
    }

    public void setTags(Set<Tag> tags) {
        this.tags = tags;
    }

    @Override
    public String toString() {
        return String.format("Task [id=%s, period=%s, description=%s, status=%s, tags=%s]", id, period, description, status, tags);
    }

    public static enum Status {

        PENDING("Pending"),
        PROGRESS("In progress"),
        CANCELED("Canceled"),
        DONE("Done");

        private String label;

        private Status(String label) {
            this.label = label;
        }

        public String getLabel() {
            return label;
        }
    }

}
