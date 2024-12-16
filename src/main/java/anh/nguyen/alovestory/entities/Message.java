package anh.nguyen.alovestory.entities;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
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

@Getter
@Setter
@Entity
@Table(name = "messages")
public class Message {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long messageId;
    @Column(name = "messageText", nullable = false, unique = false)
    private String messageText;
    @Column(name = "messageMedia", nullable = true, unique = false)
    private String messageMedia;
    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.NO_ACTION)
    @JoinColumn(name = "senderId", nullable = false)
    User sender;
    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.NO_ACTION)
    @JoinColumn(name = "recipientId", nullable = false)
    User recipient;
    @Column(name = "messageTime", nullable = false)
    private LocalDateTime messageTime; // Thời gian bình luận
    @Column(name = "messageMediaType", nullable = true)
    private String messageMediaType;

    public String getDateCreate() {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return messageTime.format(dateFormatter);
    }

    // Phương thức getter cho timeCreate
    public String getTimeCreate() {
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        return messageTime.format(timeFormatter);
    }

}
