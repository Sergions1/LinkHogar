import { inject, Injectable, signal } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { environment } from '../../../environments/environment';
import {ExpenseCategory} from '../../Models/expense/ExpenseCategory';
import {ExpenseResponse} from '../../Models/expense/ExpenseResponse';
import {HomeBalancesResponse} from '../../Models/expense/HomeBalancesResponse';
import {ExpenseSplitResponse} from '../../Models/expense/ExpenseSplitResponse';



export interface ExpenseSplitDto {
  debtorId: string;
  debtorName: string;
  amount: number;
}

export interface CreateExpenseRequest {
  homeId: string;
  payerId: string;
  payerName: string;
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
  homeBalances = signal<HomeBalancesResponse | null>(null);

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

  /**
   * Obtiene el balance general de deudas entre los miembros de una vivienda.
   * @param homeId Identificador de la vivienda.
   * @returns Observable con los balances calculados.
   */
  getHomeBalances(homeId: string): Observable<HomeBalancesResponse> {
    const token = localStorage.getItem('token');
    const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
    return this.http.get<HomeBalancesResponse>(`${this.apiUrl}/home/${homeId}/balances`, { headers });
  }

  /**
   * Obtiene el desglose detallado de un gasto específico (quién debe a quién).
   * @param expenseId Identificador del gasto.
   * @returns Observable con la lista de divisiones (splits) del gasto.
   */
  getExpenseSplits(expenseId: string): Observable<ExpenseSplitResponse[]> {
    const token = localStorage.getItem('token');
    const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
    return this.http.get<ExpenseSplitResponse[]>(`${this.apiUrl}/${expenseId}/splits`, { headers });
  }

  /**
   * Marca una división de gasto específica como pagada.
   * @param splitId Identificador de la división (split).
   * @returns Observable que se completa al realizar la actualización.
   */
  markSplitAsPaid(splitId: string): Observable<void> {
    const token = localStorage.getItem('token');
    const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
    return this.http.patch<void>(`${this.apiUrl}/splits/${splitId}/pay`, {}, { headers });
  }
}
