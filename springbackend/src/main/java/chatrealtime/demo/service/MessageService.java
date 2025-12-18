package chatrealtime.demo.service;

import chatrealtime.demo.dto.ChatMessageDTO;
import chatrealtime.demo.model.Message;
import chatrealtime.demo.model.Room;
import chatrealtime.demo.model.User;
import chatrealtime.demo.repository.MessageRepository;
import chatrealtime.demo.repository.RoomRepository;
import chatrealtime.demo.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageService {
    private final MessageRepository messageRepository;
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;

    @Transactional
    public Message saveMessage(Long roomId, ChatMessageDTO dto) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Sala no encontrada"));

        // Si el remitente es la IA, podemos buscar un usuario "sistema" o manejarlo
        User sender = userRepository.findByUsername(dto.getSenderName())
                .orElse(null); // Permitimos null si es un bot, o maneja un usuario bot pre-creado

        Message message = Message.builder()
                .content(dto.getContent())
                .sender(sender)
                .room(room)
                .type(dto.getType())
                .timestamp(LocalDateTime.now())
                .build();

        return messageRepository.save(message);
    }

    public List<Message> getMessagesByRoom(Long roomId){
        return messageRepository.findByroomIdOrderByTimestampAsc(roomId);
    }
}
