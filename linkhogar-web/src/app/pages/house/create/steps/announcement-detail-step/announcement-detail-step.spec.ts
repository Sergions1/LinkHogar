import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AnnouncementDetailStep } from './announcement-detail-step';

describe('AnnouncementDetailStep', () => {
  let component: AnnouncementDetailStep;
  let fixture: ComponentFixture<AnnouncementDetailStep>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AnnouncementDetailStep]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AnnouncementDetailStep);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
