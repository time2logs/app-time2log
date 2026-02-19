package ch.time2log.backend.domain;

import ch.time2log.backend.domain.models.CurriculumNode;
import ch.time2log.backend.domain.models.CurriculumNodeFilter;
import ch.time2log.backend.domain.models.Profession;
import ch.time2log.backend.infrastructure.supabase.SupabaseService;
import ch.time2log.backend.infrastructure.supabase.responses.CurriculumNodeResponse;
import ch.time2log.backend.infrastructure.supabase.responses.ProfessionResponse;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class AppContextDomainService {

    private final SupabaseService supabaseService;

    public AppContextDomainService(SupabaseService supabaseService) {
        this.supabaseService = supabaseService;
    }

    public List<Profession> getProfessions(UUID organizationId) {
        var responses = supabaseService.getListWithQuery(
                "admin.professions",
                "organization_id=eq." + organizationId + "&order=label.asc",
                ProfessionResponse.class
        );
        return Profession.ofList(responses);
    }

    public List<CurriculumNode> getCurriculumNodes(UUID organizationId, CurriculumNodeFilter filter) {
        String query = "organization_id=eq." + organizationId
                + "&order=sort_order.asc,label.asc"
                + filter.toQuerySegment();

        var responses = supabaseService.getListWithQuery("app.curriculum_nodes", query, CurriculumNodeResponse.class);
        return CurriculumNode.ofList(responses);
    }
}
