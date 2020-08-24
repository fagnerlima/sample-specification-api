package br.pro.fagnerlima.samplespecification.api.presentation.dto.shared;

import java.io.Serializable;
import java.time.LocalDate;

public class PeriodTO implements Serializable {

    private static final long serialVersionUID = -8077502889609997064L;

    private LocalDate startDate;

    private LocalDate endDate;

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

}
