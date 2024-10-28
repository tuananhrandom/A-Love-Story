package anh.nguyen.alovestory.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.format.annotation.DateTimeFormat;

@Entity
@Getter
@Setter
@Table(name = "post")
public class Post {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long postId;
    @Column(name = "postCaption", nullable = true, unique = false)
    private String postCaption;
    @Column(name = "postMedia", nullable = true, unique = false) // media url
    private String postMedia;
    @Column(name = "postMediatype", nullable = true, unique = false)
    private String postMediaType;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "userId", nullable = false)
    private User user;
    @Column(name = "createdAt", nullable = false)
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    private LocalDateTime createdAt;
    @Column(name = "likeCount", nullable = false, unique = false)
    private Integer likeCount = 0;
    @Column(name = "commentCount", nullable = false)
    private Integer commentCount = 0;

    public String getDateCreate() {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return createdAt.format(dateFormatter);
    }

    // Phương thức getter cho timeCreate
    public String getTimeCreate() {
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        return createdAt.format(timeFormatter);
    }
}
