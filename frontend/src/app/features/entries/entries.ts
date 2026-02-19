import { Component, OnInit, inject, signal, computed } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { TranslateModule, TranslateService } from '@ngx-translate/core';
import { OrganizationService } from '@services/organization.service';
import { AppContextService } from '@services/app-context.service';
import { ActivityService } from '@services/activity.service';
import { ToastService } from '@services/toast.service';
import { CurriculumNode, Profession } from '@app/core/models/app-context.models';
import { ActivityRecord } from '@app/core/models/activity-record.models';

@Component({
  selector: 'app-entries',
  standalone: true,
  imports: [FormsModule, TranslateModule],
  templateUrl: './entries.html',
})
export class EntriesComponent implements OnInit {
  private readonly organizationService = inject(OrganizationService);
  private readonly appContextService = inject(AppContextService);
  private readonly activityService = inject(ActivityService);
  private readonly toast = inject(ToastService);
  private readonly translate = inject(TranslateService);

  // Context – auto-selected, hidden from UI
  protected readonly selectedOrganizationId = signal('');
  protected readonly selectedProfessionId = signal('');

  // New entry form
  protected readonly entryDate = signal(new Date().toISOString().slice(0, 10));
  protected readonly hours = signal(1);
  protected readonly notes = signal('');
  protected readonly isSaving = signal(false);

  // Category browser
  protected readonly breadcrumb = signal<CurriculumNode[]>([]);
  protected readonly currentNodes = signal<CurriculumNode[]>([]);
  protected readonly isLoadingNodes = signal(false);
  protected readonly selectedActivity = signal<CurriculumNode | null>(null);

  protected readonly currentParent = computed(() => {
    const crumb = this.breadcrumb();
    return crumb.length > 0 ? crumb[crumb.length - 1] : null;
  });

  // History
  protected readonly records = signal<ActivityRecord[]>([]);
  protected readonly allActivities = signal<CurriculumNode[]>([]);

  private readonly activityMap = computed(() => {
    const map = new Map<string, string>();
    for (const a of this.allActivities()) {
      map.set(a.id, a.label);
    }
    return map;
  });

  // Edit modal
  protected readonly editingRecord = signal<ActivityRecord | null>(null);
  protected readonly editDate = signal('');
  protected readonly editHours = signal(1);
  protected readonly editNotes = signal('');
  protected readonly editActivityId = signal('');
  protected readonly isUpdating = signal(false);

  // Delete confirm
  protected readonly deletingRecordId = signal<string | null>(null);

  ngOnInit(): void {
    this.organizationService.getOrganizations().subscribe({
      next: (orgs) => {
        if (orgs.length === 0) return;
        this.selectedOrganizationId.set(orgs[0].id);
        this.initOrg(orgs[0].id);
      },
    });
  }

  private initOrg(orgId: string): void {
    this.appContextService.getProfessions(orgId).subscribe({
      next: (professions: Profession[]) => {
        if (professions.length === 0) return;
        this.selectedProfessionId.set(professions[0].id);
        this.loadRootNodes();
      },
    });

    this.appContextService.getCurriculumNodes(orgId, { nodeType: 'activity' }).subscribe({
      next: (acts) => this.allActivities.set(acts),
    });

    this.loadRecords();
  }

  private loadRootNodes(): void {
    const orgId = this.selectedOrganizationId();
    const profId = this.selectedProfessionId();
    if (!orgId || !profId) return;

    this.isLoadingNodes.set(true);
    this.appContextService.getCurriculumNodes(orgId, { professionId: profId }).subscribe({
      next: (nodes) => {
        this.currentNodes.set(nodes.filter((n) => n.active));
        this.isLoadingNodes.set(false);
      },
      error: () => this.isLoadingNodes.set(false),
    });
  }

  private loadChildren(parentId: string): void {
    const orgId = this.selectedOrganizationId();
    const profId = this.selectedProfessionId();

    this.isLoadingNodes.set(true);
    this.appContextService.getCurriculumNodes(orgId, { professionId: profId, parentId }).subscribe({
      next: (nodes) => {
        this.currentNodes.set(nodes.filter((n) => n.active));
        this.isLoadingNodes.set(false);
      },
      error: () => this.isLoadingNodes.set(false),
    });
  }

  protected selectNode(node: CurriculumNode): void {
    if (node.nodeType === 'activity') {
      this.selectedActivity.set(node);
    } else {
      this.breadcrumb.update((b) => [...b, node]);
      this.loadChildren(node.id);
    }
  }

  protected navigateBack(): void {
    const newCrumb = this.breadcrumb().slice(0, -1);
    this.breadcrumb.set(newCrumb);
    if (newCrumb.length === 0) {
      this.loadRootNodes();
    } else {
      this.loadChildren(newCrumb[newCrumb.length - 1].id);
    }
  }

  protected clearActivity(): void {
    this.selectedActivity.set(null);
    const crumb = this.breadcrumb();
    if (crumb.length === 0) {
      this.loadRootNodes();
    } else {
      this.loadChildren(crumb[crumb.length - 1].id);
    }
  }

  protected adjustHours(delta: number): void {
    const v = this.hours() + delta;
    if (v >= 1 && v <= 24) this.hours.set(v);
  }

  protected saveEntry(): void {
    const orgId = this.selectedOrganizationId();
    const profId = this.selectedProfessionId();
    const activity = this.selectedActivity();
    if (!orgId || !profId || !activity) return;

    this.isSaving.set(true);
    this.activityService.create({
      organizationId: orgId,
      professionId: profId,
      curriculumActivityId: activity.id,
      entryDate: this.entryDate(),
      hours: this.hours(),
      notes: this.notes().trim() || undefined,
      rating: null,
    }).subscribe({
      next: () => {
        this.toast.success(this.translate.instant('toast.entryCreated'));
        this.notes.set('');
        this.hours.set(1);
        this.selectedActivity.set(null);
        this.breadcrumb.set([]);
        this.isSaving.set(false);
        this.loadRootNodes();
        this.loadRecords();
      },
      error: () => this.isSaving.set(false),
    });
  }

  protected getActivityLabel(id: string): string {
    return this.activityMap().get(id) ?? '-';
  }

  private loadRecords(): void {
    const orgId = this.selectedOrganizationId();
    if (!orgId) return;
    this.activityService.getRecords(orgId).subscribe({
      next: (records) => this.records.set(records),
    });
  }

  protected startEdit(record: ActivityRecord): void {
    this.editingRecord.set(record);
    this.editDate.set(record.entryDate);
    this.editHours.set(record.hours);
    this.editNotes.set(record.notes ?? '');
    this.editActivityId.set(record.curriculumActivityId);
  }

  protected cancelEdit(): void {
    this.editingRecord.set(null);
  }

  protected adjustEditHours(delta: number): void {
    const v = this.editHours() + delta;
    if (v >= 1 && v <= 24) this.editHours.set(v);
  }

  protected saveEdit(): void {
    const record = this.editingRecord();
    if (!record) return;

    this.isUpdating.set(true);
    this.activityService.update(record.id, {
      curriculumActivityId: this.editActivityId() || undefined,
      entryDate: this.editDate(),
      hours: this.editHours(),
      notes: this.editNotes().trim(),
      rating: record.rating,
    }).subscribe({
      next: () => {
        this.toast.success(this.translate.instant('toast.entryUpdated'));
        this.isUpdating.set(false);
        this.editingRecord.set(null);
        this.loadRecords();
      },
      error: () => this.isUpdating.set(false),
    });
  }

  protected requestDelete(id: string): void {
    this.deletingRecordId.set(id);
  }

  protected cancelDelete(): void {
    this.deletingRecordId.set(null);
  }

  protected confirmDelete(id: string): void {
    this.activityService.delete(id).subscribe({
      next: () => {
        this.toast.success(this.translate.instant('toast.entryDeleted'));
        this.deletingRecordId.set(null);
        this.loadRecords();
      },
    });
  }
}
