package chatrealtime.demo.dto;

import lombok.Data;

@Data
public class MessageRequestDTO {
    private String content;
    private Long senderId;
}
