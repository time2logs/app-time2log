package ch.time2log.backend.infrastructure.supabase.responses;

import java.util.UUID;

public record CurriculumNodeResponse(
        UUID id,
        UUID parent_id,
        String node_type,
        String key,
        String label,
        String description,
        Integer sort_order,
        Boolean is_active
) {}
