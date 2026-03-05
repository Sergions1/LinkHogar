import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UbicationStep } from './ubication-step';

describe('UbicationStep', () => {
  let component: UbicationStep;
  let fixture: ComponentFixture<UbicationStep>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [UbicationStep]
    })
    .compileComponents();

    fixture = TestBed.createComponent(UbicationStep);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
