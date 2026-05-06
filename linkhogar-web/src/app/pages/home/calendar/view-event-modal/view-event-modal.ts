import {Component, EventEmitter, Input, Output} from '@angular/core';
import {HomeEventResponse} from '../../../../Models/event/eventModel';
import {DatePipe} from '@angular/common';

@Component({
  selector: 'app-view-event-modal',
  imports: [
    DatePipe
  ],
  templateUrl: './view-event-modal.html',
  styleUrl: './view-event-modal.scss',
})
export class ViewEventModal {
  @Input() event!: HomeEventResponse;

  @Output() close = new EventEmitter<void>();
  @Output() edit = new EventEmitter<HomeEventResponse>();

  onEdit() {
    this.edit.emit(this.event);
  }
}
