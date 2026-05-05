import {ExpenseCategory} from './ExpenseCategory';
import {ExpenseSplitResponse} from './ExpenseSplitResponse';

export interface ExpenseResponse {
  id: string;
  payerId: string;
  amount: number;
  description: string;
  category: ExpenseCategory;
  createdAt: string;
  payerName?: string; // Opcional, por si luego lo traemos del back
  splits: ExpenseSplitResponse[];
}
