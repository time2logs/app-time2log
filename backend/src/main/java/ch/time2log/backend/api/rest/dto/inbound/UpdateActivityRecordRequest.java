package ch.time2log.backend.api.rest.dto.inbound;

import java.time.LocalDate;
import java.util.UUID;

public record UpdateActivityRecordRequest(
        UUID curriculumActivityId,
        UUID teamId,
        LocalDate entryDate,
        Integer hours,
        String notes,
        Integer rating
) {}
