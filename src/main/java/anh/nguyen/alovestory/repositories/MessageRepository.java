package anh.nguyen.alovestory.repositories;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import anh.nguyen.alovestory.entities.Message;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findBySender_UserIdAndRecipient_UserIdOrSender_UserIdAndRecipient_UserIdOrderByMessageTimeAsc(
            Long senderId1, Long recipientId1, Long senderId2, Long recipientId2, Pageable pageable);
}
