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
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

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
                .setAllowedOriginPatterns("http://localhost:4200")
                .addInterceptors(new HttpSessionHandshakeInterceptor())
                .withSockJS();
    }
    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                // Borra todo lo que hay dentro y solo pon esto:
                return message;
            }
        });
    }

    private String extractToken(StompHeaderAccessor accessor) {
        // 1. Intentar extraer de Header Authorization (Lo más fiable si lo mandas desde Angular)
        String authHeader = accessor.getFirstNativeHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            System.out.println("✅ Token encontrado en Header Authorization");
            return token;
        }

        // 2. Intentar extraer de las Cookies nativas del Frame STOMP
        List<String> nativeCookies = accessor.getNativeHeader("Cookie");
        if (nativeCookies != null && !nativeCookies.isEmpty()) {
            String token = nativeCookies.stream()
                    .flatMap(cookie -> Arrays.stream(cookie.split(";")))
                    .map(String::trim)
                    .filter(c -> c.startsWith("jwt="))
                    .map(c -> c.substring(4))
                    .findFirst()
                    .orElse(null);
            if (token != null) {
                System.out.println("✅ Token encontrado en Native Header Cookies");
                return token;
            }
        }

        // 3. Intentar extraer de los Atributos de Sesión (HttpSessionHandshakeInterceptor)
        // Cuando usas .addInterceptors(new HttpSessionHandshakeInterceptor()) en la config,
        // Spring a veces mueve las cookies o la sesión aquí.
        var sessionAttributes = accessor.getSessionAttributes();
        if (sessionAttributes != null && sessionAttributes.containsKey("jwt")) {
            System.out.println("✅ Token encontrado en Session Attributes");
            return (String) sessionAttributes.get("jwt");
        }

        System.err.println("❌ No se encontró ningún token en la petición WebSocket");
        return null;
    }
}