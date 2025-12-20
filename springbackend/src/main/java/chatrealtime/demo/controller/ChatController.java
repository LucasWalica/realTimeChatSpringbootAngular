package chatrealtime.demo.controller;

import chatrealtime.demo.dto.ChatMessageDTO;
import chatrealtime.demo.model.Message;
import chatrealtime.demo.model.Room;
import chatrealtime.demo.service.AIService;
import chatrealtime.demo.service.MessageService;
import chatrealtime.demo.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class ChatController {
    private final SimpMessagingTemplate messagingTemplate;
    private final MessageService messageService;
    private final AIService aiService;
    private final RoomService roomService;

    @MessageMapping("/chat/{roomId}")
    public void handleMessage(@DestinationVariable Long roomId,
                              @Payload String content,
                              Principal principal) {

        String username = (principal != null) ? principal.getName() : "Usuario";

        // 1. Procesar y enviar el mensaje del usuario
        ChatMessageDTO userDto = buildDto(content, username, "USER");
        messageService.saveMessage(roomId, userDto);
        messagingTemplate.convertAndSend("/topic/room/" + roomId, userDto);

        // 2. Verificar si la sala es privada del bot o si mencionaron al bot
        // Obtenemos la sala para ver si tiene activado el flag isBotRoom
        Room room = roomService.findRoomById(roomId);
        boolean isBotRoom = room != null && room.isBotRoom();
        boolean botMentioned = content.toLowerCase().contains("@bot");

        if (isBotRoom || botMentioned) {
            // Limpiamos la mención para que la IA no se confunda
            String aiPrompt = content.replace("@bot", "").trim();

            aiService.generateResponse(aiPrompt).thenAccept(aiReply -> {
                ChatMessageDTO botDto = buildDto(aiReply, "AI Buddy", "BOT");

                // Guardamos la respuesta de la IA en la DB y la enviamos por Socket
                messageService.saveMessage(roomId, botDto);
                messagingTemplate.convertAndSend("/topic/room/" + roomId, botDto);
            });
        }
    }

    private ChatMessageDTO buildDto(String content, String sender, String type) {
        return ChatMessageDTO.builder()
                .content(content)
                .senderName(sender)
                .timestamp(LocalDateTime.now().toString())
                .type(type)
                .build();
    }
}
