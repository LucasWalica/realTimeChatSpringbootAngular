package chatrealtime.demo.config;

import chatrealtime.demo.model.Room;
import chatrealtime.demo.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final RoomRepository chatRoomRepository;

    @Override
    public void run(String... args) {
        // En lugar de forzar el ID 0, miramos si hay alguna sala
        if (chatRoomRepository.count() == 0) {
            Room defaultRoom = new Room();
            defaultRoom.setName("Sala General");
            // NO ponemos setId(0L), dejamos que la DB decida el ID

            Room savedRoom = chatRoomRepository.save(defaultRoom);

            System.out.println("-----------------------------------------");
            System.out.println("✅ SALA CREADA AUTOMÁTICAMENTE");
            System.out.println("👉 El ID de la sala es: " + savedRoom.getId());
            System.out.println("-----------------------------------------");
        } else {
            List<Room> rooms = chatRoomRepository.findAll();
            System.out.println("ℹ️ Salas disponibles: " + rooms.size());
            rooms.forEach(r -> System.out.println("ID: " + r.getId() + " - " + r.getName()));
        }
    }
}