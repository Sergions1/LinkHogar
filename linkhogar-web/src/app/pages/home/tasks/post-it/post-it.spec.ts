import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PostIt } from './post-it';

describe('PostIt', () => {
  let component: PostIt;
  let fixture: ComponentFixture<PostIt>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PostIt]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PostIt);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
