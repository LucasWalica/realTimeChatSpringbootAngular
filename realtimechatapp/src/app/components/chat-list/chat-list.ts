import { Component, signal, output, input, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Chat } from '../../models/Chat.models';

@Component({
  selector: 'app-chat-list',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './chat-list.html'
})
export class ChatList {
  // Inputs/Outputs con Signals
  selectedChatId = input<number | null>(null); // Cambiado a number (Long ID)
  selectChat = output<number>();
  logout = output<void>();

  searchQuery = signal('');

  // Signal que contendrá los datos del backend
  rooms = signal<Chat[]>([
    {
      id: 1,
      name: "Sarah Johnson",
      isGroup: false,
      avatar: "https://avatar.iran.liara.run/public/31",
      lastMessage: {
        content: "Hey! Are we still meeting tomorrow?",
        timestamp: new Date().toISOString()
      },
      unreadCount: 3,
      online: true,
    }
  ]);

  // Filtro de búsqueda computado (Reactividad pura de Angular 18+)
  filteredChats = computed(() => {
    const query = this.searchQuery().toLowerCase();
    return this.rooms().filter(chat => 
      chat.name.toLowerCase().includes(query)
    );
  });

  onSelectChat(id: number) {
    this.selectChat.emit(id);
  }

  onLogout() {
    this.logout.emit();
  }

  openNewConversation() {
    // Aquí podrías usar un prompt para el 'Invite Code' que valide contra tu backend
    const inviteCode = prompt('Enter room invite code:');
    if (inviteCode) console.log('Joining room...', inviteCode);
  }
}