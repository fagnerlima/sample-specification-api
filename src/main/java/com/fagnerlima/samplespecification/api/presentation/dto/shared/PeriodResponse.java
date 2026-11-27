package com.fagnerlima.samplespecification.api.presentation.dto.shared;

import java.time.LocalDate;

import com.fagnerlima.samplespecification.api.domain.model.shared.Period;

public record PeriodResponse(LocalDate startDate, LocalDate endDate) {

    public static PeriodResponse from(Period period) {
        return new PeriodResponse(period.getStartDate(), period.getEndDate());
    }

}
