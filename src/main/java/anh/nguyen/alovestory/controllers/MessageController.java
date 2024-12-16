package anh.nguyen.alovestory.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import anh.nguyen.alovestory.DTO.MessageDTO;
import anh.nguyen.alovestory.entities.Message;
import anh.nguyen.alovestory.services.MessageService;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/message")
public class MessageController {
    @Autowired
    MessageService messageService;

    @GetMapping("/conversation")
    public List<Message> getConversation(@RequestParam Long senderId, Long recipientId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return messageService.findWholeConversation(senderId, recipientId, pageable);
    }

    @PostMapping("/new")
    public ResponseEntity<?> postNewMessage(@RequestParam(value = "file", required = false) MultipartFile file,
            @RequestBody MessageDTO messageDTO) {
        try {
            messageService.newMessage(file, messageDTO);
            return new ResponseEntity<>("OK", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Message Fail", HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/delete/{messageId}")
    public ResponseEntity<?> deleteByNotificationId(@PathVariable Long messageId) {
        try {
            messageService.deleteByMessageId(messageId);
            return new ResponseEntity<>("Success", HttpStatus.OK);
        } catch (Exception error) {
            return new ResponseEntity<>("Failed", HttpStatus.BAD_REQUEST);
        }
    }

}
