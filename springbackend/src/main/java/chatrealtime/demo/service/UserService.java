package chatrealtime.demo.service;

import chatrealtime.demo.model.Room;
import chatrealtime.demo.model.User;
import chatrealtime.demo.repository.RoomRepository;
import chatrealtime.demo.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoomRepository roomRepository;

    public User register(User user){
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setOnline(false);
        String code = generateUniqueUserCode(user.getUsername());
        user.setUserCode(code);
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

    public  Optional<User> findByUserCode(String code){
        return userRepository.findByUserCode(code);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
    }

    public String generateUniqueUserCode(String username) {
        // Tomamos las primeras 3 letras del nombre y 4 números aleatorios
        String prefix = (username.length() >= 3) ? username.substring(0, 3).toUpperCase() : "USR";
        String randomSuffix = String.format("%04d", new Random().nextInt(10000));
        String code = prefix + "-" + randomSuffix;

        // Verificamos que no exista ya en la base de datos (recursivo)
        if (userRepository.existsByUserCode(code)) {
            return generateUniqueUserCode(username);
        }
        return code;
    }

    /**
     * Usamos @Transactional porque este método realiza varias consultas
     * y una inserción. Si la inserción falla, queremos que la transacción se limpie.
     */
    @Transactional
    public Room createChatByCode(User currentUser, String inviteCode) {
        // 1. Buscar al destinatario
        User targetUser = userRepository.findByUserCode(inviteCode)
                .orElseThrow(() -> new EntityNotFoundException("Código de invitación no válido"));

        // 2. Evitar chats con uno mismo
        if (currentUser.getId().equals(targetUser.getId())) {
            throw new IllegalArgumentException("No puedes invitarte a ti mismo");
        }

        // 3. Lógica para no duplicar salas (opcional pero recomendada)
        // ... (aquí iría la búsqueda de sala existente)
        Optional<Room> existingRoom = roomRepository.findPrivateRoomBetweenUsers(currentUser.getId(), targetUser.getId());

        if (existingRoom.isPresent()) {
            return existingRoom.get(); // Devolvemos la que ya existe para que el front la abra
        }

        // 4. Crear y guardar la nueva sala
        Room newRoom = Room.builder()
                .members(Set.of(currentUser, targetUser))
                .build();

        return roomRepository.save(newRoom);
    }
}
