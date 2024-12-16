package anh.nguyen.alovestory.controllers;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import anh.nguyen.alovestory.DTO.CommentDTO;
import anh.nguyen.alovestory.entities.Comment;
import anh.nguyen.alovestory.entities.Post;
import anh.nguyen.alovestory.entities.User;
import anh.nguyen.alovestory.repositories.PostRepository;
import anh.nguyen.alovestory.repositories.UserRepository;
import anh.nguyen.alovestory.services.CommentService;
import anh.nguyen.alovestory.services.PostService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/comment")
public class CommentController {
    @Autowired
    PostService postService;
    @Autowired
    PostRepository postRepository;
    @Autowired
    CommentService commentService;
    @Autowired
    UserRepository userRepository;

    @GetMapping("/getPostComment/{postId}")
    public List<Comment> getPostComments(@RequestParam Long postId) {
        List<Comment> listComments = commentService.findCommentInPost(postId);
        return listComments;
    }

    @GetMapping("/all")
    public List<Comment> getAllComment() {
        return commentService.findAllComments();
    }

    @DeleteMapping("/delete/{commentId}")
    public ResponseEntity<?> deleteComment(@PathVariable Long commentId) {
        if (commentService.commentExist(commentId)) {
            commentService.deleteComment(commentId);
            return new ResponseEntity<>("Delete Completed", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Delete Fail", HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/new")
    public ResponseEntity<?> createComment(@RequestBody CommentDTO commentDTO) {
        // Lấy User từ userId trong commentDTO
        Optional<User> userOptional = userRepository.findById(commentDTO.getUserId());
        if (!userOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        // Lấy Post từ postId trong commentDTO
        Optional<Post> postOptional = postRepository.findById(commentDTO.getPostId());
        if (!postOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Post not found");
        }

        // Tạo Comment mới
        Comment comment = new Comment();
        comment.setCommentText(commentDTO.getCommentText());
        comment.setUser(userOptional.get()); // Gán User đã tìm được vào Comment
        comment.setPost(postOptional.get()); // Gán Post đã tìm được vào Comment
        comment.setCommentTime(LocalDateTime.now()); // Đặt thời gian hiện tại làm commentTime

        // Lưu comment vào database
        commentService.createComment(comment);
        return ResponseEntity.status(HttpStatus.CREATED).body("Comment created successfully");
    }
}
