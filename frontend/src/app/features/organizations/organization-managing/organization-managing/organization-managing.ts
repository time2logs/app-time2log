import { Component, inject, OnInit, signal } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { TranslateModule, TranslateService } from '@ngx-translate/core';
import { OrganizationService } from '@services/organization.service';
import { ToastService } from '@services/toast.service';
import { Organization } from '@app/core/models/organizations.models';
import { Profile } from '@app/core/models/profile.models';

type Tab = 'members' | 'activities' | 'settings';

@Component({
  selector: 'app-organization-managing',
  standalone: true,
  imports: [TranslateModule, FormsModule],
  templateUrl: './organization-managing.html',
})
export class OrganizationManaging implements OnInit {
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly organizationService = inject(OrganizationService);
  private readonly toast = inject(ToastService);
  private readonly translate = inject(TranslateService);

  protected readonly activeTab = signal<Tab>('members');
  protected readonly organization = signal<Organization | null>(null);

  protected readonly members = signal<Profile[]>([]);
  protected readonly showInviteForm = signal(false);
  protected readonly inviteUserId = signal('');
  protected readonly inviteRole = signal('member');
  protected readonly isInviting = signal(false);

  protected readonly showDeleteConfirm = signal(false);
  protected readonly isDeleting = signal(false);

  private organizationId = '';

  ngOnInit(): void {
    this.organizationId = this.route.snapshot.params['id'];
    this.loadOrganization();
    this.loadMembers();
  }

  protected setTab(tab: Tab): void {
    this.activeTab.set(tab);
  }

  protected goBack(): void {
    this.router.navigate(['/organizations']);
  }

  protected toggleInviteForm(): void {
    this.showInviteForm.update((v) => !v);
  }

  protected invite(): void {
    const userId = this.inviteUserId().trim();
    if (!userId) return;

    this.isInviting.set(true);

    this.organizationService.inviteToOrganization(this.organizationId, userId, this.inviteRole()).subscribe({
      next: () => {
        this.inviteUserId.set('');
        this.inviteRole.set('member');
        this.isInviting.set(false);
        this.toast.success(this.translate.instant('toast.inviteSuccess'));
        this.loadMembers();
      },
      error: () => {
        this.isInviting.set(false);
      },
    });
  }

  protected removeMember(member: Profile): void {
    this.organizationService.removeOrganizationMember(this.organizationId, member.id).subscribe({
      next: () => {
        this.toast.success(this.translate.instant('toast.memberRemoved'));
        this.loadMembers();
      },
    });
  }

  protected deleteOrganization(): void {
    this.isDeleting.set(true);
    this.organizationService.deleteOrganization(this.organizationId).subscribe({
      next: () => {
        this.toast.success(this.translate.instant('toast.organizationDeleted'));
        this.router.navigate(['/organizations']);
      },
      error: () => this.isDeleting.set(false),
    });
  }

  private loadOrganization(): void {
    this.organizationService.getOrganizations().subscribe({
      next: (orgs) => {
        const org = orgs.find((o) => o.id === this.organizationId);
        if (org) this.organization.set(org);
      },
    });
  }

  private loadMembers(): void {
    this.organizationService.getOrganizationMembers(this.organizationId).subscribe({
      next: (members) => this.members.set(members),
    });
  }
}
