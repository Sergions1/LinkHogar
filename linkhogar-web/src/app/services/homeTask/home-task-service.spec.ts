import { TestBed } from '@angular/core/testing';

import { HomeTaskService } from './home-task-service';

describe('HomeTaskService', () => {
  let service: HomeTaskService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(HomeTaskService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
