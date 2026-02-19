package ch.time2log.backend.api.rest.dto.inbound;

import jakarta.annotation.Nullable;

public record UpdateProfileRequest(
        @Nullable String firstName,
        @Nullable String lastName
) {}
