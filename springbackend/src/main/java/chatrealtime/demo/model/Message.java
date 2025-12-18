package chatrealtime.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false)
    private String type;

    private LocalDateTime timestamp;

    @ManyToOne
    @JoinColumn(name="sender_id")
    private User sender;

    @ManyToOne
    @JoinColumn(name="room_id")
    @JsonIgnore
    private Room room;


}
