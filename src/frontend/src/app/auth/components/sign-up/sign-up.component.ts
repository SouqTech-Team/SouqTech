import { ChangeDetectionStrategy, Component, EventEmitter, Input, Output } from '@angular/core';
import { FormBuilder, FormControl, Validators } from '@angular/forms';
import { equalFieldsValidator } from '../../../shared/utils/equal-fields.validator';
import { EqualFieldsErrorStateMatcher, BasicErrorStateMatcher } from '../../../shared/utils/error-state-matchers';
import { SignUpRequest } from '../../../shared/models/auth.model';


@Component({
  selector: 'app-sign-up',
  templateUrl: './sign-up.component.html',
  styleUrls: ['./sign-up.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class SignUpComponent {
  /* Input/Output */
  @Input() disabled = false;
  @Output() formSubmitted = new EventEmitter<SignUpRequest>();
  /* Password Visibility */
  isPasswordVisible = false;
  isPasswordRepeatVisible = false;
  /* Form */
  form = this.fb.group({
    email: ['', [Validators.required, Validators.email]],
    name: ['', [Validators.required]],
    surname: ['', [Validators.required]],
    password: ['', [Validators.required]],
    passwordRepeat: ['', [Validators.required]],
  }, {
    validator: equalFieldsValidator('password', 'passwordRepeat')
  });
  equalFieldsErrorStateMatcher = new EqualFieldsErrorStateMatcher();
  basicErrorStateMatcher = new BasicErrorStateMatcher();

  constructor(private fb: FormBuilder) {}

  togglePasswordVisibility(): void {
    this.isPasswordVisible = !this.isPasswordVisible;
  }

  togglePasswordRepeatVisibility(): void {
    this.isPasswordRepeatVisible = !this.isPasswordRepeatVisible;
  }

  onFormSubmit(): void {
    if (this.form.valid) {
      const signUpCredentials = this.form.value;
      delete signUpCredentials.passwordRepeat;
      this.formSubmitted.emit(signUpCredentials);
    }
  }

  /* Getters to access form controls */
  get email(): FormControl {
    return this.form.get('email')! as FormControl;
  }
  get name(): FormControl {
    return this.form.get('name')! as FormControl;
  }
  get surname(): FormControl {
    return this.form.get('surname')! as FormControl;
  }
  get password(): FormControl {
    return this.form.get('password')! as FormControl;
  }
  get passwordRepeat(): FormControl {
    return this.form.get('passwordRepeat')! as FormControl;
  }
}