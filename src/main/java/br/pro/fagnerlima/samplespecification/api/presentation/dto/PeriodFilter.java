package br.pro.fagnerlima.samplespecification.api.presentation.dto;

import java.io.Serializable;
import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import com.github.fagnerlima.springspecificationtools.annotation.SpecPeriodEndDate;
import com.github.fagnerlima.springspecificationtools.annotation.SpecPeriodStartDate;

public class PeriodFilter implements Serializable {

    private static final long serialVersionUID = -8074151011143755949L;

    @SpecPeriodStartDate
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate startDate;

    @SpecPeriodEndDate
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
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
