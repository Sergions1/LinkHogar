import { TestBed } from '@angular/core/testing';

import { SettingsServices } from './settings-services';

describe('SettingsServices', () => {
  let service: SettingsServices;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(SettingsServices);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
