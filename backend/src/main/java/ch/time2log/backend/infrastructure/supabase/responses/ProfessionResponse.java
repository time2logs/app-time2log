package ch.time2log.backend.infrastructure.supabase.responses;

import java.util.UUID;

public record ProfessionResponse(
        UUID id,
        UUID organization_id,
        String key,
        String label
) {}
