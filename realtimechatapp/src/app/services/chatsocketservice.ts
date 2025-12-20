import { Injectable, signal } from '@angular/core';
import { Client, IMessage } from '@stomp/stompjs';
import { apiurl, socketurl } from '../environ/env.api';
import SockJS from 'sockjs-client';

@Injectable({
  providedIn: 'root'
})
export class ChatSocketService {
  private stompClient: Client | null = null;
  
  // Signal para saber si estamos conectados
  public isConnected = signal<boolean>(false);
  
  // Signal para recibir el último mensaje llegado de cualquier sala
  public lastMessageReceived = signal<any>(null);

  constructor() { }

  /**
   * Conecta al WebSocket usando el token para la autenticación
   */
  connect() {
  // Asegúrate de que la URL NO termine en / si luego sumas /ws-chat
  const baseUrl = apiurl.endsWith('/') ? apiurl.slice(0, -1) : apiurl;
  const url = `${baseUrl}/ws-chat`;
  
  console.log('🚀 Iniciando proceso de conexión a:', url);

  this.stompClient = new Client({
    webSocketFactory: () => {
      console.log('📡 Creando instancia de SockJS con credenciales...');
      return new SockJS(url, null, { withCredentials: true } as any);
    },
    debug: (str) => {
      console.log('🛠️ STOMP LÍNEA A LÍNEA:', str);
    },
    reconnectDelay: 5000,
  });

  this.stompClient.onConnect = (frame) => {
    console.log('✅ ¡CONECTADO EXITOSAMENTE AL SOCKET!');
    this.isConnected.set(true);
  };

  this.stompClient.onWebSocketClose = (event) => {
    console.warn('🔌 El WebSocket se ha cerrado:', event);
  };

  this.stompClient.activate();
  console.log('⚡ stompClient.activate() ha sido llamado');
}

  private currentSubscription: any = null;

  subscribeToRoom(roomId: number) {
    if (!this.stompClient || !this.isConnected()) return;

    // 1. Limpiar suscripción anterior si existe
    if (this.currentSubscription) {
      this.currentSubscription.unsubscribe();
    }

    console.log(`Subscribing to room: ${roomId}`);
    
    // 2. Guardar la nueva suscripción
    this.currentSubscription = this.stompClient.subscribe(`/topic/room/${roomId}`, (message) => {
      const msgData = JSON.parse(message.body);
      console.log('📩 Mensaje recibido del socket:', msgData);
      this.lastMessageReceived.set(msgData);
    });
  }

  sendMessage(roomId: number, content: string) {
    if (!this.stompClient || !this.isConnected()) return;

    // AJUSTE: Enviamos el string plano (Payload) a /app/chat/{id}
    this.stompClient.publish({
      destination: `/app/chat/${roomId}`,
      body: content // Tu backend recibe @Payload String content
    });
  }

  disconnect() {
    if (this.stompClient) {
      this.stompClient.deactivate();
      this.isConnected.set(false);
    }
  }
}