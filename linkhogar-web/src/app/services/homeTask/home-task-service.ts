import {inject, Injectable} from '@angular/core';
import {environment} from '../../../environments/environment';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {map, Observable} from 'rxjs';
import {HomeTaskResponse, StatusTranslator, TaskStatusBackend} from '../../Models/homeTasks/HomeTaskResponse';

@Injectable({
  providedIn: 'root',
})
export class HomeTaskService {
  private http = inject(HttpClient);
  private apiUrl = `${environment.apiUrl}/homeTasks`;

  getTasksByHome(homeId: string): Observable<HomeTaskResponse[]> {
    const token = localStorage.getItem('token');
    const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);

    return this.http.get<any[]>(`${this.apiUrl}/getByHome/${homeId}`, {headers}).pipe(
      map(tasks => tasks.map(task => ({
        ...task,
        status: StatusTranslator[task.status as TaskStatusBackend] || 'por hacer' // Traducimos el status usando el diccionario
      } as HomeTaskResponse)))
    );
  }

  createTask(taskData: any): Observable<{taskId: string}> {
    const token = localStorage.getItem('token');
    const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);

    return this.http.post<{taskId: string}>(`${this.apiUrl}/create`, taskData, {headers});
  }

  updateTaskStatus(taskId: string, newStatus: string): Observable<void> {
    const token = localStorage.getItem('token');
    const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);

    return this.http.patch<void>(`${this.apiUrl}/${taskId}/status`, { status: newStatus }, { headers });
  }

  deleteTask(taskId: string): Observable<void> {
    const token = localStorage.getItem('token');
    const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);

    return this.http.delete<void>(`${this.apiUrl}/${taskId}`, { headers });
  }
}
