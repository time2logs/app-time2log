package ch.time2log.backend.domain.models;

import java.time.LocalDate;

public record ActivityRecordFilter(LocalDate from, LocalDate to) {
    public static ActivityRecordFilter of(LocalDate from, LocalDate to) {
        if (from != null && to != null && from.isAfter(to)) {
            throw new IllegalArgumentException("'from' must be <= 'to'");
        }
        return new ActivityRecordFilter(from, to);
    }

    public boolean hasAny() {
        return from != null || to != null;
    }
}
