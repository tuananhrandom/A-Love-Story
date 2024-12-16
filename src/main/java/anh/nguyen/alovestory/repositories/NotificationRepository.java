package anh.nguyen.alovestory.repositories;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import anh.nguyen.alovestory.entities.Notification;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    Page<Notification> findAllByRecipient_UserIdOrderByCreatedAtDesc(Long recipientId, Pageable pageable);
}
