import { Component, signal } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './login.html'
})
export class LoginComponent {
  email = '';
  password = '';
  showPassword = signal(false);

  constructor(private router: Router) {}

  togglePassword() {
    this.showPassword.update(v => !v);
  }

  onLogin() {
    // Aquí iría tu llamada al AuthService
    console.log('Login attempt', this.email);
    this.router.navigate(['/chat-list']);
  }
}