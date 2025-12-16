package chatrealtime.demo.service;

import chatrealtime.demo.dto.MessageRequestDTO;
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
    public Message saveMessage(Long roomId, MessageRequestDTO dto){
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Sala no encontrada"));
        User sender = userRepository.findById(dto.getSenderId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Message message = Message.builder()
                .content(dto.getContent())
                .sender(sender)
                .room(room)
                .timestamp(LocalDateTime.now())
                .build();
        return messageRepository.save(message);
    }

    public List<Message> getMessagesByRoom(Long roomId){
        return messageRepository.findByroomIdOrderByTimestampAsc(roomId);
    }
}
