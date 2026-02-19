export interface Profession {
  id: string;
  organizationId: string;
  key: string;
  label: string;
}

export interface Team {
  id: string;
  organizationId: string;
  professionId: string;
  name: string;
}

export interface CurriculumNode {
  id: string;
  parentId: string | null;
  nodeType: 'category' | 'activity';
  key: string;
  label: string;
  description: string | null;
  sortOrder: number;
  active: boolean;
}
