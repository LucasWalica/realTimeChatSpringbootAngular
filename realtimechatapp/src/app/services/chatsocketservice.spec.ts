import { TestBed } from '@angular/core/testing';

import { Chatsocketservice } from './chatsocketservice';

describe('Chatsocketservice', () => {
  let service: Chatsocketservice;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(Chatsocketservice);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
