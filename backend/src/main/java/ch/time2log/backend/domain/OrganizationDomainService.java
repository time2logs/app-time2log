package ch.time2log.backend.domain;

import ch.time2log.backend.domain.models.Organization;
import ch.time2log.backend.infrastructure.supabase.SupabaseService;
import ch.time2log.backend.infrastructure.supabase.responses.OrganizationResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrganizationDomainService {

    private final SupabaseService supabaseService;

    public OrganizationDomainService(SupabaseService supabaseService) {
        this.supabaseService = supabaseService;
    }

    public List<Organization> getOrganizations() {
        return Organization.ofList(supabaseService.getList("admin.organizations", OrganizationResponse.class));
    }
}
