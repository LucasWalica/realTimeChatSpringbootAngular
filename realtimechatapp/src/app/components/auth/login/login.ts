import { Component, signal } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { CommonModule } from '@angular/common';
import { Auth } from '../../../services/auth-service';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [ReactiveFormsModule, CommonModule, RouterLink],
  templateUrl: './login.html'
})
export class LoginComponent {
  showPassword = signal(false);
  isLoading = signal(false);
  errorMessage = signal('');

  loginForm = new FormGroup({
    username: new FormControl("", [Validators.required]),
    password: new FormControl("", [Validators.required])
  });

  constructor(private router: Router, private auth: Auth) {}

  onLogin() {
    if (this.loginForm.invalid) return;

    this.isLoading.set(true);
    const { username, password } = this.loginForm.getRawValue();

    this.auth.login(username!, password!).subscribe({
      next: () => {
        this.isLoading.set(false);
        // Al usar HttpOnly cookies, Spring Boot ya envió la cookie
        this.router.navigate(['/dashboard']); 
      },
      error: (err) => {
        this.isLoading.set(false);
        this.errorMessage.set('Credenciales inválidas');
      }
    });
  }
}