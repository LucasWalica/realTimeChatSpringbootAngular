package chatrealtime.demo.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Service
public class AIService {

    private final WebClient webClient;

    public AIService(WebClient.Builder webClientBuilder, @Value("${openrouter.api.key}") String apiKey) {
        this.webClient = webClientBuilder
                .baseUrl("https://openrouter.ai/api/v1") // Esta es la base correcta
                .build();

        // Guardamos la key limpia
        this.apiKeyStr = apiKey.trim();
    }

    private final String apiKeyStr;

    @Async
    public CompletableFuture<String> generateResponse(String userPrompt) {
        System.err.println("[IA-LOG] Enviando a OpenRouter...");

        return webClient.post()
                .uri("/chat/completions") // Se une a la baseUrl
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKeyStr)
                .header("HTTP-Referer", "http://localhost:8080")
                .header("X-Title", "ChatLucas")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(Map.of(
                        "model", "allenai/olmo-3.1-32b-think:free",
                        "messages", List.of(Map.of("role", "user", "content", userPrompt))
                ))
                .retrieve()
                .bodyToMono(Map.class)
                .map(response -> {
                    List choices = (List) response.get("choices");
                    Map firstChoice = (Map) choices.get(0);
                    Map message = (Map) firstChoice.get("message");
                    return (String) message.get("content");
                })
                .doOnError(e -> System.err.println("[IA-LOG] FALLO: " + e.getMessage()))
                .onErrorReturn("Error de conexión.")
                .toFuture();
    }
}