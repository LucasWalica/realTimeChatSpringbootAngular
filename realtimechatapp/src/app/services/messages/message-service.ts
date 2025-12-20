import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { apiurl } from '../../environ/env.api';


@Injectable({
  providedIn: 'root',
})
export class MessageService {
  constructor(private http: HttpClient) {}

  // Consume RoomController.getMyRooms
  getRooms() {
    return this.http.get<any[]>(`${apiurl}api/rooms`, { withCredentials: true });
  }

  // Consume MessageController.getHistory
  getMessageHistory(roomId: number) {
    return this.http.get<any[]>(`${apiurl}api/messages/${roomId}`, { withCredentials: true });
  }

  // Consume UserController.search para el modal de "Nueva Conversación"
  searchUsers(query: string) {
    return this.http.get<any[]>(`${apiurl}api/users/search?username=${query}`, { withCredentials: true });
  }
}
