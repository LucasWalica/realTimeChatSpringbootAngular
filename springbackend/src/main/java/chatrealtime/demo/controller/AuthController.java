package chatrealtime.demo.controller;

import chatrealtime.demo.dto.LoginRequestDTO;
import chatrealtime.demo.dto.UserRegistrationDTO;
import chatrealtime.demo.dto.UserResponseDTO;
import chatrealtime.demo.model.User;
import chatrealtime.demo.security.JwtService;
import chatrealtime.demo.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> register(@RequestBody UserRegistrationDTO registrationDTO){
        User user = User.builder()
                .username(registrationDTO.getUsername())
                .password(registrationDTO.getPassword())
                .email(registrationDTO.getEmail())
                .build();

        User savedUser = userService.register(user);

        UserResponseDTO response = new UserResponseDTO();

        response.setId(savedUser.getId());
        response.setUsername(savedUser.getUsername());
        response.setEmail(savedUser.getEmail());
        response.setOnline(savedUser.isOnline());

        return ResponseEntity.ok(response);
    };

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO loginDto, HttpServletResponse response){
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword())
        );
        String token = jwtService.generateToken(loginDto.getUsername());
        Cookie cookie = new Cookie("jwt", token);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(24*60*60);
        response.addCookie(cookie);
        return ResponseEntity.ok(Map.of("message", "Login exitoso"));
    }
}
