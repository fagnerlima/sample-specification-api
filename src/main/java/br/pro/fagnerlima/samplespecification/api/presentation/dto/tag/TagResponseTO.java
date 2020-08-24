package br.pro.fagnerlima.samplespecification.api.presentation.dto.tag;

import java.io.Serializable;

public class TagResponseTO implements Serializable {

    private static final long serialVersionUID = -4917148367873330040L;

    private Long id;

    private String description;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
