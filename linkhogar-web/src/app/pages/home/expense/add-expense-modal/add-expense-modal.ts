import { Component, EventEmitter, Input, Output, signal, computed, inject, OnChanges } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ExpenseSplitDto, CreateExpenseRequest, ExpenseService } from '../../../../services/expense/expense-service';
import { ExpenseCategoryPipe } from '../../../../pipes/ExpenseCategoryPipe';
import {ExpenseCategory} from '../../../../Models/expense/ExpenseCategory';

@Component({
  selector: 'app-add-expense-modal',
  imports: [
    ExpenseCategoryPipe,
    FormsModule
  ],
  templateUrl: './add-expense-modal.html',
  styleUrl: './add-expense-modal.scss',
})
export class AddExpenseModal {
  private expenseService = inject(ExpenseService);

  @Input() homeId!: string;
  @Input() myUserId!: string;
  @Input() members: any[] = [];

  @Output() expenseCreated = new EventEmitter<void>();
  @Output() close = new EventEmitter<void>();

  amount = signal<number | null>(null);
  description = signal<string>('');
  category = signal<ExpenseCategory>(ExpenseCategory.SUPERMERCADO);

  selectedMemberIds = signal<Set<string>>(new Set());

  categories = Object.values(ExpenseCategory);

  isSubmitting = signal<boolean>(false);

  isValid = computed(() => {
    const amt = this.amount();
    const cat = this.category();
    const desc = this.description().trim();

    if (!amt || amt <= 0) return false;
    if (this.selectedMemberIds().size === 0) return false;
    if (cat === ExpenseCategory.OTROS && !desc) return false;

    return true;
  });

  ngOnChanges() {
    if (this.members.length > 0 && this.selectedMemberIds().size === 0) {
      this.selectedMemberIds.set(new Set(this.members.map(m => m.id)));
    }
  }

  toggleMember(memberId: string) {
    const current = new Set(this.selectedMemberIds());
    if (current.has(memberId)) {
      current.delete(memberId);
    } else {
      current.add(memberId);
    }
    this.selectedMemberIds.set(current);
  }

  private getMemberName(userId: string): string {
    const member = this.members.find(m => m.id === userId);
    return member ? `${member.name}` : 'Usuario';
  }

  submit() {
    if (!this.isValid() || this.isSubmitting()) return;

    this.isSubmitting.set(true);

    const totalAmount = this.amount()!;
    const participantsCount = this.selectedMemberIds().size;
    const splitAmount = parseFloat((totalAmount / participantsCount).toFixed(2));

    let sum = 0;
    const splits: ExpenseSplitDto[] = Array.from(this.selectedMemberIds()).map((id, index) => {
      let amountOwed = splitAmount;
      if (index === participantsCount - 1) {
        amountOwed = parseFloat((totalAmount - sum).toFixed(2));
      } else {
        sum += amountOwed;
      }
      return { debtorId: id, debtorName: this.getMemberName(id),amount: amountOwed };
    });

    const request: CreateExpenseRequest = {
      homeId: this.homeId,
      payerId: this.myUserId,
      payerName: this.getMemberName(this.myUserId),
      amount: totalAmount,
      description: this.description(),
      category: this.category(),
      splits: splits
    };

    this.expenseService.createExpense(request).subscribe({
      next: () => {
        this.isSubmitting.set(false);
        this.expenseCreated.emit();
        this.closeModal();
      },
      error: (err) => {
        console.error('Error creando gasto', err);
        this.isSubmitting.set(false);
      }
    });
  }

  closeModal() {
    this.close.emit();
  }
}
