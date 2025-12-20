package chatrealtime.demo.dto;

import lombok.Data;

@Data
public class UserResponseDTO {
    private Long id;
    private String username;
    private  String email;
    private boolean online;
    private String avatar;
}
