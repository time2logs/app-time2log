export interface ActivityRecord {
  id: string;
  organizationId: string;
  professionId: string;
  userId: string;
  teamId: string | null;
  curriculumActivityId: string;
  entryDate: string;
  hours: number;
  notes: string | null;
  rating: number | null;
}

export interface CreateActivityRecordRequest {
  organizationId: string;
  professionId: string;
  curriculumActivityId: string;
  teamId?: string | null;
  entryDate: string;
  hours: number;
  notes?: string;
  rating?: number | null;
}

export interface UpdateActivityRecordRequest {
  curriculumActivityId?: string;
  teamId?: string | null;
  entryDate?: string;
  hours?: number;
  notes?: string;
  rating?: number | null;
}
