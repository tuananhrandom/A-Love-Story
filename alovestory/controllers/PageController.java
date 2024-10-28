package anh.nguyen.alovestory.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.ui.Model;

import anh.nguyen.alovestory.entities.Comment;
import anh.nguyen.alovestory.entities.Post;
import anh.nguyen.alovestory.services.CommentService;
import anh.nguyen.alovestory.services.PostService;
import anh.nguyen.alovestory.services.UserService;

import org.springframework.web.bind.annotation.GetMapping;

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
        List<Post> posts = postService.getAllPost();
        model.addAttribute("posts", posts);

        // Tạo một Map để lưu bình luận theo bài viết
        Map<Long, List<Comment>> commentsByPostId = new HashMap<>();

        for (Post post : posts) {
            List<Comment> comments = commentService.findCommentInPost(post.getPostId());
            commentsByPostId.put(post.getPostId(), comments);
            System.out.println("Post ID: " + post.getPostId() + ", Comments: " + comments);
        }

        model.addAttribute("commentsByPostId", commentsByPostId);
        return "home";
    }
}
