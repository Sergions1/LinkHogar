import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AddExpenseModal } from './add-expense-modal';

describe('AddExpenseModal', () => {
  let component: AddExpenseModal;
  let fixture: ComponentFixture<AddExpenseModal>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AddExpenseModal]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AddExpenseModal);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
