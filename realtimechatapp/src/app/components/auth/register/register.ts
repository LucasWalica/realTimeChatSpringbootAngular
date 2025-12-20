import { Component, signal } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { CommonModule } from '@angular/common';
import { Auth } from '../../../services/auth-service';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [ReactiveFormsModule, CommonModule, RouterLink],
  templateUrl: './register.html',
})
export class RegisterComponent {
  showPassword = signal(false);
  isLoading = signal(false);
  errorMessage = signal('');

  registerForm = new FormGroup({
    email: new FormControl("", [Validators.required, Validators.email]),
    username: new FormControl("", [Validators.required, Validators.minLength(5), Validators.maxLength(25)]),
    password1: new FormControl("", [Validators.required, Validators.minLength(6), Validators.maxLength(20)]),
    password2: new FormControl("", [Validators.required, Validators.minLength(6), Validators.maxLength(20)])
  }, {
    validators: passwordConfirmationValidator("password1", "password2")
  });

  constructor(private router: Router, private auth: Auth) {}

  togglePassword() {
    this.showPassword.update(v => !v);
  }

  onRegister() {
    if (this.registerForm.invalid) return;

    this.isLoading.set(true);
    this.errorMessage.set('');

    const { username, email, password1 } = this.registerForm.getRawValue();

    this.auth.register(username!, email!, password1!).subscribe({
      next: () => {
        console.log("saliendo")
        this.isLoading.set(false);
        this.router.navigate(['/login']);
      },
      error: (err) => {
        console.log("error", err)
        this.isLoading.set(false);
        this.errorMessage.set(err.error?.message || 'Error al crear la cuenta');
      }
    });
  }
}


import { AbstractControl, ValidationErrors, ValidatorFn } from "@angular/forms";

export function passwordConfirmationValidator(
  controlName: string,
  matchingControlName: string
): ValidatorFn {
  return (formGroup: AbstractControl): ValidationErrors | null => {
    const passwordControl = formGroup.get(controlName);
    const confirmPasswordControl = formGroup.get(matchingControlName);

    if (!passwordControl || !confirmPasswordControl) {
      return null;
    }

    if (passwordControl.value !== confirmPasswordControl.value) {
      confirmPasswordControl.setErrors({ passwordMismatch: true });
    } else {
      confirmPasswordControl.setErrors(null);
    }

    return null;
  };
}


