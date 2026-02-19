package ch.time2log.backend.domain.models;

import ch.time2log.backend.infrastructure.supabase.responses.ProfessionResponse;

import java.util.List;
import java.util.UUID;

public record Profession(
        UUID id,
        UUID organizationId,
        String key,
        String label
) {
    public static Profession of(ProfessionResponse response) {
        return new Profession(
                response.id(),
                response.organization_id(),
                response.key(),
                response.label()
        );
    }

    public static List<Profession> ofList(List<ProfessionResponse> responses) {
        return responses.stream().map(Profession::of).toList();
    }
}
