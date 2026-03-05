import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PhotosStep } from './photos-step';

describe('PhotosStep', () => {
  let component: PhotosStep;
  let fixture: ComponentFixture<PhotosStep>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PhotosStep]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PhotosStep);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
