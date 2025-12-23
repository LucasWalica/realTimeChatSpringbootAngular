package chatrealtime.demo.controller;

import chatrealtime.demo.dto.InviteRequest;
import chatrealtime.demo.dto.RoomDTO;
import chatrealtime.demo.model.Room;
import chatrealtime.demo.model.User;
import chatrealtime.demo.service.RoomService;
import chatrealtime.demo.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/rooms")
@RequiredArgsConstructor
public class RoomController {

    private final RoomService roomService;
    private final UserService userService;
    @GetMapping
    public ResponseEntity<List<RoomDTO>> getMyRooms(@AuthenticationPrincipal UserDetails userDetails){
        User user = userService.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado."));

        return ResponseEntity.ok(roomService.findRoomsDTOByUser(user.getId()));
    }

    @PostMapping("private/{targetUserId}")
    public ResponseEntity<RoomDTO> createPrivateChat(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long targetUserId) {
        return ResponseEntity.ok(
                roomService.getOrCreatePrivateChat(userService.findByUsername(userDetails.getUsername()).orElseThrow().getId(), targetUserId)
        );
    }

    @PostMapping("/invite")
    public ResponseEntity<?> createPrivateChat(
            @RequestBody InviteRequest request,
            Principal principal) {

        try {
            // 1. Obtenemos el usuario autenticado desde el contexto de seguridad
            User currentUser = userService.findByUsername(principal.getName())
                    .orElseThrow();

            User targetUser = userService.findByUserCode(request.inviteCode())
                    .orElseThrow();

            // 2. Llamamos al servicio con la lógica que escribimos antes
            RoomDTO room = roomService.getOrCreatePrivateChat(currentUser.getId(), targetUser.getId());

            // 3. Devolvemos la sala (ya sea la nueva o la que ya existía)
            return ResponseEntity.ok(room);

        } catch (EntityNotFoundException e) {
            // Código no encontrado
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            // Intentó invitarse a sí mismo
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            // Error genérico
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Ocurrió un error al procesar la invitación");
        }
    }
}
