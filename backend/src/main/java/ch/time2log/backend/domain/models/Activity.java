package ch.time2log.backend.domain.models;

import ch.time2log.backend.infrastructure.supabase.responses.ActivityRecordResponse;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record Activity(
        UUID id,
        UUID organizationId,
        UUID professionId,
        UUID userId,
        UUID teamId,
        UUID curriculumActivityId,
        LocalDate entryDate,
        Integer hours,
        String notes,
        Integer rating,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
    public static Activity of(ActivityRecordResponse response) {
        return new Activity(
                response.id(),
                response.organization_id(),
                response.profession_id(),
                response.user_id(),
                response.team_id(),
                response.curriculum_activity_id(),
                response.entry_date(),
                response.hours(),
                response.notes(),
                response.rating(),
                response.created_at(),
                response.updated_at()
        );
    }

    public static List<Activity> ofList(List<ActivityRecordResponse> activityRecordResponses) {
        return activityRecordResponses.stream().map(Activity::of).toList();
    }
}
