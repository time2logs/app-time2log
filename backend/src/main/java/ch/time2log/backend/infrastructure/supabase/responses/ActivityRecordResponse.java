package ch.time2log.backend.infrastructure.supabase.responses;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

public record ActivityRecordResponse(
        UUID id,
        UUID organization_id,
        UUID profession_id,
        UUID user_id,
        UUID team_id,
        UUID curriculum_activity_id,
        LocalDate entry_date,
        Integer hours,
        String notes,
        Integer rating,
        OffsetDateTime created_at,
        OffsetDateTime updated_at
) {}
