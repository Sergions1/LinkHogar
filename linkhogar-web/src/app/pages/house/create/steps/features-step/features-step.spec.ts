import { ComponentFixture, TestBed } from '@angular/core/testing';

import { FeaturesStep } from './features-step';

describe('FeaturesStep', () => {
  let component: FeaturesStep;
  let fixture: ComponentFixture<FeaturesStep>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [FeaturesStep]
    })
    .compileComponents();

    fixture = TestBed.createComponent(FeaturesStep);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
