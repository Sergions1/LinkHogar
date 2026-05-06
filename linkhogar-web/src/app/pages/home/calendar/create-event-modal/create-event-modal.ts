import {Component, EventEmitter, inject, Input, OnInit, Output, signal} from '@angular/core';
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import {EventService} from '../../../../services/event/event-service';
import {CreateEventRequest, HomeEventResponse} from '../../../../Models/event/eventModel';

@Component({
  selector: 'app-create-event-modal',
  imports: [
    ReactiveFormsModule
  ],
  templateUrl: './create-event-modal.html',
  styleUrl: './create-event-modal.scss',
})
export class CreateEventModal implements OnInit {
  private formBuilder = inject(FormBuilder);
  private eventService = inject(EventService);

  @Input() homeId!: string;
  @Input() myUserId!: string;
  @Input() myUserName!: string;
  @Input() initialDate: string | null = null;
  @Input() eventToEdit: HomeEventResponse | null = null;

  @Output() close = new EventEmitter<void>();
  @Output() eventCreated = new EventEmitter<void>();

  eventForm: FormGroup;
  isSubmitting = signal<boolean>(false);

  constructor() {
    this.eventForm = this.formBuilder.group({
      title: ['', Validators.required],
      description: [''],
      startDate: ['', Validators.required],
      endDate: [''],
      allDay: [false],
      reminderMinutesBefore: [0]
    });
  }

  ngOnInit() {
    if (this.eventToEdit) {
      this.eventForm.patchValue({
        title: this.eventToEdit.title,
        description: this.eventToEdit.description,
        startDate: this.formatDateForInput(this.eventToEdit.startDate),
        endDate: this.eventToEdit.endDate ? this.formatDateForInput(this.eventToEdit.endDate) : '',
        allDay: this.eventToEdit.allDay,
        reminderMinutesBefore: this.eventToEdit.reminderMinutesBefore
      });
    } else if (this.initialDate) {
      this.eventForm.patchValue({ startDate: this.formatDateForInput(this.initialDate) });
    }

    //Bloquear inputs de fecha si es "Tod el día"
    this.eventForm.get('allDay')?.valueChanges.subscribe(isAllDay => {
      if (isAllDay) {
        this.eventForm.get('endDate')?.disable();
      } else {
        this.eventForm.get('endDate')?.enable();
      }
    });
  }

  //Helper para asegurar que la fecha se muestra bien en el input HTML
  private formatDateForInput(dateString: string): string {
    const date = new Date(dateString);
    const tzOffset = date.getTimezoneOffset() * 60000;
    return (new Date(date.getTime() - tzOffset)).toISOString().slice(0, 16);
  }

  submit() {
    if (this.eventForm.invalid || this.isSubmitting()) return;

    this.isSubmitting.set(true);
    const formValue = this.eventForm.value;

    const request: CreateEventRequest = {
      homeId: this.homeId,
      creatorId: this.myUserId,
      creatorName: this.myUserName,
      title: formValue.title,
      description: formValue.description,
      startDate: new Date(formValue.startDate).toISOString(),
      endDate: formValue.endDate ? new Date(formValue.endDate).toISOString() : undefined,
      allDay: formValue.allDay,
      reminderMinutesBefore: Number(formValue.reminderMinutesBefore)
    };

    if (this.eventToEdit) {

      this.eventService.updateEvent(this.eventToEdit.id, request).subscribe({
        next: () => {
          this.isSubmitting.set(false);
          this.eventCreated.emit();
          this.close.emit();
        },
        error: (err) => {
          console.error('Error editando evento', err);
          this.isSubmitting.set(false);
        }
      });
      this.isSubmitting.set(false);
    } else {
      this.eventService.createEvent(request).subscribe({
        next: () => {
          this.isSubmitting.set(false);
          this.eventCreated.emit();
          this.close.emit();
        },
        error: (err) => {
          console.error('Error creando evento', err);
          this.isSubmitting.set(false);
        }
      });
    }
  }
}
