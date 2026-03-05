import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TypeStep } from './type-step';

describe('TypeStep', () => {
  let component: TypeStep;
  let fixture: ComponentFixture<TypeStep>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TypeStep]
    })
    .compileComponents();

    fixture = TestBed.createComponent(TypeStep);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
