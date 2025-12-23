import { Component, ElementRef, ViewChild, AfterViewChecked, signal, input, inject, effect } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MessageService } from '../../services/messages/message-service';
import { ChatSocketService } from '../../services/chatsocketservice';
import { Auth } from '../../services/auth-service'; // Importa tu servicio de Auth

@Component({
  selector: 'app-chat-detail',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './chat-detail.html'
})
export class ChatDetail implements AfterViewChecked {
  chatId = input<number | null>(null);
  chatName = input<string | null>(null); // Añade esto para mostrar el nombre dinámico
  isBotRoom = input<boolean>(false);     // Añade esto para saber si poner icono de bot en el header

  @ViewChild('scrollContainer') private scrollContainer!: ElementRef;

  private messageService = inject(MessageService);
  private socketService = inject(ChatSocketService);
  private auth = inject(Auth); // Inyectamos auth para saber quién soy yo

  messageText = signal('');
  messages = signal<any[]>([]);

  constructor() {
    effect(() => {
      const id = this.chatId();
      if (id !== null) {
        this.loadHistory(id);
        this.socketService.subscribeToRoom(id);
      }
    }, { allowSignalWrites: true });

    effect(() => {
      const newMsg = this.socketService.lastMessageReceived();
      if (newMsg) {
        // Mapeamos el DTO de Java al formato que espera el HTML
        const formattedMsg = this.mapMessage(newMsg);
        this.messages.update(m => [...m, formattedMsg]);
      }
    }, { allowSignalWrites: true });
  }

  // Función para transformar el mensaje del Backend al formato del Front
  private mapMessage(msg: any) {
    const myUsername = this.auth.getUsername();
    // this.auth.getUserName(); // Obtén el nombre del usuario logueado
    
    let senderType: 'me' | 'bot' | 'other' = 'other';
    
    if (msg.type === 'BOT') {
      senderType = 'bot';
    } else if (msg.senderName === myUsername) {
      senderType = 'me';
    }

    return {
      id: msg.id,
      sender: senderType,
      senderName: msg.senderName,
      text: msg.content, // Java envía 'content', el HTML usa 'text'
      time: msg.timestamp ? new Date(msg.timestamp).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }) : '',
      type: msg.type
    };
  }

  private loadHistory(id: number) {
    this.messageService.getMessageHistory(id).subscribe({
      next: (history) => {
        const formattedHistory = history.map((m: any) => this.mapMessage(m));
        this.messages.set(formattedHistory);
      }
    });
  }

  ngAfterViewChecked() { this.scrollToBottom(); }
  private scrollToBottom(): void {
    if (this.scrollContainer) {
      this.scrollContainer.nativeElement.scrollTop = this.scrollContainer.nativeElement.scrollHeight;
    }
  }

  handleSend() {
    console.log('Intentando enviar mensaje a la sala:', this.chatId());
  console.log('Contenido:', this.messageText());
    const text = this.messageText().trim();
    const roomId = this.chatId();
    if (!text || roomId === null) return;
    this.socketService.sendMessage(roomId, text);
    this.messageText.set('');
  }
}