import { Component, signal, output, input, computed, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MessageService } from '../../services/messages/message-service';
import { ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import { Auth } from '../../services/auth-service';
@Component({
  selector: 'app-chat-list',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule],
  templateUrl: './chat-list.html'
})
export class ChatList implements OnInit {

  currentUserCode = signal<string | null>(null);
  currentUsername = signal<string | null>(null);

  private fb = inject(FormBuilder);
  private authService = inject(Auth);

  inviteForm = this.fb.group({
    inviteCode: ['', [Validators.required, Validators.minLength(4)]]
  });

  showInviteModal = signal(false);

  closeModal() {
    this.showInviteModal.set(false);
    this.inviteForm.reset();
  }
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
    this.currentUserCode.set(this.authService.getUserCode());
    this.currentUsername.set(this.authService.getUsername());
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

  copyCode() {
    const code = this.currentUserCode();
    if (code) {
      navigator.clipboard.writeText(code);
      // Opcional: podrías usar un toast aquí
    }
  }

  onSelectChat(id: number) {
    this.selectChat.emit(id);
  }

  onLogout() {
    this.logout.emit();
  }

  openNewConversation(){
    this.showInviteModal.set(true);
  }

  submitInvite() {
    if (this.inviteForm.valid) {
      const code = this.inviteForm.value.inviteCode!;
      this.messageService.sendInviteCode(code).subscribe({
        next: (newRoom) => {
          // Si la sala ya existe o es nueva, la añadimos a la lista si no está
          this.rooms.update(prev => {
            const exists = prev.find(r => r.id === newRoom.id);
            return exists ? prev : [newRoom, ...prev];
          });
          this.onSelectChat(newRoom.id);
          this.closeModal();
        },
        error: (err) => alert('Error: ' + (err.error || 'Código no válido'))
      });
    }
  }
}