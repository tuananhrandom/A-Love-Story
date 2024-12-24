package anh.nguyen.alovestory.services;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


import anh.nguyen.alovestory.DTO.NotificationDTO;
import anh.nguyen.alovestory.entities.Notification;
import anh.nguyen.alovestory.entities.Post;
import anh.nguyen.alovestory.entities.User;
import anh.nguyen.alovestory.repositories.NotificationRepository;
import anh.nguyen.alovestory.repositories.PostRepository;
import anh.nguyen.alovestory.repositories.UserRepository;

@Service
public class NotificationService {

    @Autowired
    SseService sseService;
    @Autowired
    NotificationRepository notificationRepository;

    @Autowired
    UserRepository userRepository;
    @Autowired
    PostRepository postRepository;

    public Notification findNotificationById(Long notificationId) {
        return notificationRepository.findById(notificationId).get();
    }

    public void createNotification(NotificationDTO dto) {
        Notification notification = new Notification();
        notification.setNotificationImage(dto.getNotificationImage());
        notification.setNotificationTitle(dto.getNotificationTitle());
        String shortentContent = shortenCaption(dto.getNotificationContent(), 100);
        notification.setNotificationContent(shortentContent);
        notification.setIsRead(dto.getIsRead());
        notification.setCreatedAt(LocalDateTime.now());

        // Fetch and set the actor and recipient users
        User actor = userRepository.findById(dto.getActorId())
                .orElseThrow(() -> new RuntimeException("Actor not found"));
        User recipient = userRepository.findById(dto.getRecipientId())
                .orElseThrow(() -> new RuntimeException("Recipient not found"));
        notification.setActor(actor);
        if (dto.getActorId() != dto.getRecipientId()) {
            notification.setRecipient(recipient);
            addUnreadNoti(recipient.getUserId());
            sseService.sendSseEventToRecipient(recipient.getUnreadNoti(), recipient, "new-unreadNoti");
        } else {
            notification.setRecipient(actor.getPartnerUser());
            addUnreadNoti(actor.getPartnerUser().getUserId());
            sseService.sendSseEventToRecipient(actor.getPartnerUser().getUnreadNoti(), actor.getPartnerUser(),
                    "new-unreadNoti");
        }

        // Optionally, fetch the post if the postId is not null
        if (dto.getPostId() != null) {
            Post post = postRepository.findById(dto.getPostId()).orElse(null);
            notification.setPost(post);
        }
        notificationRepository.save(notification);
        

    }

    // tăng một thông báo cho người dùng nhận được thông báo.
    public void addUnreadNoti(Long userId) {
        User thisUser = userRepository.findById(userId).get();
        Integer unreadNoti = thisUser.getUnreadNoti() + 1;
        thisUser.setUnreadNoti(unreadNoti);
        userRepository.save(thisUser);
    }

    // giamr một thông báo cho người dùng nhận được thông báo.
    public void subUnreadNoti(Long userId) {
        User thisUser = userRepository.findById(userId).get();
        Integer unreadNoti = thisUser.getUnreadNoti() - 1;
        thisUser.setUnreadNoti(unreadNoti);
        userRepository.save(thisUser);
    }

    // Set to 0 thông báo cho người dùng nhận được thông báo.
    public void resetUnreadNoti(Long userId) {
        User thisUser = userRepository.findById(userId).get();
        thisUser.setUnreadNoti(0);
        userRepository.save(thisUser);
    }

    // lấy về tất cả thog báo
    public List<Notification> findAllNotification() {
        return notificationRepository.findAll();
    }

    // Lấy về Notication của một User và chỉ lấy 10 thông báo / lần
    public Page<Notification> findNotificationsByReceipentId(Long recipientId, Pageable pageable) {
        return notificationRepository.findAllByRecipient_UserIdOrderByCreatedAtDesc(recipientId, pageable);
    }

    // xóa một notification
    public void deleteByNotificationId(Long notificationId) {
        try {
            Long thisUserId = notificationRepository.findById(notificationId).get().getRecipient().getUserId();
            notificationRepository.deleteById(notificationId);
            subUnreadNoti(thisUserId);

        } catch (Exception e) {
            System.err.println("Loi khi xoa notificaion");
        }
    }

    public String shortenCaption(String caption, int maxLength) {
        if (caption.length() > maxLength) {
            return caption.substring(0, maxLength) + "...";
        }
        return caption;
    }

    public void changeIsRead(Long commentId) {
        Notification thisNotification = notificationRepository.findById(commentId).get();
        if (!thisNotification.getIsRead()) {
            thisNotification.setIsRead(true);
            notificationRepository.save(thisNotification);
        }
    }

}
