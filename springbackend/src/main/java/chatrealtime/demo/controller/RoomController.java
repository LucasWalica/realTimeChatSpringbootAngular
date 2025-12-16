package chatrealtime.demo.controller;

import chatrealtime.demo.model.Room;
import chatrealtime.demo.model.User;
import chatrealtime.demo.service.RoomService;
import chatrealtime.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rooms")
@RequiredArgsConstructor
public class RoomController {

    private final RoomService roomService;
    private final UserService userService;
    @GetMapping
    public ResponseEntity<List<Room>> getMyRooms(@AuthenticationPrincipal UserDetails userDetails){
        User user = userService.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado."));

        return ResponseEntity.ok(roomService.findRoomsByUser(user.getId()));
    }

    @PostMapping("private/{targetUserId}")
    public ResponseEntity<Room> createPrivateChat(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long targetUserId) {
        return ResponseEntity.ok(
                roomService.getOrCreatePrivateChat(userService.findByUsername(userDetails.getUsername()).orElseThrow().getId(), targetUserId)
        );
    }
}
