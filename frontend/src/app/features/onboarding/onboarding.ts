import { Component, OnInit, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { TranslateModule, TranslateService } from '@ngx-translate/core';
import { filter, firstValueFrom, take } from 'rxjs';
import { AuthService } from '@services/auth.service';
import { ToastService } from '@services/toast.service';

type State = 'loading' | 'ready' | 'saving' | 'done' | 'invalid' | 'expired';

interface InviteDetails {
  organization_name: string;
  email: string;
  role: string;
}

@Component({
  selector: 'app-onboarding',
  standalone: true,
  imports: [FormsModule, TranslateModule],
  templateUrl: './onboarding.html',
})
export class OnboardingPage implements OnInit {
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly authService = inject(AuthService);
  private readonly toast = inject(ToastService);
  private readonly translate = inject(TranslateService);

  protected readonly state = signal<State>('loading');
  protected readonly inviteDetails = signal<InviteDetails | null>(null);
  protected readonly firstName = signal('');
  protected readonly lastName = signal('');
  protected readonly password = signal('');
  protected readonly confirmPassword = signal('');
  protected readonly showPassword = signal(false);
  protected readonly showConfirmPassword = signal(false);
  protected readonly validationError = signal<string | null>(null);

  private inviteToken = '';

  async ngOnInit(): Promise<void> {
    this.inviteToken = this.route.snapshot.queryParams['invite_token'] ?? '';

    if (!this.inviteToken) {
      this.state.set('invalid');
      return;
    }

    // Wait for auth to be initialized (detectSessionInUrl processes the hash)
    await firstValueFrom(
      this.authService.isInitialized$.pipe(filter(Boolean), take(1))
    );

    if (!this.authService.isAuthenticated()) {
      const returnUrl = `/onboarding?invite_token=${this.inviteToken}`;
      this.router.navigate(['/auth/login'], { queryParams: { redirectTo: returnUrl } });
      return;
    }

    await this.loadInviteDetails();
  }

  private async loadInviteDetails(): Promise<void> {
    try {
      const details = await this.authService.getInviteDetails(this.inviteToken);
      this.inviteDetails.set(details);
      this.state.set('ready');
    } catch (err: unknown) {
      const msg = ((err as { message?: string })?.message ?? '').toLowerCase();
      this.state.set(msg.includes('expired') ? 'expired' : 'invalid');
    }
  }

  async joinOrganization(): Promise<void> {
    if (!this.validate()) return;

    this.state.set('saving');
    this.validationError.set(null);

    try {
      await this.authService.acceptInvite(this.inviteToken);

      await firstValueFrom(
        this.authService.updateProfile(this.firstName().trim(), this.lastName().trim())
      );

      await firstValueFrom(
        this.authService.updatePassword(this.password())
      );

      this.state.set('done');
      setTimeout(() => this.router.navigate(['/dashboard']), 2000);
    } catch {
      this.state.set('ready');
      this.toast.error(this.translate.instant('errors.generic'));
    }
  }

  private validate(): boolean {
    if (this.firstName().trim().length < 2) {
      this.validationError.set(this.translate.instant('auth.validation.firstNameMin'));
      return false;
    }
    if (this.lastName().trim().length < 2) {
      this.validationError.set(this.translate.instant('auth.validation.lastNameMin'));
      return false;
    }
    if (this.password().length < 6) {
      this.validationError.set(this.translate.instant('auth.validation.passwordMin'));
      return false;
    }
    if (this.password() !== this.confirmPassword()) {
      this.validationError.set(this.translate.instant('onboarding.passwordMismatch'));
      return false;
    }
    this.validationError.set(null);
    return true;
  }
}
