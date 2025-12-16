package chatrealtime.demo.repository;

import chatrealtime.demo.model.Invitation;
import chatrealtime.demo.model.InvitationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InvitationRepository extends JpaRepository<Invitation, Long> {
    List<Invitation> FindByReceiberAndStatus(Long reveiverId, InvitationStatus status);
}
