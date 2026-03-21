import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AdminHouses } from './admin-houses';

describe('AdminHouses', () => {
  let component: AdminHouses;
  let fixture: ComponentFixture<AdminHouses>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AdminHouses]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AdminHouses);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
