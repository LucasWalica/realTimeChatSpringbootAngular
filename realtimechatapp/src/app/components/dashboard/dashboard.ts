import { Component, signal, inject, OnInit } from '@angular/core'; // Añade inject y OnInit
import { ChatSocketService } from '../../services/chatsocketservice'; // Importa tu servicio
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { ChatList } from '../chat-list/chat-list';
import { ChatDetail } from '../chat-detail/chat-detail';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, ChatList, ChatDetail],
  templateUrl: './dashboard.html'
})
export class DashboardComponent implements OnInit {
  selectedRoomId = signal<number | null>(null);
  
  // Inyectamos el servicio de Socket
  private socketService = inject(ChatSocketService);
  private router = inject(Router);

  ngOnInit() {
    console.log('🚀 Dashboard inicializado. Conectando WebSocket...');
    // Llamamos a la conexión. Como usas cookies, no necesitas pasar el token aquí
    this.socketService.connect();
  }

  handleChatSelection(id: number) {
    console.log('Cambiando al chat con ID:', id);
    this.selectedRoomId.set(id);
  }

  handleLogout() {
    this.socketService.disconnect(); // Es buena práctica desconectar al salir
    this.router.navigate(['/login']);
  }
}