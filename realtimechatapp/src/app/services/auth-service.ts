import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
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
    // Cambiado 'email' por 'username' para coincidir con LoginRequestDTO.java
    const data = { username, password };
    return this.http.post(`${this.authUrl}login`, data, { withCredentials: true });
  }

  /**
   * Logout (Spring Security manejará la limpieza de la cookie)
   */
  logout(): Observable<any> {
    return this.http.post(`${this.authUrl}logout`, {}, { withCredentials: true });
  }
}