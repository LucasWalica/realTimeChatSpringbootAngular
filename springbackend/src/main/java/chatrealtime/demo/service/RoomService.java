package chatrealtime.demo.service;

import chatrealtime.demo.dto.RoomDTO;
import chatrealtime.demo.model.Message;
import chatrealtime.demo.model.Room;
import chatrealtime.demo.model.User;
import chatrealtime.demo.repository.RoomRepository;
import chatrealtime.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoomService {
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;

    public List<RoomDTO> findRoomsDTOByUser(Long userId){
        List<Room> rooms = roomRepository.findByMembers_id(userId);
        boolean hasBotRoom = rooms.stream().anyMatch(Room::isBotRoom);
        if(!hasBotRoom){
            Room botRoom = createBotRoomForUser(userId);
            rooms.add(botRoom);
        }
        return rooms.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private RoomDTO convertToDTO(Room room) {

        String lastContent = "No hay mensajes aún";

        if (room.getMessages() != null && !room.getMessages().isEmpty()) {
            lastContent = room.getMessages().stream().max(Comparator.comparing(Message::getTimestamp))
                    .map(Message::getContent)
                    .orElse("No hay mensajes aún");
        }

        return RoomDTO.builder()
                .id(room.getId())
                .name(room.isBotRoom() ? "AI Buddy" : room.getName())
                .isBotRoom(room.isBotRoom())
                .memberNames(room.getMembers().stream()
                        .map(User::getUsername)
                        .collect(Collectors.toSet()))
                .lastMessage(lastContent)
                .build();
    }

    private Room createBotRoomForUser(Long userId) {
        User user = userRepository.findById(userId).orElseThrow();
        Room room = new Room();
        room.setName("AI Buddy");
        room.setBotRoom(true);
        room.setMembers(Set.of(user));
        return roomRepository.save(room);
    }

    public Room createGroup(String groupName, List<Long> participantsIds){
        List<User> users = userRepository.findAllById(participantsIds);

        Room group = Room.builder()
                .name(groupName)
                .isGroup(true)
                .members(new HashSet<>(users))
                .build();
        return roomRepository.save(group);
    }

    public RoomDTO getOrCreatePrivateChat(Long user1Id, Long user2Id){
        List<Room> userRooms = roomRepository.findByMembers_id(user1Id);

        for (Room room : userRooms){
            if(!room.isGroup()){
                boolean containsUser2 = room.getMembers().stream()
                        .anyMatch(u->u.getId().equals(user2Id));
                if (containsUser2) return convertToDTO(room);
            }
        }


        User u1 = userRepository.findById(user1Id).orElseThrow();
        User u2 = userRepository.findById(user2Id).orElseThrow();
        Room newRoom = Room.builder()
                .isGroup(false)
                .members(Set.of(u1, u2))
                .build();

        roomRepository.save(newRoom);
        return convertToDTO(newRoom);
    }

    public boolean isUserMemberOfRoom(Long userId, Long roomId){
        Room room= roomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Sala no encontrada"));
        return room.getMembers().stream()
                .anyMatch(member -> member.getId().equals(userId));
    }

    public Room findRoomById(Long id){
        return roomRepository.findRoomById(id);
    }
}
