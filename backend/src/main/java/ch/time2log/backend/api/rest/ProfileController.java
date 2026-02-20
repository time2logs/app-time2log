package ch.time2log.backend.api.rest;

import ch.time2log.backend.api.rest.dto.inbound.UpdateProfileRequest;
import ch.time2log.backend.api.rest.dto.outbound.ProfileDto;
import ch.time2log.backend.domain.exception.EntityNotFoundException;
import ch.time2log.backend.infrastructure.supabase.SupabaseAdminClient;
import ch.time2log.backend.infrastructure.supabase.SupabaseService;
import ch.time2log.backend.infrastructure.supabase.responses.ProfileResponse;
import ch.time2log.backend.security.AuthenticatedUser;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("api/profile")
public class ProfileController {

    private final SupabaseService supabase;
    private final SupabaseAdminClient supabaseAdmin;

    public ProfileController(SupabaseService supabase, SupabaseAdminClient supabaseAdmin) {
        this.supabase = supabase;
        this.supabaseAdmin = supabaseAdmin;
    }

    @GetMapping
    public ProfileDto getProfile(@AuthenticationPrincipal AuthenticatedUser user) {
        var profiles = supabase.getListWithQuery("app.profiles", "id=eq." + user.id(), ProfileResponse.class);
        if (profiles.isEmpty()) {
            throw new EntityNotFoundException("Profile not found");
        }
        return ProfileDto.of(profiles.getFirst());
    }

    @GetMapping("/{id}")
    public ProfileDto getProfileById(@PathVariable UUID id) {
        var profiles = supabase.getListWithQuery("app.profiles", "id=eq." + id, ProfileResponse.class);
        if (profiles.isEmpty()) {
            throw new EntityNotFoundException("Profile not found");
        }
        return ProfileDto.of(profiles.getFirst());
    }

    @PatchMapping
    public ProfileDto updateProfile(
            @AuthenticationPrincipal AuthenticatedUser user,
            @RequestBody UpdateProfileRequest request
    ) {
        Map<String, Object> body = new LinkedHashMap<>();
        if (request.firstName() != null) body.put("first_name", request.firstName());
        if (request.lastName() != null) body.put("last_name", request.lastName());

        var updated = supabase.patch("app.profiles", "id=eq." + user.id(), body, ProfileResponse[].class);
        if (updated == null || updated.length == 0) {
            throw new EntityNotFoundException("Profile not found");
        }
        return ProfileDto.of(updated[0]);
    }

    @DeleteMapping
    public void deleteProfile(@AuthenticationPrincipal AuthenticatedUser user) {
        supabaseAdmin.deleteUser(user.id()).block();
    }
}
