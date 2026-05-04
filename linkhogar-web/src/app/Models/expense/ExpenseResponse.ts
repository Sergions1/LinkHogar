import {ExpenseCategory} from './ExpenseCategory';

export interface ExpenseResponse {
  id: string;
  payerId: string;
  amount: number;
  description: string;
  category: ExpenseCategory;
  createdAt: string;
  payerName?: string; // Opcional, por si luego lo traemos del back
}
