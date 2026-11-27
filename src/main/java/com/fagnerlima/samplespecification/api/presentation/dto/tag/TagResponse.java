package com.fagnerlima.samplespecification.api.presentation.dto.tag;

import com.fagnerlima.samplespecification.api.domain.model.tag.Tag;

public record TagResponse(Long id, String description) {

    public static TagResponse from(Tag tag) {
        return new TagResponse(tag.getId(), tag.getDescription());
    }

}
