package chatrealtime.demo.repository;

import chatrealtime.demo.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findByroomIdOrderByTimestampAsc(Long roomId);
}
