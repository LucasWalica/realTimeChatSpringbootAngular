package chatrealtime.demo.service;

import chatrealtime.demo.model.User;
import chatrealtime.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User register(User user){
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setOnline(false);
        return  userRepository.save(user);
    }
    public void updateOnlineStatus(String username, boolean isOnline){
        userRepository.findByUsername(username).ifPresent(user -> {
            user.setOnline(isOnline);
            userRepository.save(user);
        });
    }
    public List<User> searchUsers(String query){
        return  userRepository.findAll().stream()
                .filter(u->u.getUsername().contains(query))
                .toList();
    }

    public Optional<User> findByUsername(String username){
        return userRepository.findByUsername(username);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
    }
}
