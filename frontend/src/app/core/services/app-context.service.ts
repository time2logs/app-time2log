import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { environment } from '@env/environment';
import { Observable } from 'rxjs';
import { CurriculumNode, Profession } from '@app/core/models/app-context.models';

export interface CurriculumNodeFilter {
  professionId?: string;
  parentId?: string;
  nodeType?: string;
}

@Injectable({ providedIn: 'root' })
export class AppContextService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = `${environment.apiBaseUrl}`;

  getProfessions(organizationId: string): Observable<Profession[]> {
    const params = new HttpParams().set('organizationId', organizationId);
    return this.http.get<Profession[]>(`${this.baseUrl}/context/professions`, { params });
  }

  getCurriculumNodes(organizationId: string, filter: CurriculumNodeFilter = {}): Observable<CurriculumNode[]> {
    let params = new HttpParams().set('organizationId', organizationId);
    if (filter.professionId) params = params.set('professionId', filter.professionId);
    if (filter.parentId) params = params.set('parentId', filter.parentId);
    if (filter.nodeType) params = params.set('nodeType', filter.nodeType);
    return this.http.get<CurriculumNode[]>(`${this.baseUrl}/curriculum/nodes`, { params });
  }
}
