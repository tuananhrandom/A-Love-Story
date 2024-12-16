package anh.nguyen.alovestory.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

// import org.hibernate.mapping.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.ui.Model;

import anh.nguyen.alovestory.entities.Comment;
import anh.nguyen.alovestory.entities.CustomUserDetails;
import anh.nguyen.alovestory.entities.Notification;
import anh.nguyen.alovestory.entities.Post;
import anh.nguyen.alovestory.entities.User;
import anh.nguyen.alovestory.services.CommentService;
import anh.nguyen.alovestory.services.NotificationService;
import anh.nguyen.alovestory.services.PostService;
import anh.nguyen.alovestory.services.SseService;
import anh.nguyen.alovestory.services.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Controller
@RequestMapping("")
public class PageController {
    @Autowired
    PostService postService;
    @Autowired
    UserService userService;
    @Autowired
    CommentService commentService;
    @Autowired
    NotificationService notificationService;
    @Autowired
    SseService sseService;

    // trả về trang home
    @GetMapping("/home")
    public String getHomePage(Model model) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication.isAuthenticated() && authentication.getPrincipal() instanceof CustomUserDetails) {
                CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
                User thisUser = userService.findUserById(userDetails.getUserId());
                model.addAttribute("currentUser", thisUser);
            }
            return "home";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("errorMessage", "An error occurred while loading the page.");
            return "error";
        }
    }

    // trả về trang login
    @GetMapping("/login")
    public String getLogin() {
        // userService.updatePassword(Integer.toUnsignedLong(1), "miucon91");
        return "login";
    }

    // trả về các post
    @GetMapping("/render/post")
    public String getPostRender(@RequestParam Long postId, @RequestParam Long currentUserId, Model model) {
        Post post = postService.findByPostId(postId);
        model.addAttribute("post", post);
        model.addAttribute("currentUserId", currentUserId); // Set the current user
        Map<Long, List<Comment>> commentsByPostId = new HashMap<>();
        List<Comment> comments = commentService.findCommentInPost(post.getPostId());
        commentsByPostId.put(post.getPostId(), comments);
        model.addAttribute("commentsByPostId", commentsByPostId);
        return "fragments/postFragment"; // Return the fragment
    }

    // trả về các thông báo
    @GetMapping("/render/notification")
    public String getNotificationRender(@RequestParam Long notificationId, @RequestParam Long currentUserId,
            Model model) {
        Notification notification = notificationService.findNotificationById(notificationId);
        System.out.println(notification.toString());
        model.addAttribute("notifications", notification);
        return "fragments/notificationFragment";
    }

    // trả về tin nhắn
    @GetMapping("/render/message")
    public String getMessages(@RequestParam Long messageId, @RequestParam Long senderId,
            @RequestParam Long recipientId) {

        return new String();
    }

    // đường sse
    @GetMapping(value = "/stream/{userId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamNotification(@PathVariable Long userId) {
        return sseService.createRecipientEmitter(userId);
    }

    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamComment() {
        return sseService.createSseEmitter();
    }

}
