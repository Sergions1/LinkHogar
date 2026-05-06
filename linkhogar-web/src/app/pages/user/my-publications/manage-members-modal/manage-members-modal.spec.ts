import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ManageMembersModal } from './manage-members-modal';

describe('ManageMembersModal', () => {
  let component: ManageMembersModal;
  let fixture: ComponentFixture<ManageMembersModal>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ManageMembersModal]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ManageMembersModal);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
