import { inject, Injectable, signal } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { environment } from '../../../environments/environment';
import {ExpenseCategory} from '../../Models/expense/ExpenseCategory';
import {ExpenseResponse} from '../../Models/expense/ExpenseResponse';



export interface ExpenseSplitDto {
  debtorId: string;
  amount: number;
}

export interface CreateExpenseRequest {
  homeId: string;
  payerId: string;
  amount: number;
  description: string;
  category: ExpenseCategory;
  splits: ExpenseSplitDto[];
}

@Injectable({
  providedIn: 'root'
})
export class ExpenseService {
  private http = inject(HttpClient);
  private apiUrl = `${environment.apiUrl}/expenses`;

  // Caché en memoria
  homeExpenses = signal<ExpenseResponse[]>([]);

  /**
   * Obtiene la lista de gastos asociados a una vivienda específica.
   * @param homeId Identificador de la vivienda.
   * @returns Observable con el array de gastos.
   */
  getHomeExpenses(homeId: string): Observable<ExpenseResponse[]> {
    const token = localStorage.getItem('token');
    if (!token) return throwError(() => new Error('No hay token'));

    const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
    return this.http.get<ExpenseResponse[]>(`${this.apiUrl}/home/${homeId}`, { headers });
  }

  /**
   * Crea un nuevo gasto y define cómo se divide entre los miembros.
   * @param request Objeto con los datos del gasto y los desgloses (splits).
   * @returns Observable con el ID del gasto creado.
   */
  createExpense(request: CreateExpenseRequest): Observable<{id: string}> {
    const token = localStorage.getItem('token');
    const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
    return this.http.post<{id: string}>(this.apiUrl, request, { headers });
  }

  /**
   * Elimina un gasto existente del sistema.
   * @param expenseId Identificador único del gasto a eliminar.
   * @returns Observable que se completa al eliminar el gasto.
   */
  deleteExpense(expenseId: string): Observable<void> {
    const token = localStorage.getItem('token');
    const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
    return this.http.delete<void>(`${this.apiUrl}/${expenseId}`, { headers });
  }
}
