package ch.time2log.backend.api.rest;

import ch.time2log.backend.api.rest.dto.inbound.CreateActivityRecordRequest;
import ch.time2log.backend.api.rest.dto.inbound.UpdateActivityRecordRequest;
import ch.time2log.backend.api.rest.dto.outbound.ActivityRecordDto;
import ch.time2log.backend.domain.ActivityDomainService;
import ch.time2log.backend.domain.models.ActivityRecordFilter;
import ch.time2log.backend.domain.models.CreateActivityCommand;
import ch.time2log.backend.domain.models.UpdateActivityCommand;
import ch.time2log.backend.security.AuthenticatedUser;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/activity-records")
public class ActivityController {

    private final ActivityDomainService activityDomainService;

    public ActivityController(ActivityDomainService activityDomainService) {
        this.activityDomainService = activityDomainService;
    }

    @GetMapping
    public List<ActivityRecordDto> getActivityRecords(
            @RequestParam UUID organizationId,
            @RequestParam(required = false) LocalDate from,
            @RequestParam(required = false) LocalDate to
    ) {
        ActivityRecordFilter filter = new ActivityRecordFilter(from, to);
        return ActivityRecordDto.ofList(activityDomainService.getActivities(organizationId, filter));
    }

    @PostMapping
    public ActivityRecordDto createActivityRecord(
            @AuthenticationPrincipal AuthenticatedUser user,
            @RequestBody CreateActivityRecordRequest request
    ) {
        var command = new CreateActivityCommand(
                request.organizationId(),
                request.professionId(),
                user.id(),
                request.curriculumActivityId(),
                request.teamId(),
                request.entryDate(),
                request.hours(),
                request.notes(),
                request.rating()
        );
        return ActivityRecordDto.of(activityDomainService.createActivity(command));
    }

    @PatchMapping("/{id}")
    public ActivityRecordDto updateActivityRecord(
            @PathVariable UUID id,
            @RequestBody UpdateActivityRecordRequest request
    ) {
        var command = new UpdateActivityCommand(
                request.curriculumActivityId(),
                request.teamId(),
                request.entryDate(),
                request.hours(),
                request.notes(),
                request.rating()
        );
        if (command.curriculumActivityId() == null
                && command.teamId() == null
                && command.entryDate() == null
                && command.hours() == null
                && command.notes() == null
                && command.rating() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No fields to update");
        }
        return ActivityRecordDto.of(activityDomainService.updateActivity(id, command));
    }

    @DeleteMapping("/{id}")
    public void deleteActivityRecord(@PathVariable UUID id) {
        activityDomainService.deleteActivity(id);
    }
}
