package chatrealtime.demo.repository;

import chatrealtime.demo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    List<User> findByOnlineTrue();
    boolean existsByUserCode(String code);
    Optional<User> findByUserCode(String code);
}
