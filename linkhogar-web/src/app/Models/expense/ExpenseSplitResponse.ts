export interface ExpenseSplitResponse {
  id: string;
  debtorId: string;
  debtorName: string;
  amountOwed: number;
  paid: boolean; // El backend ahora devuelve esto
}
