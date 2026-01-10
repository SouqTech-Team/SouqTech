import { ChangeDetectionStrategy, Component, EventEmitter, Input, Output } from '@angular/core';
import { FormBuilder, FormControl, Validators } from '@angular/forms';
import { BasicErrorStateMatcher } from '../../../shared/utils/error-state-matchers';
import { SignInRequest } from '../../../shared/models/auth.model';

@Component({
  selector: 'app-sign-in',
  templateUrl: './sign-in.component.html',
  styleUrls: ['./sign-in.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class SignInComponent {
  /* Input/Output */
  @Input() disabled = false;
  @Output() formSubmitted = new EventEmitter<SignInRequest>();
  /* Password Visibility */
  isPasswordVisible = false;
  /* Form */
  form = this.fb.group({
    email: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required]],
  });
  matcher = new BasicErrorStateMatcher();

  constructor(private fb: FormBuilder) {}

  toggleVisibility(): void {
    this.isPasswordVisible = !this.isPasswordVisible;
  }

  onFormSubmit(): void {
    if (this.form.valid) {
      this.formSubmitted.emit(<SignInRequest>this.form.value);
    }
  }

  /* Getters to access form controls */
  get email(): FormControl {
    return this.form.get('email')! as FormControl;
  }
  get password(): FormControl {
    return this.form.get('password')! as FormControl;
  }
}