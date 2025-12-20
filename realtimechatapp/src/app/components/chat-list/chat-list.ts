import { Component, signal, output, input, computed, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MessageService } from '../../services/messages/message-service';

@Component({
  selector: 'app-chat-list',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './chat-list.html'
})
export class ChatList implements OnInit {
  // Inyectamos tu MessageService
  private messageService = inject(MessageService);

  // Inputs y Outputs
  selectedChatId = input<number | null>(null);
  selectChat = output<number>();
  logout = output<void>();

  searchQuery = signal('');
  
  // Aquí guardaremos las salas reales que vienen de RoomController
  rooms = signal<any[]>([]);

  // Filtro inteligente: busca por nombre de sala
  filteredChats = computed(() => {
    const query = this.searchQuery().toLowerCase().trim();
    if (!query) return this.rooms();
    
    return this.rooms().filter(room => 
      room.name?.toLowerCase().includes(query)
    );
  });

  ngOnInit() {
    this.fetchRooms();
  }

  fetchRooms() {
    // Llamamos al endpoint @GetMapping de RoomController
    this.messageService.getRooms().subscribe({
      next: (data) => {
        this.rooms.set(data);
      },
      error: (err) => {
        console.error('Error al obtener las salas:', err);
      }
    });
  }

  onSelectChat(id: number) {
    this.selectChat.emit(id);
  }

  onLogout() {
    this.logout.emit();
  }

  openNewConversation() {
    // Aquí puedes abrir un pequeño modal o usar un prompt 
    // para buscar usuarios mediante el UserController
    const targetUser = prompt('Nombre del usuario para chatear:');
    if (targetUser) {
      console.log('Iniciando búsqueda de usuario:', targetUser);
    }
  }
}