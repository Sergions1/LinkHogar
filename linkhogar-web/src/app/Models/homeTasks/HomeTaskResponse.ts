// Lo que viene de Spring Boot
export type TaskStatusBackend = 'todo' | 'inProgress' | 'done';

// Lo que usaremos en Angular (nuestro modelo real)
export type TaskStatus = 'Por hacer' | 'En curso' | 'Finalizada';

// Traductor: Backend -> Frontend
export const StatusTranslator: Record<TaskStatusBackend, TaskStatus> = {
  'todo': 'Por hacer',
  'inProgress': 'En curso',
  'done': 'Finalizada'
};

// Traductor: Frontend -> Backend
export const StatusReverseTranslator: Record<TaskStatus, TaskStatusBackend> = {
  'Por hacer': 'todo',
  'En curso': 'inProgress',
  'Finalizada': 'done'
};

export interface HomeTaskResponse {
  id: string;
  title: string;
  description: string;
  status: TaskStatus;
  assignedUserId?: string;
  assignedUserName?: string;
  startDate?: string;
  dueDate?: string;
  completedAt?: string;
}
