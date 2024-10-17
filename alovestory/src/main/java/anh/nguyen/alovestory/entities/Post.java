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
    @Column(name="postMedia", nullable = true, unique = false)
    private String postMedia;
    @Column(name="postPicture", nullable = true, unique = false)
    private String postMediaType;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId", nullable = false)
    private User user;
    @Column(name="createdAt", nullable = false)
    private LocalDateTime createdAt;
    @Column(name="postLiked", nullable = false, unique =false)
    private Integer postLiked = 0;
    @Column(name = "commentCount", nullable = false)
    private Integer commentCount; 
}
