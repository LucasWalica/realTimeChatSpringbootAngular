// Basado en Room.java y User.java
export interface Chat {
  id: number;          // Long en Java -> number en TS
  name: string;        // Room.name
  isGroup: boolean;    // Room.isGroup
  avatar?: string;     // Calculado o desde el miembro del Room
  lastMessage?: {      // Basado en Message.java
    content: string;
    timestamp: string;
  };
  unreadCount: number; // Esto suele ser una lógica de negocio o DTO
  online?: boolean;    // User.online
}