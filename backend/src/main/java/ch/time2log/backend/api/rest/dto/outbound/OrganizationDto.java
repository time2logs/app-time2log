package ch.time2log.backend.api.rest.dto.outbound;

import ch.time2log.backend.domain.models.Organization;
import ch.time2log.backend.infrastructure.supabase.responses.OrganizationResponse;

import java.util.List;
import java.util.UUID;

public record OrganizationDto(
   UUID id,
   String name
) {
    public static OrganizationDto of(Organization organization) {
        return new OrganizationDto(organization.id(), organization.name());
    }

    public static List<OrganizationDto> ofList(List<Organization> organizations) {
        return organizations.stream().map(OrganizationDto::of).toList();
    }
}
