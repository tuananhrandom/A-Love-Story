package anh.nguyen.alovestory.entities;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.fasterxml.jackson.annotation.JsonIgnore;

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

@Entity
@Getter
@Setter
@Table(name = "notifications")
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long notificationId;
    @Column(name = "notificationimage", nullable = false)
    private String notificationImage;
    @Column(name = "notificationtitle", nullable = false)
    private String notificationTitle;
    @Column(name = "notificationcontent", nullable = true)
    private String notificationContent;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "postId", nullable = false)
    private Post post;
    @ManyToOne(fetch = FetchType.LAZY)
    // đây là người gửi thông báo này
    @JoinColumn(name = "actorId", nullable = false)
    @JsonIgnore
    private User actor;
    // đây sẽ là những người nhận được thông báo, thế này sẽ phù hợp hơn với hệ
    // thống sau này nếu có nâng cấp lên phục vụ nhiều người dùng hơn
    @ManyToOne
    @JoinColumn(name = "recipientId", nullable = false)
    @JsonIgnore
    private User recipient;
    @Column(name = "isRead", nullable = false)
    Boolean isRead;
    @Column(name = "createdAt", nullable = false)
    private LocalDateTime createdAt;

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
