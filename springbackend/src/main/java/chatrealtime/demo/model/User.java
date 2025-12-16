package chatrealtime.demo.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.Set;

@Entity
@Table(name="users")
@Getter @Setter @AllArgsConstructor @NoArgsConstructor @Builder
public class User{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable=false)
    private String username;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    private String avatar;
    private boolean online;

    @ManyToMany(mappedBy = "members")
    private Set<Room> rooms;


}