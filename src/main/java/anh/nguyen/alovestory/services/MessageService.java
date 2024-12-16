package anh.nguyen.alovestory.services;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import anh.nguyen.alovestory.DTO.MessageDTO;
import anh.nguyen.alovestory.entities.Message;
import anh.nguyen.alovestory.entities.User;
import anh.nguyen.alovestory.repositories.MessageRepository;
import anh.nguyen.alovestory.repositories.UserRepository;

@Service
public class MessageService {
    String MESS_UPLOAD_DIR = "E:/uploads/message";
    @Autowired
    MessageRepository messageRepository;
    @Autowired
    UserRepository userRepository;

    // tạo mới một message
    public void newMessage(MultipartFile file, MessageDTO messageDTO) {
        Message message = new Message();
        try {
            String filename = null;
            String messageMediaType = null;
            if (file != null && !file.isEmpty()) {
                filename = saveFile(file);
                String fileType = file.getContentType();
                if (fileType != null) {
                    messageMediaType = fileType.split("/")[1];
                } else {
                    System.err.println("Cannot get file type");
                }
            }
            message.setMessageMediaType(messageMediaType);
            message.setMessageText(messageDTO.getMessageText());
            message.setMessageTime(LocalDateTime.now());
            User sender = userRepository.findById(messageDTO.getSenderId()).get();
            User recipient = userRepository.findById(messageDTO.getRecipientId()).get();
            message.setSender(sender);
            message.setRecipient(recipient);
            message.setMessageMedia(filename);
            messageRepository.save(message);
        } catch (Exception e) {
            System.err.println("Error while creating message" + e.getMessage());
        }
    }

    public String saveFile(MultipartFile file) {
        Path uploadPath = Paths.get(MESS_UPLOAD_DIR);
        if (!Files.exists(uploadPath)) {
            try {
                Files.createDirectories(uploadPath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        Path filePath = uploadPath.resolve(fileName);
        try {
            Files.copy(file.getInputStream(), filePath);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        // Trả về tên file để lưu vào DBss
        return fileName;
    }

    // lấy về message
    public Message findByMessageId(Long messageId) {
        return messageRepository.findById(messageId).get();
    }

    // lấy về message của một cuộc hội thoại giữa 2 người.
    public List<Message> findWholeConversation(Long senderId, Long recipientId, Pageable pageable) {
        return messageRepository
                .findBySender_UserIdAndRecipient_UserIdOrSender_UserIdAndRecipient_UserIdOrderByMessageTimeAsc(senderId,
                        recipientId, recipientId, senderId, pageable);
    }

    // xóa một message
    public void deleteByMessageId(Long messageId) {
        try {
            messageRepository.deleteById(messageId);
        } catch (Exception e) {
            System.err.println("Error while deleting message:" + e.getMessage());
        }
    }

}
