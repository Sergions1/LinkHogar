// steps/announcement-detail-step/announcement-detail-step.ts
import { Component, EventEmitter, Input, OnInit, Output, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

export interface AnnouncementDetailData {
  title: string;
  description: string;
}

@Component({
  selector: 'app-announcement-detail-step',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './announcement-detail-step.html',
  styleUrl: './announcement-detail-step.scss',
})
export class AnnouncementDetailStep implements OnInit {
  @Input() data!: AnnouncementDetailData;
  @Output() validChange = new EventEmitter<boolean>();
  @Output() dataChange = new EventEmitter<AnnouncementDetailData>();

  readonly MAX_TITLE = 50;
  readonly MAX_DESCRIPTION = 500;
  readonly MIN_DESCRIPTION = 20;

  form = signal<AnnouncementDetailData>({
    title: '',
    description: '',
  });

  ngOnInit() {
    if (this.data) this.form.set({ ...this.data });
    this.emitValidity();
  }

  update(field: keyof AnnouncementDetailData, value: string) {
    this.form.update(f => ({ ...f, [field]: value }));
    this.dataChange.emit(this.form());
    this.emitValidity();
  }

  private emitValidity() {
    const f = this.form();
    const valid = f.title.trim().length >= 5
      && f.description.trim().length >= this.MIN_DESCRIPTION;
    this.validChange.emit(valid);
  }
}
