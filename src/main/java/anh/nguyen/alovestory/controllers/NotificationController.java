package anh.nguyen.alovestory.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import anh.nguyen.alovestory.DTO.NotificationDTO;
import anh.nguyen.alovestory.entities.Notification;
import anh.nguyen.alovestory.services.NotificationService;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@RestController
@RequestMapping("/notification")
public class NotificationController {

    @Autowired
    NotificationService notificationService;

    @GetMapping("/all")
    public List<Notification> getAll() {
        return notificationService.findAllNotification();
    }

    // tim moi lan 10 theo receipentId
    @GetMapping("/notifications")
    @ResponseBody
    public Page<Notification> getNotificationsByReceipents(@RequestParam Long recipientId, @RequestParam int page,
            @RequestParam int size) {
        Pageable pageable = PageRequest.of(page, size);
        return notificationService.findNotificationsByReceipentId(recipientId, pageable);
    }

    // tao moi mot notification
    @PostMapping("/new")
    public ResponseEntity<?> newNotification(@RequestBody NotificationDTO notificationDTO) {
        try {
            notificationService.createNotification(notificationDTO);
            return new ResponseEntity<>("New Notification", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Failed" + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/delete/{notificationId}")
    public ResponseEntity<?> deleteByNotificationId(@PathVariable Long notificationId) {
        try {
            notificationService.deleteByNotificationId(notificationId);
            return new ResponseEntity<>("Success", HttpStatus.OK);
        } catch (Exception error) {
            return new ResponseEntity<>("Failed", HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("reset/{userId}")
    public ResponseEntity<?> resetUnreadNotification(@PathVariable Long userId) {
        try {
            notificationService.resetUnreadNoti(userId);
            return new ResponseEntity<>("Reset success", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Reset Fail: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }

    }

    @PutMapping("read/{notificationId}")
    public ResponseEntity<?> updateIsRead(@PathVariable Long notificationId) {
        try {
            notificationService.changeIsRead(notificationId);
            return new ResponseEntity<>("Read", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("ERROR", HttpStatus.BAD_REQUEST);
        }

    }

}
