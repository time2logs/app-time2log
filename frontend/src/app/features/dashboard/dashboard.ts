import { Component, OnInit, inject, signal, computed } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { TranslateModule } from '@ngx-translate/core';
import { forkJoin } from 'rxjs';
import { OrganizationService } from '@services/organization.service';
import { AppContextService } from '@services/app-context.service';
import { ActivityService } from '@services/activity.service';
import { Organization } from '@app/core/models/organizations.models';
import { CurriculumNode } from '@app/core/models/app-context.models';
import { ActivityRecord } from '@app/core/models/activity-record.models';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [TranslateModule, RouterLink, FormsModule],
  templateUrl: './dashboard.html',
})
export class DashboardComponent implements OnInit {
  private readonly organizationService = inject(OrganizationService);
  private readonly appContextService = inject(AppContextService);
  private readonly activityService = inject(ActivityService);

  protected readonly isLoading = signal(true);
  protected readonly organizations = signal<Organization[]>([]);
  protected readonly selectedOrganizationId = signal('');
  protected readonly records = signal<ActivityRecord[]>([]);
  protected readonly allActivities = signal<CurriculumNode[]>([]);

  protected readonly totalHours = computed(() =>
    this.records().reduce((sum, e) => sum + e.hours, 0)
  );

  protected readonly thisWeekHours = computed(() => {
    const now = new Date();
    const startOfWeek = new Date(now);
    const day = startOfWeek.getDay();
    const offset = day === 0 ? 6 : day - 1;
    startOfWeek.setDate(startOfWeek.getDate() - offset);
    startOfWeek.setHours(0, 0, 0, 0);
    return this.records()
      .filter((e) => new Date(e.entryDate) >= startOfWeek)
      .reduce((sum, e) => sum + e.hours, 0);
  });

  protected readonly thisMonthHours = computed(() => {
    const now = new Date();
    return this.records()
      .filter((e) => {
        const d = new Date(e.entryDate);
        return d.getFullYear() === now.getFullYear() && d.getMonth() === now.getMonth();
      })
      .reduce((sum, e) => sum + e.hours, 0);
  });

  protected readonly recentRecords = computed(() => this.records().slice(0, 5));

  private readonly activityMap = computed(() => {
    const map = new Map<string, string>();
    for (const a of this.allActivities()) {
      map.set(a.id, a.label);
    }
    return map;
  });

  ngOnInit(): void {
    this.organizationService.getOrganizations().subscribe({
      next: (organizations) => {
        this.organizations.set(organizations);
        if (organizations.length === 0) {
          this.isLoading.set(false);
          return;
        }
        this.selectedOrganizationId.set(organizations[0].id);
        this.refreshMetrics();
      },
      error: () => this.isLoading.set(false),
    });
  }

  protected onOrgChange(orgId: string): void {
    this.selectedOrganizationId.set(orgId);
    this.refreshMetrics();
  }

  protected currentOrganizationName(): string {
    return this.organizations().find((o) => o.id === this.selectedOrganizationId())?.name ?? '-';
  }

  protected getActivityLabel(id: string): string {
    return this.activityMap().get(id) ?? id;
  }

  private refreshMetrics(): void {
    const orgId = this.selectedOrganizationId();
    if (!orgId) return;

    this.isLoading.set(true);

    forkJoin({
      records: this.activityService.getRecords(orgId),
      activities: this.appContextService.getCurriculumNodes(orgId, { nodeType: 'activity' }),
    }).subscribe({
      next: ({ records, activities }) => {
        this.records.set(records);
        this.allActivities.set(activities);
        this.isLoading.set(false);
      },
      error: () => this.isLoading.set(false),
    });
  }
}
