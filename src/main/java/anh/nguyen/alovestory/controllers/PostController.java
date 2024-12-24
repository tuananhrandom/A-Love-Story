package anh.nguyen.alovestory.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.data.domain.Pageable;
import java.time.LocalDateTime;
import anh.nguyen.alovestory.entities.Post;
import anh.nguyen.alovestory.services.PostService;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequestMapping("/post")
public class PostController {

    @Autowired
    PostService postService;

    // tìm tất cả các post
    @GetMapping("/all")
    public List<Post> getAllPost() {
        return postService.getAllPost();
    }

    // xóa đi một post
    @DeleteMapping("/delete/{postId}")
    public ResponseEntity<?> deletePost(@PathVariable Long postId) {
        if (postService.postExistById(postId)) {
            System.out.println("đã vào đến delete");
            postService.deletePost(postId);
            return new ResponseEntity<>("Delete Complete", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Delete Failed", HttpStatus.BAD_REQUEST);
        }
    }

    // tạo mới một post
    @PostMapping("/new")
    public ResponseEntity<?> newPost(
            @RequestParam("postCaption") String postCaption,
            @RequestParam("userId") Long userId,
            @RequestParam(value = "file", required = false) MultipartFile file) {
        try {
            String fileName = null;
            String postMediaType = null;

            // Only process the file if it's provided
            if (file != null && !file.isEmpty()) {
                String fileType = file.getContentType();
                if (fileType != null) {
                    fileName = postService.saveFile(file);
                    postMediaType = fileType.split("/")[1];
                } else {
                    System.err.println("Cannot get file type");
                }
            }

            // Create new post
            Post post = new Post();
            post.setPostCaption(postCaption);
            post.setUser(postService.findUserById(userId));
            post.setPostMedia(fileName); // This will be null if no file is uploaded
            post.setPostMediaType(postMediaType); // This will also be null if no file is uploaded
            post.setCreatedAt(LocalDateTime.now());
            post.setLikeCount(0);
            post.setCommentCount(0);

            postService.createNewPost(post);
            return new ResponseEntity<>("Create success", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Create Failed: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // tăng like cho bài post
    @PutMapping("like/{postId}")
    public ResponseEntity<?> likePost(@PathVariable Long postId) {
        postService.likePost(postId);
        return new ResponseEntity<>("Liked", HttpStatus.OK);
    }

    // get 10 bài post một
    @GetMapping("/posts")
    @ResponseBody
    public List<Post> getPosts(@RequestParam int page, @RequestParam int size) {
        System.out.println("trang so: " + page + ",So luong:" + size);
        Pageable pageable = PageRequest.of(page, size);
        return postService.getAllPosts(pageable); // Adjust to fetch paginated posts
    }

    @GetMapping("/{postId}")
    public Post getPostById(@PathVariable Long postId) {
        return postService.findByPostId(postId);
    }
}
