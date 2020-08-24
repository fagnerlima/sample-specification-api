package br.pro.fagnerlima.samplespecification.api.domain.model.tag;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import br.pro.fagnerlima.samplespecification.api.domain.model.shared.BaseEntity;

@Entity
@Table(name = "tag")
public class Tag extends BaseEntity {

    private static final long serialVersionUID = 5668457831354880698L;

    @NotNull
    private String description;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return String.format("Tag [description=%s]", description);
    }

}
