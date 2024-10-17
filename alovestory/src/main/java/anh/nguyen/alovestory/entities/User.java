package anh.nguyen.alovestory.entities;

import java.util.List;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table (name ="users")
@Getter
@Setter
public class User {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long userId;
    @Column(name="username", unique = true, nullable = false)
    private String username;
    @Column(name = "password", nullable = false, unique = false)
    private String password;
    @Column(name = "avatar", nullable = false, unique = false)
    private String avatar;
    @Column(name = "name", nullable = false, unique = false)
    private String name;
        // Mối quan hệ OneToMany với Post
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<Post> posts;
}
