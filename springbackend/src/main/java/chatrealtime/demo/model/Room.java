package chatrealtime.demo.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.Set;
@Entity
@Table(name="rooms")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private boolean isGroup;

    @ManyToMany
    @JoinTable(
            name="room_members",
            joinColumns = @JoinColumn(name="room_id"),
            inverseJoinColumns = @JoinColumn(name="user_id")
    )
    private Set<User> members;

    @OneToMany(mappedBy = "room", cascade=CascadeType.ALL)
    private Set<Message> messages;
}
