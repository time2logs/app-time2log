package ch.time2log.backend.api.rest;

import ch.time2log.backend.api.rest.dto.outbound.OrganizationDto;
import ch.time2log.backend.domain.OrganizationDomainService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("api/organizations")
@RestController
public class OrganizationController {

    private final OrganizationDomainService organizationDomainService;

    public OrganizationController(OrganizationDomainService organizationDomainService) {
        this.organizationDomainService = organizationDomainService;
    }

    @GetMapping
    public List<OrganizationDto> getOrganizations() {
        return OrganizationDto.ofList(organizationDomainService.getOrganizations());
    }
}
