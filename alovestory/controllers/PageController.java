package anh.nguyen.alovestory.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

// import org.hibernate.mapping.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.ui.Model;

import anh.nguyen.alovestory.DTO.CommentDTO;
import anh.nguyen.alovestory.DTO.CommentDTOAlter;
import anh.nguyen.alovestory.entities.Comment;
import anh.nguyen.alovestory.entities.CustomUserDetails;
import anh.nguyen.alovestory.entities.Post;
import anh.nguyen.alovestory.entities.User;
import anh.nguyen.alovestory.services.CommentService;
import anh.nguyen.alovestory.services.PostService;
import anh.nguyen.alovestory.services.UserService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import java.util.stream.Collectors;

@Controller
@RequestMapping("")
public class PageController {
    @Autowired
    PostService postService;
    @Autowired
    UserService userService;
    @Autowired
    CommentService commentService;

    // @GetMapping("/home")
    // public String getHomePage(Model model) {
    // List<Post> posts = postService.getAllPost();
    // model.addAttribute("posts", posts);
    // List<Comment> comments = commentService.findAllComments();
    // model.addAttribute("comments", comments);
    // return "home";
    // }
    @GetMapping("/home")
    public String getHomePage(Model model) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            System.out.println("Authentication: " + authentication);
            if (authentication.isAuthenticated() && authentication.getPrincipal() instanceof CustomUserDetails) {
                CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
                System.out.println("User details: " + userDetails);
                model.addAttribute("currentUser", userDetails);
            }

            List<Post> posts = postService.getAllPost();
            model.addAttribute("posts", posts);
            // comment
            Map<Long, List<Comment>> commentsByPostId = new HashMap<>();
            for (Post post : posts) {
                List<Comment> comments = commentService.findCommentInPost(post.getPostId());
                commentsByPostId.put(post.getPostId(), comments);
                System.out.println("Post ID: " + post.getPostId() + ", Comments: " + comments);
            }
            System.out.println("commentsByPostId Map: " + commentsByPostId);
            model.addAttribute("commentsByPostId", commentsByPostId);

            return "home";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("errorMessage", "An error occurred while loading the page.");
            return "error";
        }
    }

    @GetMapping("/login")
    public String getLogin() {
        // userService.updatePassword(Integer.toUnsignedLong(1), "miucon91");
        return "login";
    }

}
