package br.pro.fagnerlima.samplespecification.api.presentation.dto.task;

import java.util.Comparator;
import java.util.List;

import br.pro.fagnerlima.samplespecification.api.domain.model.task.Task;
import br.pro.fagnerlima.samplespecification.api.domain.model.task.Task.Status;
import br.pro.fagnerlima.samplespecification.api.presentation.dto.shared.PeriodResponse;
import br.pro.fagnerlima.samplespecification.api.presentation.dto.tag.TagResponse;

public record TaskResponse(Long id, PeriodResponse period, String description, Status status, List<TagResponse> tags) {

    public static TaskResponse from(Task task) {
        // Sorted by id so the JSON is stable: the entity keeps its tags in a Set.
        List<TagResponse> tags = task.getTags().stream()
                .map(TagResponse::from)
                .sorted(Comparator.comparing(TagResponse::id))
                .toList();

        return new TaskResponse(task.getId(), PeriodResponse.from(task.getPeriod()), task.getDescription(),
                task.getStatus(), tags);
    }

}
