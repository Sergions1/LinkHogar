import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AdminHousesComponent } from './admin-houses';

describe('AdminHouses', () => {
  let component: AdminHousesComponent;
  let fixture: ComponentFixture<AdminHousesComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AdminHousesComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AdminHousesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
