import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ManageRoomsModal } from './manage-rooms-modal';

describe('ManageRoomsModal', () => {
  let component: ManageRoomsModal;
  let fixture: ComponentFixture<ManageRoomsModal>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ManageRoomsModal]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ManageRoomsModal);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
