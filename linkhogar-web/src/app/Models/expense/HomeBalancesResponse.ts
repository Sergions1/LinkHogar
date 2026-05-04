import {UserBalanceDto} from './UserBalanceDto';
import {DebtRepaymentDto} from './DebtRepaymentDto';

export interface HomeBalancesResponse {
  balances: UserBalanceDto[];
  repayments: DebtRepaymentDto[];
}
