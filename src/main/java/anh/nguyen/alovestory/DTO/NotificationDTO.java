package anh.nguyen.alovestory.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NotificationDTO {
    private String notificationImage;
    private String notificationTitle;
    private String notificationContent;
    private Long postId;
    private Long actorId; // ID of the user who sends the notification
    private Long recipientId; // ID of the user who receives the notification
    private Boolean isRead;
    // private Long partnerId;
}
