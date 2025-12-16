package chatrealtime.demo.service;

import chatrealtime.demo.model.Room;
import chatrealtime.demo.model.User;
import chatrealtime.demo.repository.RoomRepository;
import chatrealtime.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class RoomService {
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;

    public List<Room> findRoomsByUser(Long userId){
        return roomRepository.findByMembers_id(userId);
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

    public Room getOrCreatePrivateChat(Long user1Id, Long user2Id){
        List<Room> userRooms = roomRepository.findByMembers_id(user1Id);

        for (Room room : userRooms){
            if(!room.isGroup()){
                boolean containsUser2 = room.getMembers().stream()
                        .anyMatch(u->u.getId().equals(user2Id));
                if (containsUser2) return room;
            }
        }


        User u1 = userRepository.findById(user1Id).orElseThrow();
        User u2 = userRepository.findById(user2Id).orElseThrow();
        Room newRoom = Room.builder()
                .isGroup(false)
                .members(Set.of(u1, u2))
                .build();

        return roomRepository.save(newRoom);
    }

    public boolean isUserMemberOfRoom(Long userId, Long roomId){
        Room room= roomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Sala no encontrada"));
        return room.getMembers().stream()
                .anyMatch(member -> member.getId().equals(userId));
    }
}
