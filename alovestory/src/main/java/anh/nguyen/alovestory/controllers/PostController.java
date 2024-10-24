package anh.nguyen.alovestory.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import java.time.LocalDateTime;
import anh.nguyen.alovestory.entities.Post;
import anh.nguyen.alovestory.services.PostService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/post")
public class PostController {
    private static String UPLOAD_DIR = "uploads/";

    @Autowired
    PostService postService;

    @GetMapping("/all")
    public List<Post> getAllPost() {
        return postService.getAllPost();
    }

    @DeleteMapping("/delete/{postId}")
    public ResponseEntity<?> deletePost(@RequestParam Long postId) {
        if (postService.postExistById(postId)) {
            postService.deletePost(postId);
            return new ResponseEntity<>("Delete Complete", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Delete Failed", HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/new")
    public ResponseEntity<?> newPost(@RequestParam("postCaption") String postCaption,
            @RequestParam("userId") Long userId,
            @RequestParam("file") MultipartFile file) {
        try {
            // Lưu file lên server
            String fileName = saveFile(file);

            // Tạo post mới
            Post post = new Post();
            post.setPostCaption(postCaption);
            post.setUser(postService.findUserById(userId));
            post.setPostMedia(fileName); // Lưu đường dẫn của file vào DB
            post.setCreatedAt(LocalDateTime.now());
            post.setLikeCount(0);
            post.setCommentCount(0);

            boolean created = postService.createNewPost(post);
            if (created) {
                return new ResponseEntity<>("Created", HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Create Failed", HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            return new ResponseEntity<>("Create Failed: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private String saveFile(MultipartFile file) throws IOException {
        // Tạo thư mục upload nếu chưa có
        Path uploadPath = Paths.get(UPLOAD_DIR);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Tạo tên file và lưu vào thư mục
        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        Path filePath = uploadPath.resolve(fileName);
        Files.copy(file.getInputStream(), filePath);

        // Trả về tên file để lưu vào DBss
        return fileName;
    }
}
