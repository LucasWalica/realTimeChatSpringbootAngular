import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import { apiurl } from '../environ/env.api';

@Injectable({
  providedIn: 'root',
})
export class Auth {
  // Ajustado para coincidir con @RequestMapping("/api/auth") de tu controlador
  private authUrl = `${apiurl}api/auth/`;

  constructor(private http: HttpClient) {}

  /**
   * Registra un nuevo usuario siguiendo UserRegistrationDTO
   */
  register(username: string, email: string, password: string): Observable<any> {
    // Solo enviamos los campos que espera el UserRegistrationDTO de Java
    const data = { username, email, password };
    return this.http.post(`${this.authUrl}register`, data, { withCredentials: true });
  }

  /**
   * Login siguiendo LoginRequestDTO (usa username y password)
   */
  login(username: string, password: string): Observable<any> {
  const data = { username, password };
  return this.http.post<any>(`${this.authUrl}login`, data, { withCredentials: true }).pipe(
    tap(response => {
      if (response.code) {
        // Guardamos el código para que persista al recargar
        localStorage.setItem('userCode', response.code);
        localStorage.setItem('username', username);
      }
    })
  );
  }

  // Método para recuperar el código fácilmente
  getUserCode(): string | null {
    return localStorage.getItem('userCode');
  }

  getUsername(): string | null {
    return localStorage.getItem('username');
  }

  /**
   * Logout (Spring Security manejará la limpieza de la cookie)
   */
  logout(): Observable<any> {
    return this.http.post(`${this.authUrl}logout`, {}, { withCredentials: true });
  }
}