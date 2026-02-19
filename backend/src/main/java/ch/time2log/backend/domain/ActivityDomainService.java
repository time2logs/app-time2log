package ch.time2log.backend.domain;

import ch.time2log.backend.domain.exception.EntityNotCreatedException;
import ch.time2log.backend.domain.exception.NoRowsAffectedException;
import ch.time2log.backend.domain.models.Activity;
import ch.time2log.backend.domain.models.ActivityRecordFilter;
import ch.time2log.backend.domain.models.CreateActivityCommand;
import ch.time2log.backend.domain.models.UpdateActivityCommand;
import ch.time2log.backend.infrastructure.supabase.SupabaseService;
import ch.time2log.backend.infrastructure.supabase.responses.ActivityRecordResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class ActivityDomainService {

    private final SupabaseService supabaseService;

    public ActivityDomainService(SupabaseService supabaseService) {
        this.supabaseService = supabaseService;
    }

    public List<Activity> getActivities(UUID organizationId, ActivityRecordFilter filter) {
        if (filter == null || !filter.hasAny()) {
            return getActivities(organizationId);
        }
        return getActivitiesWithFilters(organizationId, filter.from(), filter.to());
    }

    public Activity createActivity(CreateActivityCommand command) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("organization_id", command.organizationId());
        body.put("profession_id", command.professionId());
        body.put("user_id", command.userId());
        body.put("curriculum_activity_id", command.curriculumActivityId());
        body.put("entry_date", command.entryDate());
        body.put("hours", command.hours());
        body.put("team_id", command.teamId());
        body.put("notes", command.notes() != null ? command.notes().trim() : null);
        body.put("rating", command.rating());

        var created = supabaseService.post("app.activity_records", body, ActivityRecordResponse[].class);
        if (created == null || created.length == 0) {
            throw new EntityNotCreatedException("Supabase returned no created activity record");
        }
        return Activity.of(created[0]);
    }

    public Activity updateActivity(UUID id, UpdateActivityCommand command) {
        Map<String, Object> body = new LinkedHashMap<>();
        if (command.curriculumActivityId() != null) body.put("curriculum_activity_id", command.curriculumActivityId());
        if (command.teamId() != null) body.put("team_id", command.teamId());
        if (command.entryDate() != null) body.put("entry_date", command.entryDate());
        if (command.hours() != null) body.put("hours", command.hours());
        if (command.notes() != null) body.put("notes", command.notes().trim());
        if (command.rating() != null) body.put("rating", command.rating());

        var updated = supabaseService.patch("app.activity_records", "id=eq." + id, body, ActivityRecordResponse[].class);
        if (updated == null || updated.length == 0) {
            throw new NoRowsAffectedException(
                    HttpStatus.NOT_FOUND,
                    "ACTIVITY_RECORD_NOT_FOUND",
                    "Activity record not found",
                    "Activity record could not be updated."
            );
        }
        return Activity.of(updated[0]);
    }

    public void deleteActivity(UUID id) {
        int deleted = supabaseService.deleteReturningCount("app.activity_records", "id=eq." + id);
        if (deleted == 0) {
            throw new NoRowsAffectedException(
                    HttpStatus.NOT_FOUND,
                    "ACTIVITY_RECORD_NOT_FOUND",
                    "Activity record not found",
                    "Activity record could not be deleted."
            );
        }
    }

    private List<Activity> getActivities(UUID organizationId) {
        String query = "organization_id=eq." + organizationId +
                "&order=entry_date.desc,created_at.desc";
        var entries = supabaseService.getListWithQuery("app.activity_records", query, ActivityRecordResponse.class);
        return Activity.ofList(entries);
    }

    private List<Activity> getActivitiesWithFilters(UUID organizationId, LocalDate from, LocalDate to) {
        StringBuilder query = new StringBuilder()
                .append("organization_id=eq.").append(organizationId)
                .append("&order=entry_date.desc,created_at.desc");

        if (from != null) query.append("&entry_date=gte.").append(from);
        if (to != null) query.append("&entry_date=lte.").append(to);

        var entries = supabaseService.getListWithQuery("app.activity_records", query.toString(), ActivityRecordResponse.class);
        return Activity.ofList(entries);
    }
}
