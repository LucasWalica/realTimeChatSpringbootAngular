package chatrealtime.demo.config;
import chatrealtime.demo.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSocketMessageBroker
@Order(Ordered.HIGHEST_PRECEDENCE + 99)
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic");
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws-chat")
                .setAllowedOriginPatterns("*");
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

                if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
                    String token = extractToken(accessor);

                    if (token != null) {
                        try {
                            String username = jwtService.extractUsername(token);
                            if (username != null && jwtService.isTokenValid(token, username)) {
                                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                                UsernamePasswordAuthenticationToken authentication =
                                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                                accessor.setUser(authentication);
                                System.out.println("✅ Usuario autenticado en WebSocket: " + username);
                            }
                        } catch (Exception e) {
                            System.err.println("❌ Error validando Token en WebSocket: " + e.getMessage());
                            // No dejamos pasar si el token está mal
                            return null;
                        }
                    } else {
                        System.err.println("⚠️ Intento de conexión WebSocket sin Token");
                        // Para desarrollo puedes dejarlo pasar o retornar null para bloquear
                        // return null;
                    }
                }
                return message;
            }
        });
    }

    private String extractToken(StompHeaderAccessor accessor) {
        // 1. Intentar extraer de Header Authorization (Estándar STOMP/Angular)
        String authHeader = accessor.getFirstNativeHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }

        // 2. Intentar extraer de Cookies (Tu script de Node actual)
        List<String> cookies = accessor.getNativeHeader("Cookie");
        if (cookies != null && !cookies.isEmpty()) {
            return cookies.stream()
                    .flatMap(cookie -> Arrays.stream(cookie.split(";")))
                    .map(String::trim)
                    .filter(c -> c.startsWith("jwt="))
                    .map(c -> c.substring(4))
                    .findFirst()
                    .orElse(null);
        }

        return null;
    }
}