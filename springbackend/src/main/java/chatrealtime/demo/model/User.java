package chatrealtime.demo.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.Set;

@Entity
@Table(name="users")
@Getter @Setter @AllArgsConstructor @NoArgsConstructor @Builder
public class User implements UserDetails {
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


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(); // Devuelve una lista vacía (sin roles por ahora)
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // Importante: que sea true
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // Importante: que sea true
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // Importante: que sea true
    }

    @Override
    public boolean isEnabled() {
        return true; // Importante: que sea true
    }
}