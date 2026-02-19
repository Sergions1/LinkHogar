import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EntityCardView } from './entity-card-view';

describe('EntityCardView', () => {
  let component: EntityCardView;
  let fixture: ComponentFixture<EntityCardView>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [EntityCardView]
    })
    .compileComponents();

    fixture = TestBed.createComponent(EntityCardView);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
