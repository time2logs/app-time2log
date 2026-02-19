package ch.time2log.backend.api.rest.dto.outbound;

import ch.time2log.backend.domain.models.Activity;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record ActivityRecordDto(
        UUID id,
        UUID organizationId,
        UUID professionId,
        UUID userId,
        UUID teamId,
        UUID curriculumActivityId,
        LocalDate entryDate,
        Integer hours,
        String notes,
        Integer rating
) {
    public static ActivityRecordDto of(Activity activity) {
        return new ActivityRecordDto(
                activity.id(),
                activity.organizationId(),
                activity.professionId(),
                activity.userId(),
                activity.teamId(),
                activity.curriculumActivityId(),
                activity.entryDate(),
                activity.hours(),
                activity.notes(),
                activity.rating()
        );
    }

    public static List<ActivityRecordDto> ofList(List<Activity> activities) {
        return activities.stream().map(ActivityRecordDto::of).toList();
    }
}
