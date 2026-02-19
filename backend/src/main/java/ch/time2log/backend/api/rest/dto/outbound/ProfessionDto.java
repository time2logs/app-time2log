package ch.time2log.backend.api.rest.dto.outbound;

import ch.time2log.backend.domain.models.Profession;

import java.util.List;
import java.util.UUID;

public record ProfessionDto(
        UUID id,
        UUID organizationId,
        String key,
        String label
) {
    public static ProfessionDto of(Profession profession) {
        return new ProfessionDto(profession.id(), profession.organizationId(), profession.key(), profession.label());
    }

    public static List<ProfessionDto> ofList(List<Profession> professions) {
        return professions.stream().map(ProfessionDto::of).toList();
    }
}
