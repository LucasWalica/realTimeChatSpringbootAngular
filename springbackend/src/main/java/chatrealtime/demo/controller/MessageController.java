package chatrealtime.demo.controller;

import chatrealtime.demo.model.Message;
import chatrealtime.demo.model.User;
import chatrealtime.demo.service.MessageService;
import chatrealtime.demo.service.RoomService;
import chatrealtime.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageController {
    private final MessageService messageService;
    private final RoomService roomService;
    private  final UserService userService;

    @GetMapping("/{roomId}")
    public ResponseEntity<List<Message>> getHistory(
            @PathVariable Long roomId,
            @AuthenticationPrincipal UserDetails userDetails){
        User user = userService.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("usuario no encontrado"));
        if (!roomService.isUserMemberOfRoom(user.getId(), roomId)){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return  ResponseEntity.ok(messageService.getMessagesByRoom(roomId));
    }
}
