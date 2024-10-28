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

@Entity
@Getter
@Setter
@Table(name = "comment")
public class Comment {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long commentId;
    @Column(name = "commentText", nullable = false, unique = false)
    private String commentText;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "userId", nullable = false)
    User user;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "postId", nullable = false)
    private Post post;
    @Column(name = "commentTime", nullable = false)
    private LocalDateTime commentTime; // Thời gian bình luận

    public String getDateCreate() {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return commentTime.format(dateFormatter);
    }

    // Phương thức getter cho timeCreate
    public String getTimeCreate() {
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        return commentTime.format(timeFormatter);
    }
}
