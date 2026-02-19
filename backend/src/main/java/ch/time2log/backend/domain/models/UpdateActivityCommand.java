package ch.time2log.backend.domain.models;

import java.time.LocalDate;
import java.util.UUID;

public record UpdateActivityCommand(
        UUID curriculumActivityId,
        UUID teamId,
        LocalDate entryDate,
        Integer hours,
        String notes,
        Integer rating
) {}
