package ch.time2log.backend.domain.models;

import java.time.LocalDate;
import java.util.UUID;

public record CreateActivityCommand(
        UUID organizationId,
        UUID professionId,
        UUID userId,
        UUID curriculumActivityId,
        UUID teamId,
        LocalDate entryDate,
        Integer hours,
        String notes,
        Integer rating
) {}
