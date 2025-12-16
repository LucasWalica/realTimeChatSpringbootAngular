package chatrealtime.demo.service;

import chatrealtime.demo.model.Message;
import chatrealtime.demo.model.Room;
import chatrealtime.demo.model.User;
import chatrealtime.demo.repository.MessageRepository;
import chatrealtime.demo.repository.RoomRepository;
import chatrealtime.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final MessageRepository messageRepository;
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;


    public Message sendMessage(Long senderId, Long roomId, String content){
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new RuntimeException(("usuario no encontrado")));
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("sala no encontrada"));

        Message message = Message.builder()
                .content(content)
                .sender(sender)
                .room(room)
                .timestamp(LocalDateTime.now())
                .build();
        return messageRepository.save(message);
    };

    public List<Message> getMessagesByRoom(Long roomId){
        return messageRepository.findByroomIdOrderByTimestampAsc(roomId);
    }
}


