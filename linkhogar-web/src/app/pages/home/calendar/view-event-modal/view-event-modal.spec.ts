import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ViewEventModal } from './view-event-modal';

describe('ViewEventModal', () => {
  let component: ViewEventModal;
  let fixture: ComponentFixture<ViewEventModal>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ViewEventModal]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ViewEventModal);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
