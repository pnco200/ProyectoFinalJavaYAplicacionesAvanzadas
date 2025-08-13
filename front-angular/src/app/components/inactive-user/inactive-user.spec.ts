import { ComponentFixture, TestBed } from '@angular/core/testing';

import { InactiveUser } from './inactive-user';

describe('InactiveUser', () => {
  let component: InactiveUser;
  let fixture: ComponentFixture<InactiveUser>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [InactiveUser]
    })
    .compileComponents();

    fixture = TestBed.createComponent(InactiveUser);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
