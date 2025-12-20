import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';

@Component({
  selector: 'app-register',
  standalone: true,
  // Importamos RouterLink para la navegación entre login y register
  imports: [CommonModule, FormsModule, RouterLink], 
  templateUrl: './register.html'
})
export class RegisterComponent {
  // Signals para el estado de la UI
  showPassword = signal(false);
  isLoading = signal(false);

  // Modelos de datos (puedes usarlos con [(ngModel)] en el HTML)
  registerData = {
    username: '',
    email: '',
    password: '',
    confirmPassword: ''
  };
username: any;

  constructor(private router: Router) {}

  togglePassword() {
    this.showPassword.update(v => !v);
  }

  onRegister() {
    if (this.registerData.password !== this.registerData.confirmPassword) {
      alert('Las contraseñas no coinciden');
      return;
    }

    this.isLoading.set(true);

    // Simulación de llamada a tu API de Spring Boot
    console.log('Registrando usuario:', this.registerData);

    setTimeout(() => {
      this.isLoading.set(false);
      // Tras el registro exitoso, redirigimos al login
      this.router.navigate(['/login']);
    }, 1500);
  }
}