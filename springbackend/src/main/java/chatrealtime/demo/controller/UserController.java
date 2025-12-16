package chatrealtime.demo.controller;

import chatrealtime.demo.dto.UserResponseDTO;
import chatrealtime.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/search")
    public ResponseEntity<List<UserResponseDTO>> search(@RequestParam String username, @AuthenticationPrincipal UserDetails userDetails){
        String currentUser = userDetails.getUsername();

        return ResponseEntity.ok(
                userService.searchUsers(username).stream()
                        .filter(user -> !user.getUsername().equals(currentUser))
                        .map(user -> {
                            UserResponseDTO dto = new UserResponseDTO();
                            dto.setId(user.getId());
                            dto.setUsername(user.getUsername());
                            dto.setOnline(user.isOnline());
                            return dto;
                        }).collect(Collectors.toList())
        );
    }
}
