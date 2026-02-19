import { ComponentFixture, TestBed } from '@angular/core/testing';

import { HouseCard } from './house-card';

describe('HouseCard', () => {
  let component: HouseCard;
  let fixture: ComponentFixture<HouseCard>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [HouseCard]
    })
    .compileComponents();

    fixture = TestBed.createComponent(HouseCard);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
