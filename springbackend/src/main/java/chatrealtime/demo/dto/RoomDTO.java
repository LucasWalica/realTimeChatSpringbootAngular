package chatrealtime.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RoomDTO {
    private Long id;
    private String name;
    private boolean isBotRoom;
    // Enviamos solo nombres o info básica de miembros, no el objeto User entero
    private Set<String> memberNames;
    private String lastMessage; // Opcional: para mostrar debajo del nombre en la lista
}