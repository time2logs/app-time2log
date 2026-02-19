package ch.time2log.backend.infrastructure.supabase.responses;

import java.util.UUID;

public record TeamResponse(
        UUID id,
        UUID organization_id,
        UUID profession_id,
        String name
) {}
