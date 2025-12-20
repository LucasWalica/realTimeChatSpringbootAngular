package chatrealtime.demo.repository;

import chatrealtime.demo.model.Room;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoomRepository extends JpaRepository<Room, Long> {
    List<Room> findByMembers_id(Long userId);
    boolean existsByIdAndMembers_Id(Long roomId, Long userId);
    Room findRoomById(Long roomId);

}
