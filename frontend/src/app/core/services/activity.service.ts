import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { environment } from '@env/environment';
import { Observable } from 'rxjs';
import { ActivityRecord, CreateActivityRecordRequest, UpdateActivityRecordRequest } from '@app/core/models/activity-record.models';

@Injectable({ providedIn: 'root' })
export class ActivityService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = `${environment.apiBaseUrl}/activity-records`;

  getRecords(organizationId: string, from?: string, to?: string): Observable<ActivityRecord[]> {
    let params = new HttpParams().set('organizationId', organizationId);
    if (from) params = params.set('from', from);
    if (to) params = params.set('to', to);
    return this.http.get<ActivityRecord[]>(this.baseUrl, { params });
  }

  create(payload: CreateActivityRecordRequest): Observable<ActivityRecord> {
    return this.http.post<ActivityRecord>(this.baseUrl, payload);
  }

  update(id: string, payload: UpdateActivityRecordRequest): Observable<ActivityRecord> {
    return this.http.patch<ActivityRecord>(`${this.baseUrl}/${id}`, payload);
  }

  delete(id: string): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }
}
