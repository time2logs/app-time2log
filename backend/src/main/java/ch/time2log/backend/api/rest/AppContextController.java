package ch.time2log.backend.api.rest;

import ch.time2log.backend.api.rest.dto.outbound.CurriculumNodeDto;
import ch.time2log.backend.api.rest.dto.outbound.ProfessionDto;
import ch.time2log.backend.domain.AppContextDomainService;
import ch.time2log.backend.domain.models.CurriculumNodeFilter;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api")
public class AppContextController {

    private final AppContextDomainService appContextDomainService;

    public AppContextController(AppContextDomainService appContextDomainService) {
        this.appContextDomainService = appContextDomainService;
    }

    @GetMapping("/context/professions")
    public List<ProfessionDto> getProfessions(@RequestParam UUID organizationId) {
        return ProfessionDto.ofList(appContextDomainService.getProfessions(organizationId));
    }

    @GetMapping("/curriculum/nodes")
    public List<CurriculumNodeDto> getCurriculumNodes(
            @RequestParam UUID organizationId,
            @RequestParam(required = false) UUID professionId,
            @RequestParam(required = false) UUID parentId,
            @RequestParam(required = false) String nodeType
    ) {
        CurriculumNodeFilter filter = new CurriculumNodeFilter(professionId, parentId, nodeType);
        return CurriculumNodeDto.ofList(appContextDomainService.getCurriculumNodes(organizationId, filter));
    }
}
