import { Injectable, signal } from '@angular/core';
import { Client } from '@stomp/stompjs';
import { apiurl } from '../environ/env.api';
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

  private currentSubscription: any = null;

  constructor() { }

  /**
   * Extrae una cookie específica por su nombre
   */
  private getCookie(name: string): string | null {
    const nameLenPlus = (name.length + 1);
    return document.cookie
      .split(';')
      .map(c => c.trim())
      .filter(cookie => {
        return cookie.substring(0, nameLenPlus) === (name + '=');
      })
      .map(cookie => {
        return decodeURIComponent(cookie.substring(nameLenPlus));
      })[0] || null;
  }

  /**
   * Conecta al WebSocket enviando el JWT en los headers de STOMP
   */
  connect() {
    const baseUrl = apiurl.endsWith('/') ? apiurl.slice(0, -1) : apiurl;
    const url = `${baseUrl}/ws-chat`;
    
    // Extraemos el token de la cookie 'jwt'
    const token = this.getCookie('jwt');
    
    console.log('🚀 Iniciando proceso de conexión a:', url);
    if (!token) {
      console.warn('⚠️ No se encontró la cookie "jwt". Es posible que el servidor rechace la conexión.');
    }

    this.stompClient = new Client({
      webSocketFactory: () => {
        console.log('📡 Creando instancia de SockJS con credenciales...');
        return new SockJS(url, null, { withCredentials: true } as any);
      },
      // IMPORTANTÍSIMO: Enviamos el token en el frame de conexión
      connectHeaders: {
        Authorization: token ? `Bearer ${token}` : ''
      },
      debug: (str) => {
        console.log('🛠️ STOMP LÍNEA A LÍNEA:', str);
      },
      reconnectDelay: 5000,
      heartbeatIncoming: 4000,
      heartbeatOutgoing: 4000
    });

    this.stompClient.onConnect = (frame) => {
      console.log('✅ ¡CONECTADO EXITOSAMENTE AL SOCKET!');
      this.isConnected.set(true);
    };

    this.stompClient.onStompError = (frame) => {
      console.error('❌ Error de STOMP:', frame.headers['message']);
      console.error('Detalles:', frame.body);
    };

    this.stompClient.onWebSocketClose = (event) => {
      console.warn('🔌 El WebSocket se ha cerrado:', event);
      this.isConnected.set(false);
    };

    this.stompClient.activate();
  }

  subscribeToRoom(roomId: number) {
    // Si no está conectado, esperamos un poco o reintentamos
    if (!this.stompClient || !this.isConnected()) {
      console.warn('⏳ Intentando suscribir sin conexión... reintentando en 1s');
      setTimeout(() => this.subscribeToRoom(roomId), 1000);
      return;
    }

    if (this.currentSubscription) {
      this.currentSubscription.unsubscribe();
    }

    console.log(`Subscribing to room: ${roomId}`);
    
    this.currentSubscription = this.stompClient.subscribe(`/topic/room/${roomId}`, (message) => {
      const msgData = JSON.parse(message.body);
      console.log('📩 Mensaje recibido del socket:', msgData);
      this.lastMessageReceived.set(msgData);
    });
  }

  sendMessage(roomId: number, content: string) {
    if (!this.stompClient || !this.isConnected()) {
      console.error('❌ No se puede enviar mensaje: No hay conexión activa.');
      return;
    }

    this.stompClient.publish({
      destination: `/app/chat/${roomId}`,
      // Enviamos el objeto que espera el Record MessageRequest en Java
      body: JSON.stringify({ content: content }) 
    });
  }

  disconnect() {
    if (this.stompClient) {
      this.stompClient.deactivate();
      this.isConnected.set(false);
      console.log('🔌 WebSocket desactivado manualmente.');
    }
  }
}