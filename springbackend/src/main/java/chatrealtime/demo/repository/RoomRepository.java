package chatrealtime.demo.repository;

import chatrealtime.demo.model.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface RoomRepository extends JpaRepository<Room, Long> {
    List<Room> findByMembers_id(Long userId);
    boolean existsByIdAndMembers_Id(Long roomId, Long userId);
    Room findRoomById(Long roomId);
    @Query("SELECT r FROM Room r JOIN r.members m1 JOIN r.members m2 " +
            "WHERE r.isGroup = false " +
            "AND m1.id = :id1 AND m2.id = :id2")
    Optional<Room> findPrivateRoomBetweenUsers(Long id1, Long id2);
}
