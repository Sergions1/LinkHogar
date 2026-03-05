import { ComponentFixture, TestBed } from '@angular/core/testing';

import { Infoannouncement } from './infoannouncement.component';

describe('Infoaunnouncement', () => {
  let component: Infoannouncement;
  let fixture: ComponentFixture<Infoannouncement>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Infoannouncement]
    })
    .compileComponents();

    fixture = TestBed.createComponent(Infoannouncement);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
