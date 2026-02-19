package ch.time2log.backend.api.rest.dto.inbound;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.UUID;

public record CreateActivityRecordRequest(
        @NotNull UUID organizationId,
        @NotNull UUID professionId,
        @NotNull UUID curriculumActivityId,
        @Nullable UUID teamId,
        @NotNull LocalDate entryDate,
        @NotNull Integer hours,
        @Nullable String notes,
        @Nullable Integer rating
) {}
