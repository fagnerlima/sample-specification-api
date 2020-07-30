package br.pro.fagnerlima.samplespecification.api.dto;

import java.io.Serializable;

import com.github.fagnerlima.springspecificationtools.SpecOperation;
import com.github.fagnerlima.springspecificationtools.annotation.SpecField;
import com.github.fagnerlima.springspecificationtools.annotation.SpecJoin;

public class DescriptionOrTagFilter implements Serializable {

    private static final long serialVersionUID = -3125120719715109975L;

    @SpecField(operation = SpecOperation.LIKE_IGNORE_CASE_UNACCENT)
    private String description;

    @SpecJoin
    @SpecField(value = "tags.description", operation = SpecOperation.LIKE_IGNORE_CASE_UNACCENT)
    private String tagDescription;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTagDescription() {
        return tagDescription;
    }

    public void setTagDescription(String tagDescription) {
        this.tagDescription = tagDescription;
    }

}
