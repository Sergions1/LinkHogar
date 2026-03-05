import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PriceStep } from './price-step';

describe('PriceStep', () => {
  let component: PriceStep;
  let fixture: ComponentFixture<PriceStep>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PriceStep]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PriceStep);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
