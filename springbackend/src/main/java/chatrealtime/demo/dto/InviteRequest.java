package chatrealtime.demo.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record InviteRequest(
        @NotBlank(message = "El código no puede estar vacío")
        @Size(min = 4, max = 20, message = "El código debe tener una longitud válida")
        String inviteCode
) {}