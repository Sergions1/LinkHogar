import { ComponentFixture, TestBed } from '@angular/core/testing';

import { HouseForm } from './house-form';

describe('HouseForm', () => {
  let component: HouseForm;
  let fixture: ComponentFixture<HouseForm>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [HouseForm]
    })
    .compileComponents();

    fixture = TestBed.createComponent(HouseForm);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
