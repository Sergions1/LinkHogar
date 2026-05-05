export interface HomeEventResponse {
  id: string;
  creatorId: string;
  creatorName: string;
  title: string;
  description: string;
  startDate: string; // Vendrá como string ISO desde Java
  endDate?: string;
  allDay: boolean;
  reminderMinutesBefore: number;
}

export interface CreateEventRequest {
  homeId: string;
  creatorId: string;
  creatorName: string;
  title: string;
  description: string;
  startDate: string;
  endDate?: string;
  allDay: boolean;
  reminderMinutesBefore: number;
}
