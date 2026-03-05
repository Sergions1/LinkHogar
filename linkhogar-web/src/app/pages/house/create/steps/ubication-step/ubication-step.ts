// steps/ubication-step/ubication-step.ts
import { Component, EventEmitter, Input, OnInit, Output, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { debounceTime, distinctUntilChanged, from, Subject, switchMap } from 'rxjs';

export interface UbicationData {
  street: string;
  number: string;
  city: string;
  province: string;
  cp: string;
  floor?: string;
  door?: string;
}

@Component({
  selector: 'app-ubication-step',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './ubication-step.html',
})
export class UbicationStep implements OnInit {
  @Input() data!: UbicationData;
  @Output() validChange = new EventEmitter<boolean>();
  @Output() dataChange = new EventEmitter<UbicationData>();

  form = signal<UbicationData>({
    street: '', number: '', city: '',
    province: '', cp: '', floor: '', door: ''
  });

  isValidating = signal(false);
  isValid = signal(false);
  errorMessage = signal<string | null>(null);

  private searchSubject = new Subject<UbicationData>();

  ngOnInit() {
    if (this.data) this.form.set({ ...this.data });

    this.searchSubject.pipe(
      debounceTime(600),
      distinctUntilChanged(),
      switchMap(data => {
        this.isValidating.set(true);
        this.errorMessage.set(null);
        return from(this.validateAddress(data));
      })
    ).subscribe({
      next: (valid: boolean) => {
        this.isValidating.set(false);
        this.isValid.set(valid);
        this.validChange.emit(valid);
        if (!valid) {
          this.errorMessage.set('Address not found. Please check the details.');
        } else {
          this.dataChange.emit(this.form());
        }
      },
      error: () => {
        this.isValidating.set(false);
        this.isValid.set(false);
        this.errorMessage.set('Error validating address. Please try again.');
        this.validChange.emit(false);
      }
    });
  }

  onFieldChange() {
    const f = this.form();
    this.isValid.set(false);
    this.validChange.emit(false);
    if (f.street.trim() && f.number.trim() && f.city.trim() && f.cp.trim()) {
      this.searchSubject.next(f);
    }
  }

  update(field: keyof UbicationData, value: string) {
    this.form.update(f => ({ ...f, [field]: value }));
    this.onFieldChange();
  }

  private validateAddress(data: UbicationData): Promise<boolean> {
    return new Promise(resolve => {
      setTimeout(() => {
        const valid = !!(data.street && data.number && data.city && data.cp);
        resolve(valid);
      }, 500);
    });
  }
}
